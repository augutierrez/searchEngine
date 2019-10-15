import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
	public static boolean checkText(Path path) {
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

}
