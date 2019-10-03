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
		
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
