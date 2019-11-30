import java.io.FileNotFoundException;
import java.io.IOException;
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
	public void build(Path path, boolean partial) throws FileNotFoundException, IOException, InterruptedException;
	
	/* TODO public default void build(Path path, boolean partial) throws FileNotFoundException, IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isBlank()) {
					searchQuery(line, partial);
				}
			}
		}
	}
	
	public void searchQuery(String line, boolean partial);
	public void queryWriter(Path path) throws IOException;
	*/
}
