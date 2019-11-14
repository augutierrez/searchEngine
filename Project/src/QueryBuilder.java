import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	 * @param index
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
	// TODO String path --> Path path
	public void build(String path, boolean partial) throws FileNotFoundException, IOException {
		if (path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".text")) { // TODO Remove
			try (BufferedReader reader = new BufferedReader(new FileReader(path))) { // TODO Files.newBufferedReader
				String line;
				TreeSet<String> set = new TreeSet<>(); // TODO Remove
				while ((line = reader.readLine()) != null) {
					set.clear();
					if (!line.isBlank()) {
						set.addAll(TextFileStemmer.uniqueStems(line));
						searchQuery(set, partial);
					}
					
					// TODO Just ALWAYS call searchQuery(line, partial);
				}
			}
		}
	}

	/**
	 * Initiates the search of queries and stores it into the data structure.
	 * 
	 * @param set     : the set of queries
	 * @param partial : whether or not to perform partial search
	 */
	// TODO public void searchQuery(String line, boolean partial) {
	public void searchQuery(TreeSet<String> set, boolean partial) {
		if (!set.isEmpty()) {
			resultsMap.put(String.join(" ", set), index.generateResults(set, partial));
		}
		
		/* TODO
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		
		if (set.isEmpty()) {
			return;
		}
		
		String joined = String.join(" ", stems);
		
		if (resultsMap.containsKey(joined)) {
			return;
		}
		
		resultsMap.put(joined, index.generateResults(set, partial));
		*/
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
