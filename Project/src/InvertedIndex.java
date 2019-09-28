import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * @author Antonio Gutierrez
 *
 */
public class InvertedIndex {
	/**
	 * Nested Data Structure that stores all the words
	 */
	TreeMap<String, TreeMap<String, TreeSet<Integer>>> map = new TreeMap<>();

	/**
	 * used for counts flag
	 */
	TreeMap<String, Integer> wordCount = new TreeMap<>();
	/**
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
	 * @param path : the path to the file that it will read from
	 */
	public void addPath(Path path) {
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
			} catch (IOException e) {
				System.err.println("Trouble finding file in addPath!");
			}
		}
	}

	/**
	 * @param name : name of output file
	 */
	public void countsWriter(String name) {
		if (name == null) {
			name = "counts.json";
		}
		File file = new File(name);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
			SimpleJsonWriter.asObject(wordCount, Paths.get(file.toString()));
		} catch (IOException e) {
			e.printStackTrace(); // TODO Fix
		}

	}

	/**
	 * @param name : name of the output file
	 */
	public void indexWriter(String name) {
		if (name == null) {
			name = "index.json"; // TODO Move to Driver
		}
		File file = new File(name);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			SimpleJsonWriter.asNestedObjectInNestedObject(map, Paths.get(file.toString()));
		} catch (IOException e) {
			System.err.println("Problem with output file!");
		}

	}

	/**
	 * @param file : the file it will read
	 */
	public void readFile(Path file) {
		try (BufferedReader br = Files.newBufferedReader(Paths.get("index.json"))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			System.err.println("Error reading file!");
		}
	}

}
