import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * TODO Create a QueryBuilderInterface with a default implementation of 
 * the build(Path) method. Then implement that interface in both classes.
 */

/**
 * @author tony
 * 
 *         Thread Safe QueryBuilder
 */
public class ThreadedQueryBuilder {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> resultsMap;

	/**
	 * The data structure with the stored information from the text files.
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Logger for the class.
	 */
	private static final Logger log = LogManager.getLogger();

	/**
	 * The WorkQueue for the class.
	 */
	private final WorkQueue workQueue;

	/**
	 * Constructor method
	 * 
	 * @param index     - InvertedIndex that will store information
	 * @param workQueue - the WorkQueue for the class
	 */
	public ThreadedQueryBuilder(ThreadSafeInvertedIndex index, WorkQueue workQueue) {
		resultsMap = new TreeMap<>();
		this.index = index;
		this.workQueue = workQueue;
	}

	/**
	 * Reads the query files. It handles both exact and partial searches.
	 * 
	 * @param path    : the path that has the query values
	 * @param partial : whether or not to perform partial search
	 * @throws IOException           : file couldn't be found
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public void build(Path path, boolean partial) throws FileNotFoundException, IOException, InterruptedException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isBlank()) {
					try {
						workQueue.execute(new task(line, partial));
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.debug("started execute");
				}
			}
			try {
				workQueue.finish();
			} catch (InterruptedException e) {
				throw e;
			}
		}
	}

	/**
	 * Initiates the search of queries and stores it into the data structure.
	 * 
	 * @param line    : the query line
	 * @param partial : whether or not to perform partial search
	 */
	public void searchQuery(String line, boolean partial) {
		// TODO Just add a new task for this line
		// TODO Move this implementation into run()
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		if (stems.isEmpty()) {
			return;
		}
		String joined = String.join(" ", stems);
		if (resultsMap.containsKey(joined)) { // TODO Must protect this read of resultsMap
			return;
		}

		ArrayList<InvertedIndex.Result> tempList = index.generateSearch(stems, partial);

		synchronized (resultsMap) {
			// TODO Search is inside of here, so multiple threads cnanot search at the same time
			resultsMap.put(joined, tempList);
			// resultsMap.put(joined, index.generateSearch(stems, partial));
		}


		/* TODO
		something = index.generateSearch(stems, partial);
		
		synchronized (resultsMap) {
			resultsMap.put(joined, something);
		}
		*/
	}

	/**
	 * The writer used for our queries.
	 * 
	 * @param path - the output path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		// TODO Everywhere you see resultsMap you need to protect
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.searchOutput(resultsMap, path);
		}
	}

	/**
	 * @author tony
	 *
	 *         The runnable class
	 */
	public class task implements Runnable {
		/**
		 * The Query line the task is handling.
		 */
		private final String line;

		/**
		 * Whether or not to do partial search
		 */
		private final boolean partial;

		/**
		 * Constructor method for task.
		 * 
		 * @param line    - the query line.
		 * @param partial - whether or not to perform partial search
		 */
		public task(String line, boolean partial) {
			this.line = line;
			this.partial = partial;
		}

		@Override
		public void run() {
				searchQuery(line, partial);
		}
	}
}
