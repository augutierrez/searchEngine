import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

// TODO Look for the TODO in TextFileStemmer

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

		// TODO Move this inside if (parser.hasFlag(-path))
		Path path = parser.getPath("-path");
		try {
			FileReader.directoryIterator(path, index);
		} catch (Exception e) {
			System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
					+ " to data structure. Please enter existing paths to textfiles.");
		}
		
		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
				index.indexWriter(indexPath);
			} catch (Exception e) {
				System.err.println("Invalid output file name sent to indexWriter: " + indexPath
						+ ". Please enter a valid output file path name.");
			}
		}
		
		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			try {
				index.countsWriter(countsPath);
			} catch (Exception e) {
				// TODO Avoid referencing code---users can't see method names.
				// TODO Unable to write word counts to the path: " + countsPath
				System.err.println("Invalid output file name sent to countsWriter:" + countsPath
						+ ". Please enter a valid output file path name.");
			}
		}
		
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
