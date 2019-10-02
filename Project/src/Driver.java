import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
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


	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 * 
	 * @param files : the list of files in the directory we will iterate
	 * @param index : the data structure is storing what's found in the files
	 * @throws IOException
	 */
	public static void directoryIterator(Path path, InvertedIndex index) throws IOException {

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {

			for (Path path1 : listing) {

//		for (File file : files) {
//			if (file.isDirectory()) {
//				File[] listOfFiles = file.listFiles();
//				directoryIterator(listOfFiles, index);
//			}
				if (Files.isDirectory(path1)) {
					try (DirectoryStream<Path> listing1 = Files.newDirectoryStream(path1)) {
						// use an enhanced-for or for-each loop for efficiency and simplicity
						for (Path tempPath : listing1) {
//						if (Files.isDirectory(path)) {
//							System.out.println(path.toString() + "/");
//							traverseDirectory(path);
//						}
//						else {
//							System.out.printf(FORMAT, path.toString(), Files.size(path));
//						}

							// note the duplicated logic above with traverse()!
							// avoid the duplicated code by just calling the traverse() method
							// (requires designing it just right to make this possible)
							directoryIterator(tempPath, index);
						}
					}
				} else {
					if (Files.isRegularFile(path1))
						index.addPath(path1);
			}
		}
		}
	}
	
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
	 * TODO Need to actually fill in descriptions for your Javadoc comments
	 * 
	 * @param args flag/value pairs used to start this program
	 * @throws IOException
	 */

	/*
	 * TODO Should not use File or toFile in modern Java code. file.isDirectory()
	 * --> Files.isDirectory(path)
	 * 
	 * listFiles() is the hardest one to figure out: Could try something similar
	 * to:https://github.com/usf-cs212-fall2019/template-textfilefinder Or look at:
	 * https://github.com/usf-cs212-fall2019/lectures/blob/master/Files%20and%
	 * 20Exceptions/src/DirectoryStreamDemo.java
	 */
	public static void main(String[] args) throws IOException {
		// store initial start time
		Instant start = Instant.now();

		ArgumentParser parser = new ArgumentParser();
		parser.parse(args);
		InvertedIndex index = new InvertedIndex();
		Path path = parser.getPath("-path");
		
		if (path != null) {
//			File file = new File(path.toString());

//			if (Files.isDirectory(path)) {
//				index.addPath(path);
//			}
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					// use an enhanced-for or for-each loop for efficiency and simplicity
					for (Path tempPath : listing) {
//						if (Files.isDirectory(path)) {
//							System.out.println(path.toString() + "/");
//							traverseDirectory(path);
//						}
//						else {
//							System.out.printf(FORMAT, path.toString(), Files.size(path));
//						}

						// note the duplicated logic above with traverse()!
						// avoid the duplicated code by just calling the traverse() method
						// (requires designing it just right to make this possible)
						directoryIterator(tempPath, index);
					}
				}
			}

			else
				index.addPath(path);
//			} else if (file.isDirectory()) {
//				File[] listOfFiles = file.listFiles();
//				directoryIterator(listOfFiles, index);
//			}
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
