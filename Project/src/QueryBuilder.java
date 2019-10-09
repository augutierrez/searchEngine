import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author tony Cleans and organizes our search queries
 */
public class QueryBuilder {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	ArrayList<TreeSet<String>> setOfQueries = new ArrayList<>();
	// will store where its from, word count, score
	HashMap<String, Result> results = new HashMap<>();

	/**
	 * This method will take a path and clean, stem, and add the query values into
	 * our data structure.
	 * 
	 * @param path : the path that has the query values
	 * @throws IOException           : file couldn't be found
	 * @throws FileNotFoundException
	 */
	public void build(String path, InvertedIndex index) throws FileNotFoundException, IOException {
		if (path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".text")) {
			try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

				String line;
				TreeSet<String> set;
				while ((line = reader.readLine()) != null) {
					set = new TreeSet<>();
					String[] wordsInLine = TextParser.parse(line);

					for (String word : wordsInLine) {
						set.add(word);
					}
					setOfQueries.add(set);
				}
			}
		}
		searchQuery(index);

	}

	/**
	 * @param index
	 * @throws IOException
	 */
	public void searchQuery(InvertedIndex index) throws IOException {
		index.generate(setOfQueries);
	}

	public void printOut() {
		System.out.println(setOfQueries.toString());
	}

}
