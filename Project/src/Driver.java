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

		Path path = parser.getPath("-path");
		try {
			index.directoryIterator(path);
		} catch (Exception e) {
			System.err.println("Invalid path : " + path);
		}
		
		if (parser.hasFlag("-index")) {
			String name = parser.getString("-index");
			if (name == null)
				name = "index.json";
			try {
			index.indexWriter(name);
			} catch (Exception e) {
				System.err.println("Invalid output file name: " + name);
			}
		}
		
		if (parser.hasFlag("-counts")) {
			String name = parser.getString("-counts");
			if (name == null)
				name = "counts.json";
			try {
				index.countsWriter(name);
			} catch (Exception e) {
				System.err.println("Invalid output file name: " + name);
			}
		}
		if (parser.hasFlag("-query")) {
			// what is the name of the output file???? i need to pass it to my writer
			QueryBuilder queryBuilder = new QueryBuilder();
			String name = parser.getString("-query");
			if (name != null) {
				String type = "partial";

				if (parser.hasFlag("-exact"))
					type = "exact";
				try {
					queryBuilder.build(name, index, type);
				} catch (Exception e) {
					System.err.println("Invlad query file: " + name);
					System.out.println(e);
				}
				if (parser.hasFlag("-results")) {
					String outputFileName = parser.getString("-results");
					if (outputFileName == null)
						outputFileName = "results.json";


					// we can do(if exact, pass value exact, else simple
					try {
						System.out.println(outputFileName);
						index.queryWriter(outputFileName); // remember to pass kind
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}
			queryBuilder.printOut();
//			try {
//				queryBuilder.searchQuery(index);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}


		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
