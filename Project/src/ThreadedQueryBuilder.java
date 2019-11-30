import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author tony
 * 
 *         Thread Safe QueryBuilder
 */
public class ThreadedQueryBuilder implements QueryBuilderInterface {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> resultsMap;

	/**
	 * The data structure with the stored information from the text files.
	 */
	private final ThreadSafeInvertedIndex index;


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
	@Override
	public void build(Path path, boolean partial) throws FileNotFoundException, IOException, InterruptedException {
		QueryBuilderInterface.super.build(path, partial);
		workQueue.finish();
	}

	/**
	 * Initiates the search of queries and stores it into the data structure.
	 * 
	 * @param line    : the query line
	 * @param partial : whether or not to perform partial search
	 */
	@Override
	public void searchQuery(String line, boolean partial) {
		workQueue.execute(new Task(line, partial));
	}

	/**
	 * The writer used for our queries.
	 * 
	 * @param path - the output path
	 * @throws IOException
	 */
	@Override
	public void queryWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			synchronized (resultsMap) {
			SimpleJsonWriter.searchOutput(resultsMap, path);
			}
		}
	}

	/**
	 * @author tony
	 *
	 *         The runnable class
	 */
	public class Task implements Runnable {
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
		public Task(String line, boolean partial) {
			this.line = line;
			this.partial = partial;
		}

		@Override
		public void run() {

			TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
			if (stems.isEmpty()) {
				return;
			}
			String joined = String.join(" ", stems);
			boolean contains;
			synchronized (resultsMap) {
				contains = resultsMap.containsKey(joined);
			}
			if (contains) {
				return;
			}

			ArrayList<InvertedIndex.Result> tempList = index.generateSearch(stems, partial);

			synchronized (resultsMap) {
				resultsMap.put(joined, tempList);
			}
		}
	}
}
