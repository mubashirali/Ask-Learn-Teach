package cosine;

import java.util.ArrayList;

public class ConsineScore {
	public static int CosineMain(ArrayList<String> sentences) throws Exception {

		String[] result = sentences.toArray(new String[sentences.size()]);

		/*for (String string : result) {
			System.out.println("-------------------" + string);
		}*/

		/*
		 * for (int index = 0; index < result.length; index++) { result[index] =
		 * new StopWords().removeStopWords(result[index]); }
		 */

		DocumentParser dp = new DocumentParser();
		dp.parseFiles(result);
		dp.tfIdfCalculator(); // calculates TF-IDF
		float[] score = dp.getCosineSimilarity(result);

		int maxIndex = getMax(score);
		// System.out.println(result[maxIndex]);
		return maxIndex;

	}

	private static int getMax(float[] score) {
		int max = 0;
		for (int i = 1; i < score.length; i++) {
			if (score[max] < score[i]) {
				max = i;
			}
		}
		return max;
	}
}
