import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {
	
	/**
	 * Driver used to test InvertedIndex.java
	 * 
	 * @param args flag/value pairs used to start this program
	 */

	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();

		ArgumentParser parser = new ArgumentParser();
		parser.parse(args);
		InvertedIndex index = new InvertedIndex();

		ThreadSafeInvertedIndex threadIndex = new ThreadSafeInvertedIndex();
//		if (parser.hasFlag("-threads")) {
//			index = new ThreadSafeInvertedIndex();
//		}

		QueryBuilder queryBuilder;

		if (parser.hasFlag("-threads")) {
			int numThreads;
			try {
				numThreads = Integer.parseInt(parser.getString("-threads", "5"));
			} catch (java.lang.NumberFormatException e) {
				numThreads = 0;
			}

			// ThreadSafeInvertedIndex threadIndex = new ThreadSafeInvertedIndex();
			if (parser.hasFlag("-path")) {
				Path path = parser.getPath("-path");
				try {
					ThreadIndexBuilder.directoryBuilder(path, threadIndex, numThreads);
				} catch (Exception e) {
					System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
							+ " to data structure. Please enter existing paths to textfiles.");
				}
			}
			queryBuilder = new QueryBuilder(threadIndex);
		}

		// if we want it multithreaded, the workqueue must somehow take over driver I
		// think, then create an inverted index biulder that utilizes multi threading.
		// we need to make one thread handle one file, we can use the recursion to get
		// the files
		// then a thread that is ready will pick up a file as we find them.
		// then we need those threads to use the read and write block to make sure they
		// only write to the
		// iverted index once at a time.
		else {
			if (parser.hasFlag("-path")) {
			Path path = parser.getPath("-path");
				try {
					InvertedIndexBuilder.directoryIterator(path, index);
				} catch (Exception e) {
					System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
							+ " to data structure. Please enter existing paths to textfiles.");
				}
			}
			queryBuilder = new QueryBuilder(index);
		}
		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			if (parser.hasFlag("-threads")) {
				try {
					threadIndex.indexWriter(indexPath);
				} catch (Exception e) {
					System.err.println("Invalid output file sent to indexWriter: " + indexPath
							+ ". Please enter a valid output file path name.");
				}
			} else {
			try {
					index.indexWriter(indexPath);
				} catch (Exception e) {
					System.err.println("Invalid output file sent to indexWriter: " + indexPath
							+ ". Please enter a valid output file path name.");
				}
			}
		}
		
		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			if (parser.hasFlag("-threads")) {
				try {
					threadIndex.countsWriter(countsPath);
				} catch (Exception e) {
					System.err.println("Invalid output file sent to indexWriter: " + countsPath
							+ ". Please enter a valid output file path name.");
				}
			} else {
			try {
					index.countsWriter(countsPath);
				} catch (Exception e) {
					System.err.println("Unalbe to write word counts to the path:" + countsPath
							+ ". Please enter a valid output file path name.");
				}
			}
		}

		if (parser.hasFlag("-query")) {
			String name = parser.getString("-query");
			if (name != null) {
				String type = "partial";
				if (parser.hasFlag("-exact"))
					type = "exact";
				try {
					queryBuilder.build(name, type);
				} catch (Exception e) {
					System.err.println("Invlad query file: " + name);//TODO: FIX THIS
					e.printStackTrace();
				}
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsPath = parser.getPath("-results", Path.of("results.json"));
			try {
				queryBuilder.queryWriter(resultsPath);
			} catch (IOException e1) {
				System.err.println("Unable to write results into path : " + resultsPath
						+ ". Please enter a valid output path name.");
			}

		}




		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}

}
