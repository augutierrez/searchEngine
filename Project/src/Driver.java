import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;


// TODO Remove old TODO comments



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {

	
	/*
	 * TODO Driver classes: It is the programmer-specific "driver" used to call
	 * other code. It is the only class you do not share with other developers.
	 * 
	 * Anything considered useful should be in another class (and generalized).
	 */
	
	/*
	 * TODO Exception handling
	 * 
	 * All output to the user needs to be user friendly and informative.
	 * 1) Stack traces are not user friendly.
	 * 2) Output needs to be informative so that the user can re-run the code
	 * without the same problem.
	 * 
	 * Catch exceptions in the code that interacts with the user (i.e. Driver.main)
	 * Throw the exceptions everywhere else.
	 * 
	 * if (parser.hasFlag("-counts")) {
	 *   Path path = parser.getPath("-counts", Path.of("counts.json"));
			 try {
			 		index.countsWriter(path);
			 } 
			 catch (IOException e) {
			 		System.out.println("Unable ot write the word counts to JSON file at: " + path);
			 }
		}
	 */
	
	/**
	 * TODO Need to actually fill in descriptions for your Javadoc comments Driver
	 * used to test Inverted Index for Project1
	 * 
	 * @param args flag/value pairs used to start this program
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		// store initial start time
		Instant start = Instant.now();

		ArgumentParser parser = new ArgumentParser();
		parser.parse(args);
		InvertedIndex index = new InvertedIndex();
		Path path = parser.getPath("-path");
		
		index.directoryIterator(path);
		
		if (parser.hasFlag("-index")) {
			String name = parser.getString("-index");
			if (name == null)
				name = "index.json";
			index.indexWriter(name);
		}
		
		if (parser.hasFlag("-counts")) {
			String name = parser.getString("-counts");
			if (name == null)
				name = "counts.json";
			index.countsWriter(name);
		}
		
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
