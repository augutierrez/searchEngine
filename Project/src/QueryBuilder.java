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
import java.util.Iterator;
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
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> readyToPrint;

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
		readyToPrint = new TreeMap<>();
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
	public void build(String path, boolean partial) throws FileNotFoundException, IOException {
		if (path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".text")) {
			try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
				String line;
				TreeSet<String> set = new TreeSet<>();
				while ((line = reader.readLine()) != null) {
					set.clear();
					if (!line.isBlank()) {
						set.addAll(TextFileStemmer.uniqueStems(line));
						searchQuery(set, partial);
					}
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
	public void searchQuery(TreeSet<String> set, boolean partial) {
		if (!set.isEmpty()) {
			readyToPrint.put(String.join(" ", set), generateResults(set, partial));
		}
	}

	/**
	 * Takes the set and adds stems that start with the stems inside it for partial
	 * search.
	 * 
	 * @param set - the set of queries
	 * @return the same set with newly added queries for partial search
	 */
	public TreeSet<String> partialSearch(TreeSet<String> set) {

		TreeSet<String> returnSet = new TreeSet<>();
		Iterator<String> stems = set.iterator();

		/*
		 * TODO This is a linear search, so usually a better way...
		 */
		while (stems.hasNext()) {
			Iterator<String> iterate = index.getWords().iterator();
			String stem = stems.next();
			while (iterate.hasNext()) {
				String key = iterate.next();
				if (key.startsWith(stem))
					returnSet.add(key);
			}
		}

		return returnSet;
	}

	/**
	 * Creates a list of results based off the queries passed to it and the type of
	 * search.
	 * 
	 * @param set     - set of queries
	 * @param partial : whether or not to perform partial search
	 * @return a list of results
	 */
	public ArrayList<InvertedIndex.Result> generateResults(TreeSet<String> set, boolean partial) {
		if (partial) {
			set.addAll(partialSearch(set));
		}

		ArrayList<InvertedIndex.Result> query = new ArrayList<>();
		for (String word : set) {
			if (index.contains(word)) {
				InvertedIndex.Result result;

				for (String location : index.getLocations(word)) {
					int counts = index.getPositions(word, location).size();
					int totalWords = index.getWordCounts(location);
					// if we have this result already, then update it
					boolean contains = false;
					/*
					 * TODO This is another linear search below...
					 * 
					 * It is always looking for a result with a specific location...
					 * 
					 * Use a lookup map!
					 * 
					 * Map<String (location), Result> lookup = 
					 * 
					 * if lookup.containsKey(location)
					 * 		lookup.get(location).update(word)
					 */
					for (InvertedIndex.Result tempResult : query) {
						if (tempResult.getDirectory().equals(location)) {
							contains = true;
							tempResult.update(word);
							break;
						}
					}
					if (!contains) {
						result = index.new Result(location, counts);
						query.add(result);
						// TODO Also add the result to the lookup map
					}
				}
			}
		}
		Collections.sort(query);
		return query;
	}

	/**
	 * The writer used for our queries.
	 * 
	 * @param path - the output path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.searchOutput(readyToPrint, path);
		}
	}
}
