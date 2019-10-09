/**
 * @author tony This class will hold
 */
public class Result {
	private final String directory;
	private int count;
	private int totalWords;
	private int score;

	/**
	 * @param directory
	 * @param count
	 * @param score
	 */
	public Result(String directory, int count, int totalWords) {
		this.directory = directory;
		this.count = count;
		this.totalWords = totalWords;
		this.score = count / totalWords;
	}

	/**
	 * @param count
	 */
	public void add(int count) {
		this.count += count;
		this.score = this.count / this.totalWords;
	}
}


