import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Threaded version of Query Builder
 * 
 * @author tony
 * 
 */
public class ThreadedQueryBuilder {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	private final TreeMap<String, ArrayList<ThreadSafeResult>> readyToPrint;

	private static final Logger log = LogManager.getLogger();
	
//	private SimpleReadWriteLock lock;

	/**
	 * The data structure with the stored information from the text files.
	 */
	private final InvertedIndex index;

	private final WorkQueue wq;

	/**
	 * Constructor method
	 * 
	 * @param index
	 */
	public ThreadedQueryBuilder(InvertedIndex index, int numThreads) {
		this.index = index;
		readyToPrint = new TreeMap<>();
		this.wq = new WorkQueue(numThreads);
//		lock = new SimpleReadWriteLock();
	}

	/**
	 * Reads the query files. It handles both exact and partial searches.
	 * 
	 * @param path : the path that has the query values
	 * @param type : exact or partial
	 * @throws IOException           : file couldn't be found
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public void build(String path, String type) throws FileNotFoundException, IOException {
		if (path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".text")) {
			try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
				String line;
//				TreeSet<String> set = new TreeSet<>();
				while ((line = reader.readLine()) != null) {
//					set.clear();
					if (!line.isBlank()) {
//						set.addAll(TextFileStemmer.uniqueStems(line));

						try {
							wq.execute(new task(line, type));
						} catch (Exception e) {
							e.printStackTrace();
						}
						log.debug("started execute");

//						searchQuery(set, type); // this is where you multithread, its sending
						// searchQuery one line at a time
					}
				}
				try {
					wq.finish();
				} catch (InterruptedException e) {
//					 TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public class task implements Runnable {
		private final String line;
		private final String type;

		public task(String line, String type) {
			this.line = line;
			this.type = type;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (wq) {
				searchQuery(line, type);
			}

		}
	}

	public TreeSet<String> getLineStems(String line) {
		TreeSet<String> set = new TreeSet<String>();
		set.addAll(TextFileStemmer.uniqueStems(line));
		return set;
	}
	/**
	 * Initiates the search of queries and stores it into the data structure.
	 * 
	 * @param set  : the set of queries
	 * @param type : exact / partial
	 */
	public void searchQuery(String line, String type) {
		StringBuffer buffer = new StringBuffer();
		if (!line.isEmpty()) {
			TreeSet<String> set = getLineStems(line);
			for (String word : set) {
				buffer.append(word);
				buffer.append(' ');
			}
			// deletes extra space
			if (buffer.length() > 1) {
				buffer.deleteCharAt(buffer.length() - 1);

				synchronized (readyToPrint) {
					// lock.writeLock().lock();
					readyToPrint.put(buffer.toString(), index.generateResults(set, type)); // this is a shared resource,
																							// but
//			lock.writeLock().unlock();
				}
			} // is writing, so might not need to
																				// synchronize
			/*
			 * Search Result is probably where we multithread. If we transfer this to a
			 * different class then we need getter setter methods, change simple json as
			 * well. If we store in the other class find a way to send the info needed.
			 * WHere do we read block? where do we write block?
			 */
		}
	}

//	/**
//	 * Takes the set and adds stems that start with the stems inside it for partial
//	 * search.
//	 * 
//	 * @param set - the set of queries
//	 * @return the same set with newly added queries for partial search
//	 */
//	public TreeSet<String> partialSearch(TreeSet<String> set) {
//		TreeSet<String> returnSet = new TreeSet<>();
//		Iterator<String> stems = set.iterator();
//
//		while (stems.hasNext()) {
//			Iterator<String> iterate = index.getWords().iterator();
//			String stem = stems.next();
//			while (iterate.hasNext()) {
//				String key = iterate.next();
//				if (key.startsWith(stem))
//					returnSet.add(key);
//			}
//		}
//
//		return returnSet;
//	}
//
//	/**
//	 * Creates a list of results based off the queries passed to it and the type of
//	 * search.
//	 * 
//	 * @param set  - set of queries
//	 * @param type - exact/partial
//	 * @return a list of results
//	 */
//	public ArrayList<ThreadSafeResult> generateResults(TreeSet<String> set, String type) {
//		if (type.equals("partial")) {
//			set.addAll(partialSearch(set));
//		}
//
//		ArrayList<ThreadSafeResult> query = new ArrayList<>();
//		for (String word : set) {
//			if (index.contains(word)) {
//				ThreadSafeResult result;
//
//				for (String location : index.getLocations(word)) {
//					int counts = index.getPositions(word, location).size();
//					int totalWords = index.getWordCounts(location);
//					// if we have this result already, then update it
//					boolean contains = false;
//					for (ThreadSafeResult tempResult : query) { // maybe synchronize on tempResult here
//						if (tempResult.getDirectory().equals('"' + location + '"')) { // added quotes so I can simplify
//																						// SJW
//							contains = true;
//							tempResult.add(counts);
//							break;
//						}
//					}
//					if (!contains) {
//						result = new ThreadSafeResult(location, counts, totalWords);
//						query.add(result);
//					}
//				}
//			}
//		}
////		synchronized (query) { // prob don't need this block
//		Collections.sort(query);
////		}
//		return query;
//	}

	/**
	 * The writer used for our queries.
	 * 
	 * @param path - the output path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.searchOutputs(readyToPrint, path);
		}
	}

}
