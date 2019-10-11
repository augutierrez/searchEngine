import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * @author Antonio Gutierrez
 */
public class InvertedIndex {
	/**
	 * Nested Data Structure that stores all the words First Key is words Second Key
	 * is for locations
	 */
	TreeMap<String, TreeMap<String, TreeSet<Integer>>> map = new TreeMap<>();

	/**
	 * used for counts flag
	 */
	TreeMap<String, Integer> wordCount = new TreeMap<>();
	
	/**
	 * 
	 */
//	HashMap<String, HashSet<Result>> querySet = new HashMap<>();

	// the data structure that will get printed:
	TreeMap<String, TreeSet<Result>> readyToPrint = new TreeMap<>();


	/**
	 * @param setOfQueries
	 * @param set
	 * @return
	 */

	public void generate(TreeSet<String> set, String type) {
		StringBuffer buffer = new StringBuffer();
		if (!set.isEmpty()) {
			for (String word : set) {
				buffer.append(word);
				buffer.append(' ');
			}
			// deletes extra space
			buffer.deleteCharAt(buffer.length() - 1);
			// StringBuilder builder = new StringBuilder();

			// setOfQuerySets.add(generateResults(set));
			readyToPrint.put(buffer.toString(), generateResults(set, type));

			// added everything, now we print out everything
			System.out.println("search result objects: " + readyToPrint.toString());
		}
	}

	public TreeSet<String> partialSearch(TreeSet<String> set) {
		TreeSet<String> returnSet = new TreeSet<>();
		Iterator<String> stems = set.iterator();
		Iterator<String> iterate = map.keySet().iterator();
		while (stems.hasNext()) {
			String stem = stems.next();
			// System.out.println("Stem: " + stem);
			while (iterate.hasNext()) {
				String key = iterate.next();
				// System.out.println("Key: " + key + " " + key.startsWith(stem));
				if (key.startsWith(stem))
					returnSet.add(key);
			}
		}

		return returnSet;
	}

	/**
	 * @param set
	 * @return
	 */
	public TreeSet<Result> generateResults(TreeSet<String> set, String type) {

		if (type.equals("partial")) {
			set.addAll(partialSearch(set));
		}
		TreeSet<Result> query = new TreeSet<>();
		for (String word : set) {
			// traversing the maps within our map

			// searching for exact word
			if (map.containsKey(word)) {
				Result result;

				for (Map.Entry<String, TreeSet<Integer>> entry1 : map.get(word).entrySet()) {
					String location = entry1.getKey();
					// Files file = Files.createFile(location);
					int counts = entry1.getValue().size();
					int totalWords = wordCount.get(location);
					// if we have this result already, then update it
					boolean contains = false;
					for (Result tempResult : query) {
						if (tempResult.getDirectory().equals(location)) {
							contains = true;
							tempResult.add(counts);
							break;
						}
					}
//					if (query.containsKey(location)) {
//						Result temp = query.get(location);
//						temp.add(counts);
//					}
//					else {
//						result = new Result(location, counts, totalWords);
//						System.out.println("result: " + result);
//						query.put(location, result);
//					}
					if (!contains) {
						result = new Result(location, counts, totalWords);
						System.out.println("result: " + result);
						query.add(result);
					}
				}
			}
			// querySet.put(word, results);
		}


		return query;

	}

	/**
	 * Receives words from addPath() and stores it in the map data structure
	 * 
	 * @param word     : the words that must be added
	 * @param path     : the path where the word was found
	 * @param position : the line the word was found in the file
	 */
	public void add(String word, String path, int position) {
		TreeSet<String> set = TextFileStemmer.uniqueStems(word);
		word = set.first();
		if (!map.containsKey(word)) {

			TreeMap<String, TreeSet<Integer>> tempMap = new TreeMap<>();
			map.put(word, tempMap);

			map.put(word, tempMap);
		}
		if (!map.get(word).containsKey(path)) {

			TreeSet<Integer> tempSet = new TreeSet<>();
			map.get(word).put(path, tempSet);

		}
		map.get(word).get(path).add(position);

	}

	/**
	 * Extracts information from the file passed and each word found in the file and
	 * passes it to add()
	 * 
	 * @param path : the path to the file that it will read from
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void addPath(Path path) throws FileNotFoundException, IOException {
		if (path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text")) {
			try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {

			String line;

			int counter = 1;
			while ((line = reader.readLine()) != null) {
				String[] wordsInLine = TextParser.parse(line);

				for (String word : wordsInLine) {
					add(word, path.toString(), counter);
					counter++;
				}
				wordCount.put(path.toString(), counter - 1);
			}
			}
		}
	}

	/**
	 * Accepts a path, and iterates over it if it is a directory, or simply adds it
	 * to the data structure if it is a file.
	 * 
	 * @param path : the path we will extract information from
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void directoryIterator(Path path) throws FileNotFoundException, IOException {
		if (path != null) {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path currPath : listing)
						directoryIterator(currPath);
				}
			} else {
				if (Files.isRegularFile(path))
					addPath(path);
			}
		}
	}

	/**
	 * Creates a writer for the -counts flag and outputs to the file passed
	 * 
	 * @param name : name of output file
	 * @throws IOException
	 */
	public void countsWriter(String name) throws IOException {

		File file = new File(name);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
			SimpleJsonWriter.asObject(wordCount, Paths.get(file.toString()));
		}

	}

	/**
	 * Creates a writer for the -index flag and outputs to the file passed
	 * 
	 * @param name : name of the output file
	 * @throws IOException
	 */
	public void indexWriter(String name) throws IOException {

		File file = new File(name);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			SimpleJsonWriter.asNestedObjectInNestedObject(map, Paths.get(file.toString()));
		}

	}

	/**
	 * @param name
	 * @param map
	 * @throws IOException
	 */
	public void queryWriter(String name) throws IOException {
		File file = new File(name);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			SimpleJsonWriter.searchOutput(readyToPrint, Paths.get(file.toString()));
		}

	}
	/**
	 * Reads the file passed
	 * 
	 * @param file : the file it will read
	 * @throws IOException
	 */
	public void readFile(Path file) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(Paths.get("index.json"))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}

}
