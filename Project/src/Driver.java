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
		QueryBuilder queryBuilder = new QueryBuilder(index);
		InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(index);
		ThreadIndexBuilder threadBuilder;
		ThreadedQueryBuilder threadQueryBuilder = null;
		WorkQueue workQueue = null;

		int numThreads;
		try {
			numThreads = Integer.parseInt(parser.getString("-threads", "5"));
			workQueue = new WorkQueue(numThreads);
		} catch (NumberFormatException e) {
			return;
		}
		if (numThreads < 1) {
			return;
		}

		if (parser.hasFlag("-path")) {
			Path path = parser.getPath("-path");
			if (parser.hasFlag("-threads")) {
				threadBuilder = new ThreadIndexBuilder(threadIndex, numThreads);
				try {
					threadBuilder.directoryBuilder(path);
				} catch (Exception e) {
					System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
							+ " to data structure. Please enter existing paths to textfiles.");
				}
			} else {
				try {
					indexBuilder.directoryIterator(path);
				} catch (Exception e) {
					System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
							+ " to data structure. Please enter existing paths to textfiles.");
				}
			}
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
			Path name = parser.getPath("-query");
			if (name != null) {
				if (parser.hasFlag("-threads")) {
					threadQueryBuilder = new ThreadedQueryBuilder(threadIndex, numThreads);
					try {
						threadQueryBuilder.build(name, !parser.hasFlag("-exact"));
					} catch (Exception e) {
						System.err.println("Invlad query file: " + name);
					}
				} else {
					try {
					queryBuilder.build(name, !parser.hasFlag("-exact"));
					} catch (Exception e) {
						System.err.println("Invlad query file: " + name);
					}
				}
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsPath = parser.getPath("-results", Path.of("results.json"));
			if (parser.hasFlag("-threads")) {
				try {
					threadQueryBuilder.queryWriter(resultsPath);
				} catch (IOException e1) {
					System.err.println("Unable to write results into path : " + resultsPath
							+ ". Please enter a valid output path name.");
				}
			} else {
			try {
				queryBuilder.queryWriter(resultsPath);
				} catch (IOException e1) {
					System.err.println("Unable to write results into path : " + resultsPath
							+ ". Please enter a valid output path name.");
				}
			}
		}

		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
