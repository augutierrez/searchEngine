/**
 * @author tony This class will hold
 */
public class Result {
	private final String directory;
	private Integer count;
	private Integer totalWords;
	private Integer score;

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

	public String getDirectory() {
		return this.directory;
	}

	public String getCount() {
		if (this.count == null)
			return "0";
		return this.count.toString();
	}

	public String getScore() {
		return this.score.toString();
	}
}


