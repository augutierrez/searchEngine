import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Cleans and organizes our search queries
 * 
 * @author tony
 */
public class QueryBuilder {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> resultsMap;

	/**
	 * The data structure with the stored information from the text files.
	 */
	private final InvertedIndex index;

	/**
	 * Constructor method
	 * 
	 * @param index - InvertedIndex that will store information
	 */
	public QueryBuilder(InvertedIndex index) {
		resultsMap = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Reads the query files. It handles both exact and partial searches.
	 * 
	 * @param path    : the path that has the query values
	 * @param partial : whether or not to perform partial search
	 * @throws IOException           : file couldn't be found
	 * @throws FileNotFoundException
	 */
	public void build(Path path, boolean partial) throws FileNotFoundException, IOException {
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
	public void searchQuery(String line, boolean partial) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		if (stems.isEmpty()) {
			return;
		}
		String joined = String.join(" ", stems);
		if (resultsMap.containsKey(joined)) {
			return;
		}
		resultsMap.put(joined, index.generateSearch(stems, partial));
	}

	/**
	 * The writer used for our queries.
	 * 
	 * @param path - the output path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.searchOutput(resultsMap, path);
		}
	}
}
