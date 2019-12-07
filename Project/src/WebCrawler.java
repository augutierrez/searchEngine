import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class that interacts with an InvertedIndex to store data from webpages
 * 
 * @author tony
 *
 */
public class WebCrawler {

	/**
	 * The stemmer used for the path's data
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * WorkQueue for the class
	 */
	private final WorkQueue workQueue;

	/**
	 * InvertedIndex that is used to store the information
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * The amount of links requested to be stored
	 */
	private final int numLinks;

	/**
	 * a URL list that is used to keep track of the URLs processed
	 */
	private final ArrayList<URL> urlList;

	/**
	 * Constructor method for WebCrawler
	 * 
	 * @param index     - the index that will be used to store information
	 * @param workQueue - the WorkQueue for the class
	 * @param numLinks  - the limit of redirects requested
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue workQueue, int numLinks) {
		this.index = index;
		this.workQueue = workQueue;
		this.numLinks = numLinks;
		urlList = new ArrayList<URL>();
	}

	/**
	 * @param url
	 * @throws InterruptedException
	 * @throws MalformedURLException
	 */
	public void crawl(String url) throws InterruptedException, MalformedURLException {
		URL cleaned = LinkParser.clean(new URL(url));
		storeHtml(cleaned);
		urlList.add(cleaned);
		workQueue.finish();
	}

	/**
	 * Takes a URL and stores the HTML found in the links found within it
	 * 
	 * @param url - the URL whose information will be extracted
	 * @throws MalformedURLException
	 */
	public void crawl(URL url) throws MalformedURLException {
		String html = HtmlFetcher.fetch(url, 3);
		if (html == null) {
			return;
		}
		for (URL tempUrl : LinkParser.listLinks(url, html)) {
			URL tempCleaned = LinkParser.clean(tempUrl);
			boolean contain = false;
			synchronized (urlList) {

				if (urlList.contains(tempCleaned)) {
					contain = true;
				}
			}
			if (!contain && urlList.size() < numLinks) {
				storeHtml(tempCleaned);
				urlList.add(tempCleaned);
			}
		}
	}

	/**
	 * Creates a new task that will store information to the InvertedIndex
	 * 
	 * @param url - the URL whose HTML text will be extracted
	 */
	public void storeHtml(URL url) {
		workQueue.execute(new Task(url));
	}

	/**
	 * Adds all the content from the text content form the website onto the
	 * InvertedIndex
	 * 
	 * @param url   - the url of the website
	 * @param html  - the html text content
	 * @param index - the InvertedIndex used to store the text
	 */
	public static void addHtml(String url, String html, ThreadSafeInvertedIndex index) {
		int counter = 1;
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		for (String word : TextParser.parse(html)) {
			index.add(stemmer.stem(word).toString(), url, counter++);
		}
	}

	/**
	 * Task class that will handle adding to the InvertedIndex
	 * 
	 * @author tony
	 */
	private class Task implements Runnable {
		/**
		 * The URL to the website the task will read from
		 */
		private URL url;

		/**
		 * The constructor method for task
		 * 
		 * @param url - the url whose information needs to be stored
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url, 3);
			if (html == null) {
				return;
			}
			String cleanedHtml = HtmlCleaner.stripHtml(html);
			int counter = 1;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			synchronized (index) {
				for (String word : TextParser.parse(cleanedHtml)) {
					index.add(stemmer.stem(word).toString(), url.toString(), counter++);
				}
			}
			try {
				crawl(url);
			} catch (MalformedURLException e) {
				return;
			}
		}
	}

}