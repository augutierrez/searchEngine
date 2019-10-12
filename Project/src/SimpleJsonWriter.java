import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		// TODO MODIFY AND FILL IN AS NECESSARY TO PASS TESTS
		// TODO USE ITERATION NOT STRING REPLACEMENT //
		Iterator<Integer> iterate = elements.iterator();
		writer.write("[\n");
		while (iterate.hasNext()) {
			indent(iterate.next(), writer, level + 1);

			if (iterate.hasNext())
				writer.write(',');

			writer.write('\n');
		}
		writer.write(']');
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> setIterator = elements.keySet().iterator();
		writer.write("{");
		// Iterator for Strings
		while (setIterator.hasNext()) {
			String element = setIterator.next();
			writer.write('\n');
			// Value of Integer for each String
			Integer key = elements.get(element);
			quote(element, writer, level + 1);
			writer.write(": ");
			writer.write(key.toString());
			if (setIterator.hasNext()) {
				writer.write(",");
			}
		}
		writer.write('\n');
		writer.write("}");

	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level) throws IOException {
		Iterator<String> setIterator = elements.keySet().iterator();
		writer.write("{");
		// Outer loop (Strings)
		while (setIterator.hasNext()) {
			String element = setIterator.next();
			writer.write('\n');
			Iterator<Integer> intIterator = elements.get(element).iterator();
			quote(element, writer, level + 1);
			writer.write(": [");
			writer.write('\n');
			// Inner loop (Integers)
			while (intIterator.hasNext()) {
				indent(intIterator.next(), writer, level + 2);
				if (intIterator.hasNext()) {
					writer.write(",");
				}
				writer.write('\n');

			}
			indent("]", writer, level + 1);
			if (setIterator.hasNext()) {
				writer.write(",");
			}

		}
		writer.write('\n');
		writer.append("}");


	}

	/**
	 * @param elements
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	public static void searchOutput(Map<String, ? extends ArrayList<Result>> elements, Writer writer, int level)
			throws IOException {
		Iterator<String> setIterator = elements.keySet().iterator();
		writer.write("{");
		// Outer loop (Strings)
		while (setIterator.hasNext()) {
			String element = setIterator.next();
			writer.write('\n');
			Iterator<Result> setIterator2 = elements.get(element).iterator();
			// elements.get(element).keySet().iterator();
			quote(element, writer, level + 1);
			writer.write(": [");
			writer.write('\n');
			// Inner loop (Integers)
			while (setIterator2.hasNext()) {
				Result result = setIterator2.next();
				String location = result.getDirectory();
				String count = result.getCount();
				DecimalFormat df = new DecimalFormat("0.00000000");
				String score = df.format(result.getScore());
				indent("{\n", writer, level + 2);
				indent("\"where\": ", writer, level + 3);
				quote(location, writer, level);
				// writer.write(elements.get(element).get(location).getDirectory());
				writer.write(",\n");
				indent("\"count\": ", writer, level + 3);
				writer.write(count);
				writer.write(",\n");
				indent("\"score\": ", writer, level + 3);
				// DecimalFormat df = new DecimalFormat("0.00000000");
				// String score = String.format("%.8f",
				// elements.get(element).get(location).getScore());

				writer.write(score);
				writer.write("\n");
				indent("}", writer, level + 2);
				if (setIterator2.hasNext()) {
					writer.write(",");
				}
				writer.write('\n');

			}
			indent("]", writer, level + 1);
			if (setIterator.hasNext()) {
				writer.write(",");
			}

		}
		writer.write('\n');
		writer.append("}");

	}

	/**
	 * @param elements
	 * @param path
	 * @throws IOException
	 */
	public static void searchOutput(TreeMap<String, ArrayList<Result>> elements, Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			searchOutput(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObject(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObjectInNestedObject(
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level) throws IOException {
		Iterator<String> setIterator = elements.keySet().iterator();
		writer.write("{");
		// Outer loop (Strings)
		while (setIterator.hasNext()) {
			String element = setIterator.next();
			writer.write('\n');


			quote(element, writer, level + 1);
			writer.write(": {");
			writer.write('\n');
			// Inner loop (Integers)
			Iterator<String> pathIterator = elements.get(element).keySet().iterator();
			// path
			while (pathIterator.hasNext()) {
				String path = pathIterator.next();
				quote(path, writer, level + 2);
				writer.write(": [\n");
				Iterator<Integer> intIterator = elements.get(element).get(path).iterator();

				// int
				while (intIterator.hasNext()) {
					indent(intIterator.next(), writer, level + 3);
					if (intIterator.hasNext()) {
						writer.write(",");
					}
					writer.write('\n');

				}

				indent("]", writer, level + 2);
				if (pathIterator.hasNext())
					writer.write(',');
				writer.write('\n');
			}

			indent("}", writer, level + 1);
			if (setIterator.hasNext()) {
				writer.write(",");
			}

		}
		writer.write('\n');
		writer.append("}");

	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObjectInNestedObject(
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,
			Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObjectInNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}

	/**
	 * A simple main method that demonstrates this class.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// MODIFY AS NECESSARY TO DEBUG YOUR CODE

		TreeSet<Integer> elements = new TreeSet<>();
		System.out.println("Empty:");
		System.out.println(asArray(elements));

		elements.add(65);
		System.out.println("\nSingle:");
		System.out.println(asArray(elements));

		elements.add(66);
		elements.add(67);
		System.out.println("\nSimple:");
		System.out.println(asArray(elements));
		Map<String, Integer> element = new HashMap<String, Integer>();
		element.put("hello", 1);
		System.out.println("Objects");
		System.out.println(asObject(element));

		System.out.println("NEW");
		TreeMap<String, TreeSet<Integer>> elementr = new TreeMap<>();
		elementr.put("a", new TreeSet<>());
		elementr.put("b", new TreeSet<>());
		elementr.put("c", new TreeSet<>());

		elementr.get("a").add(1);
		elementr.get("b").add(2);
		elementr.get("b").add(3);
		elementr.get("b").add(4);
		System.out.println(asNestedObject(elementr));
	}
}
