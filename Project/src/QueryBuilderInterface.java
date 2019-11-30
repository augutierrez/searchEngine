import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A QueryBuilder Interface
 * 
 * @author tony
 *
 */
public interface QueryBuilderInterface {

	/**
	 *
	 * Reads the query files. It handles both exact and partial searches.
	 * 
	 * @param path    : the path that has the query values
	 * @param partial : whether or not to perform partial search
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public default void build(Path path, boolean partial)
			throws FileNotFoundException, IOException, InterruptedException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isBlank()) {
					searchQuery(line, partial);
				}
			}
		}
	}
	
	/**
	 * Initiates the search of queries and stores it into the data structure.
	 * 
	 * @param line    : the query line
	 * @param partial : whether or not to perform partial search
	 */
	public void searchQuery(String line, boolean partial);

	/**
	 * The writer used for our queries.
	 * 
	 * @param path - the output path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException;
}
