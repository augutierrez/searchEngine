import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * @author tony Cleans and organizes our search queries
 */
public class QueryBuilder {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	ArrayList<TreeSet<String>> setOfQueries = new ArrayList<>();


	/**
	 * This method will take a path and clean, stem, and add the query values into
	 * our data structure.
	 * 
	 * @param path  : the path that has the query values
	 * @param index
	 * @param type
	 * @throws IOException           : file couldn't be found
	 * @throws FileNotFoundException
	 */
	public void build(String path, InvertedIndex index, String type) throws FileNotFoundException, IOException {
		if (path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".text")) {
			try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
				String line;
				TreeSet<String> set = new TreeSet<>();
				while ((line = reader.readLine()) != null) {
					// brand new set of search words for each iteration
					set.clear();
					if (!line.isBlank()) {
						set.addAll(TextFileStemmer.uniqueStems(line));
						searchQuery(index, set, type);
					}
				}
			}
		}
	}

	/**
	 * @param index
	 * @param set
	 * @param type
	 * @throws IOException
	 */
	public void searchQuery(InvertedIndex index, TreeSet<String> set, String type) throws IOException {
		index.generate(set, type);
	}

	/**
	 * 
	 */
	public void printOut() {
		System.out.println(setOfQueries.toString());
	}

}
