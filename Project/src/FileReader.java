import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO Refactor class name to a more specific name. InvertedIndexBuilder, InvertedIndexGenerator, etc.
/**
 * @author tony Class with useful methods for reading files
 */
public class FileReader {

	/**
	 * Checks if a path is a text file
	 * 
	 * @param path - the path that's being considered
	 * @return true/ false
	 */
	public static boolean checkText(Path path) { // TODO isText
		// TODO String lower = path.toString().toLowerCase() and reuse in the return below
		// TODO return lower.endsWith(".txt") || lower.endsWith(".text");
		return path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text");
	}

	/**
	 * Accepts a path, and iterates over it if it is a directory, or simply adds it
	 * to the data structure if it is a file.
	 * 
	 * @param path  : the path we will extract information from
	 * @param index : the data structure we are storing in
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void directoryIterator(Path path, InvertedIndex index) throws FileNotFoundException, IOException {
		if (path != null) {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path currPath : listing)
						directoryIterator(currPath, index);
				}
			} else {
				if (Files.isRegularFile(path))
					index.addPath(path);
			}
		}
	}
	
	/* TODO
	public static void addPath(Path path, InvertedIndex index) throws IOException {
		code from the inverted index class
	}
	*/
	
	// TODO Remind Sophie in project 2 code reviews to prep this class for project 3

}
