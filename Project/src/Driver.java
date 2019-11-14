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
		QueryBuilder queryBuilder = new QueryBuilder(index);

		if (parser.hasFlag("-path")) {
			Path path = parser.getPath("-path");
			try {
				InvertedIndexBuilder.directoryIterator(path, index);
			} catch (Exception e) {
				System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
						+ " to data structure. Please enter existing paths to textfiles.");
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
					index.indexWriter(indexPath);
			} catch (Exception e) {
					System.err.println("Invalid output file sent to indexWriter: " + indexPath
							+ ". Please enter a valid output file path name.");
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			try {
					index.countsWriter(countsPath);
			} catch (Exception e) {
					System.err.println("Unalbe to write word counts to the path:" + countsPath
							+ ". Please enter a valid output file path name.");
			}
		}


		if (parser.hasFlag("-query")) {
			String name = parser.getString("-query");
			if (name != null) {
				boolean partial = true;
				if (parser.hasFlag("-exact"))
					partial = false;
				try {
					queryBuilder.build(name, partial);
					// TODO queryBuilder.build(name, !parser.hasFlag("-exact"));
				} catch (Exception e) {
					System.err.println("Invlad query file: " + name);
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
