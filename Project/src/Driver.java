import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

// TODO Clean up old TODO comments

/*
 * TODO Fix up SimpleJSONWriter
 * 
 * (Search for asObject on Piazza there is starter code for the approach to use.)
 * – Use an approach with iterators that does not need a counter or if statement inside loop
 * – Reuse your code everywhere possible (asNestedObject should call asArray for the inner collection)
 */

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
			// TODO More informative (about what functionality failed so the user knows what failed and how to fix it)
			System.err.println("Invalid path sent to Inverted Index, unable to add :" + path
					+ " to data structure. Please enter existing paths to textfiles.");
		}
		
		/*
		 * TODO Avoid converting too much between path and String
		 */
		
		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index");
			if (indexPath == null)
				indexPath = Path.of("index.json");
			
			// TODO Path name = parser.getPath("-index", Path.of("index.json"));
			try {
				index.indexWriter(indexPath); // TODO Make this method take a Path parameter instead of a String
												// parameter
			} catch (Exception e) {
				System.err.println("Invalid output file name sent to indexWriter: " + indexPath
						+ ". Please enter a valid output file path name.");
			}
		}
		
		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts");
			if (countsPath == null)
				countsPath = Path.of("counts.json");
			try {
				index.countsWriter(countsPath);
			} catch (Exception e) {
				System.err.println("Invalid output file name sent to countsWriter:" + countsPath
						+ ". Please enter a valid output file path name.");
			}
		}
		
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
