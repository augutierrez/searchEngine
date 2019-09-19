import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;


public class InvertedIndex {
	TreeMap<String, TreeMap<String, TreeSet<Integer>>> map = new TreeMap<>();

//	public static BufferedReader newBufferedReader(Paths path, Charset cs)throws IOException;
	/**
	 * @param word     : the words that must be added
	 * @param path     : the path where the word was found
	 * @param position : the line the word was found in the file
	 */
	public void add(String word, String path, int position) {
		if (!map.containsKey(word)) {
			TreeSet<Integer> tempSet = new TreeSet<>();
			// tempSet.add(position);
			TreeMap<String, TreeSet<Integer>> tempMap = new TreeMap<>();
			tempMap.put(path, tempSet);

			map.put(word, tempMap);
		}
		if (!map.get(word).containsKey(path)) {
			TreeMap<String, TreeSet<Integer>> tempMap = new TreeMap<>();
			TreeSet<Integer> tempSet = new TreeSet<>();
			tempMap.put(path, tempSet);

		}
		map.get(word).get(path).add(position);
		System.out.println("Inserted : " + word + " in path : " + path + " in position: " + position);

	}

	/**
	 * @param path : the path to the file that it will read from
	 * @throws IOException
	 */
	public void addPath(Path path) throws IOException {
//		if (path.getNameCount() == 0) {
		if (path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text")) {
			BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
			String line;
			int counter = 1;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> words = TextFileStemmer.uniqueStems(line);
				Iterator<String> iterate = words.iterator();
				while (iterate.hasNext()) {
					add(iterate.next(), path.toString(), counter);
				}
				}
			reader.close();
		}
	}

	/**
	 * @param name : name of the output file
	 * @throws IOException
	 */
	public void indexWriter(String name) throws IOException {
//		System.out.println("here");
//		if (name == null) {
//			name = "index.json";
//		}
		File file = new File("index.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		// System.out.println(file.exists());

//		BufferedWriter writer = new BufferedWriter(new FileWriter("c:/temp/samplefile1.txt"));
//		BufferedReader br = new BufferedReader(new FileReader(new File("Filepath")));
//	    SimpleJsonWriter jsonWriter = new SimpleJsonWriter();
		// System.out.println("map is empty: " + map.isEmpty());
		for (String key : map.keySet()) {
			// System.out.println("Key: " + key);
			SimpleJsonWriter.asNestedObject(map.get(key), Paths.get(file.toString()));
		}
		readFile(Paths.get(file.toString()));
		writer.close();

	}

	public void readFile(Path file) throws IOException {
//		System.out.println("IN readfile");
//		try {
//			System.out.println("File exists");
//			//BufferedReader reader = new BufferedReader(file);
		try (BufferedReader br = Files.newBufferedReader(Paths.get("index.json"))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				// System.out.println("empty?");
			}
		}
			
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
