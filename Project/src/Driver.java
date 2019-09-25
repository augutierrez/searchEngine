import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 * 
	 * @param files
	 *
	 * 
	 * @param index
	 */
	public static void DirectoryIterator(File[] files, InvertedIndex index) {
		for (File file : files) {
			if (file.isDirectory()) {
				File[] listOfFiles = file.listFiles();
				DirectoryIterator(listOfFiles, index);
			} else {
				if (file != null)
					index.addPath(Paths.get(file.toString()));
			}
		}

	}
	
	/**
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();

		ArgumentParser parser = new ArgumentParser();
		parser.parse(args);
		InvertedIndex index = new InvertedIndex();
		Path path = parser.getPath("-path");
		if (path != null) {
			File file = new File(path.toString());
			if (!file.isDirectory()) {
				index.addPath(path);
			} else if (file.isDirectory()) {
				File[] listOfFiles = file.listFiles();
				DirectoryIterator(listOfFiles, index);
			}
			// calculate time elapsed and output //
		}
		if (parser.hasFlag("-index")) {
			index.indexWriter(parser.getString("-index"));
		}
		if (parser.hasFlag("-counts")) {
			index.countsWriter(parser.getString("-counts"));
		}
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
