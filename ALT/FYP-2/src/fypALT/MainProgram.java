package fypALT;

import possTagger.PossTagger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.ws.http.HTTPException;
import oscar.OscarMain;

import com.google.gson.Gson;

import cosine.ConsineScore;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class MainProgram {

	private static GoogleResults results;
	private static OscarMain oscar;
	private static SearchData search;
	private static MaxentTagger tagger;
	private static PossTagger taggingClass;

	// remove comments to run wordnet
	// private static Dictionary wordnet;
	// private static ArrayList<String> taggedList = new ArrayList<String>();
	// private static ArrayList<String> synonyms = new ArrayList<String>();

	private static ArrayList<String> keyWords = new ArrayList<String>();
	private static ArrayList<String> summery = new ArrayList<String>();
	private static String questionCopy;
	private static int topAns = 0;

	public MainProgram() {

		tagger = new MaxentTagger(
				"taggers\\english-bidirectional-distsim.tagger");

		oscar = new OscarMain();
		search = new SearchData();
		taggingClass = new PossTagger();

		oscar.initalize();

		// initaliza WordNet
		// wordnet = new Dictionary();
		// Dictionary.initalize();

	}

	/*public static void main(String args[]) throws Exception {

		// Proxy Sitting
		
		 * System.setProperty("http.proxyHost", "172.16.1.2");
		 * System.setProperty("http.proxyPort", "8080");
		 

		MainProgram proc = new MainProgram(); // start Search
		proc.startSearch("what is water");

	}*/

	public boolean startSearch(String inputQuestion) throws Exception {

		// Remove comments from both line to get synonyms
		// taggedList = taggingClass.getTagged(inputQuestion, tagger);
		// synonyms = Dictionary.getSynonyms(taggedList);

		keyWords = oscar.tagChemistryKeyWords(inputQuestion);

		if (keyWords.size() > 0) {
			questionCopy = inputQuestion;

			// Remove comments to add synonyms
			/*
			 * for (String str : synonyms) { if (str != " " || str != null)
			 * System.out.println(str + "  " ); questionCopy += " " + str; }
			 */
			
			//checkPageURL(inputQuestion, 1);

			queryGoogle(inputQuestion);

			String formula = "";
			for (String str : keyWords) {
				if (!str.equals("chemistry"))
					formula += oscar.chemicalFormula(str + " ");
			}
			summery.add(formula);
			summery.add(inputQuestion);
			topAns = ConsineScore.CosineMain(summery);
			
			System.out.println("Top Answer: \n" + summery.get(topAns));
			System.out.println("Other Related Answers:");

			for (int index = 0; index < summery.size() - 1; index++)
				if (topAns != index)
					System.out.println(summery.get(index));

			return true;

		} else {
			System.out.println("Out of domain Question");
			return false;
		}

	}

	public void clearData() {
		summery.clear();
		topAns = 0;
	}

	public void checkPageURL(String inputQuestion, int urlCount)
			throws IOException {
		String Url;
		try {
			String temp = null;

			// Show title and URL of each results
			for (int i = 0; i < urlCount; i++) {

				Url = results.getResponseData().getResults().get(i).getUrl();

				if (!Url.matches(".*(doc|dot|docx|docm|dotx|dotm|ppt|pot|pps|xls|xlt|xlm|pdf)")) {
					temp = search.DataExtract(Url, questionCopy, taggingClass,
							tagger);

					if (!temp.equals("0"))
						summery.add(temp);
					else
						System.out.println("not included " + Url);

				}
			}
		} catch (HTTPException e) {
			System.err.println("Check the internet connection");
		}
	}

	public void queryGoogle(String inputQuestion) {

		String charset = "UTF-8";
		String address = null;
		int urlCount = 0;

		for (int index = 0; index < 8; index += 4) {
			address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start="
					+ index + "&q=";
			try {
				URL url = new URL(address
						+ URLEncoder.encode(inputQuestion, charset));
				Reader reader = new InputStreamReader(url.openStream(), charset);
				results = new Gson().fromJson(reader, GoogleResults.class);

				urlCount = results.getResponseData().getResults().size();

				checkPageURL(inputQuestion, urlCount);

			} catch (Exception e) {
				System.err.println("internet connection error");
			}
		}

	}

	public ArrayList<String> getSummery() {
		return summery;
	}

	public int getTopIndex() {
		return topAns;
	}
}
