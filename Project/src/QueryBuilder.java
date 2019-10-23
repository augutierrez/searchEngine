import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author tony Cleans and organizes our search queries
 */
public class QueryBuilder {

	/**
	 * Data Structure that will hold cleaned, stemmed, and sorted query values.
	 */
	TreeMap<String, ArrayList<Result>> readyToPrint = new TreeMap<>();

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
		this.index = index;
	}

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
	public void build(String path, String type) throws FileNotFoundException, IOException {
		if (path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".text")) {
			try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
				String line;
				TreeSet<String> set = new TreeSet<>();
				while ((line = reader.readLine()) != null) {
					// brand new set of search words for each iteration
					set.clear();
					if (!line.isBlank()) {
						set.addAll(TextFileStemmer.uniqueStems(line));
						searchQuery(set, type);
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
	public void searchQuery(TreeSet<String> set, String type) throws IOException {
		generate(set, type);
	}

	public void generate(TreeSet<String> set, String type) {
		StringBuffer buffer = new StringBuffer();
		if (!set.isEmpty()) {
			for (String word : set) {
				buffer.append(word);
				buffer.append(' ');
			}
			// deletes extra space
			buffer.deleteCharAt(buffer.length() - 1);
			readyToPrint.put(buffer.toString(), generateResults(set, type));
		}
	}

	/**
	 * @param set
	 * @param type
	 * @return a a
	 */
	public ArrayList<Result> generateResults(TreeSet<String> set, String type) {

		if (type.equals("partial")) {
			set.addAll(index.partialSearch(set));
		}
		ArrayList<Result> query = new ArrayList<>();
		for (String word : set) {
			// searching for exact word
			if (index.contains(word)) {
				Result result;

				for (String location : index.getLocations(word)) {
					int counts = index.getPositions(word, location).size();
					int totalWords = index.getWordCounts(location);
					// if we have this result already, then update it
					boolean contains = false;
					for (Result tempResult : query) {
						if (tempResult.getDirectory().equals(location)) {
							contains = true;
							tempResult.add(counts);
							break;
						}
					}
					if (!contains) {
						result = new Result(location, counts, totalWords);
						query.add(result);
					}
				}
			}
		}
		Collections.sort(query);
		return query;

	}

	/**
	 * The writer used for our queries
	 * 
	 * @param name
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.searchOutput(readyToPrint, path);
		}

	}


}
