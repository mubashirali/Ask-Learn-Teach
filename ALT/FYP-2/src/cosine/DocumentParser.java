package cosine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentParser {

	// This variable will hold all terms of each document in an array.
	private List<String[]> termsDocsArray = new ArrayList<String[]>();
	private List<String> allTerms = new ArrayList<String>(); // to hold all
	// terms
	private List<double[]> tfidfDocsVector = new ArrayList<double[]>();

	private List<Integer> fileNameList = new ArrayList<Integer>();

	public void parseFiles(String[] result) throws FileNotFoundException,
	IOException {

		@SuppressWarnings("unused")
		BufferedReader in = null;
		for (int j = 0; j < result.length; j++) {
			fileNameList.add(j);
			String[] tokenizedTerms = result[j].replaceAll("[\\W&&[^\\s]]", "")
					.split("\\W+");

			for (String term : tokenizedTerms) {
				if (!allTerms.contains(term)) { // avoid duplicate entry
					allTerms.add(term);
				}
			}
			termsDocsArray.add(tokenizedTerms);
		}
	}

	/**
	 * Method to create termVector according to its tfidf score.
	 */
	public void tfIdfCalculator() {
		double tf; // term frequency
		double idf; // inverse document frequency
		double tfidf; // term requency inverse document frequency
		for (String[] docTermsArray : termsDocsArray) {
			double[] tfidfvectors = new double[allTerms.size()];
			int count = 0;
			for (String terms : allTerms) {
				tf = new TfIdf().tfCalculator(docTermsArray, terms);
				idf = new TfIdf().idfCalculator(termsDocsArray, terms);
				tfidf = tf * idf;
				tfidfvectors[count] = tfidf;
				count++;
			}
			tfidfDocsVector.add(tfidfvectors); // storing document vectors;
		}
	}

	/**
	 * Method to calculate cosine similarity between all the documents.
	 */

	public float[] getCosineSimilarity(String[] result) {
		float[] ResScore = new float[(tfidfDocsVector.size() - 1)];
		int j = tfidfDocsVector.size() - 1;

		//System.out.println("COSINE SIMILARITY: size" + tfidfDocsVector.size());

		// System.out.println("size "+tfidfDocsVector.size());
		for (int i = 0; i < tfidfDocsVector.size() - 1; i++) {
			// for (int j = 0; j < tfidfDocsVector.size(); j++) {
			float cosineResult = (float) new CosineSimilarity()
			.cosineSimilarity(tfidfDocsVector.get(i),
					tfidfDocsVector.get(j));
			ResScore[i] = cosineResult;

			/*System.out.println("B/w " + fileNameList.get(i) + " & "
					+ fileNameList.get(j) + " = " + cosineResult);*/
		}

		return (ResScore);

	}

}
