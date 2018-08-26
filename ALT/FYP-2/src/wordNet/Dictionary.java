package wordNet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.WuPalmer;

import wordNet.Wordnet;

public class Dictionary {
	private static ILexicalDatabase db = new NictWordNet();
	// private static RelatednessCalculator lin = new Lin(db);
	private static RelatednessCalculator wup = new WuPalmer(db);
	private static RelatednessCalculator path = new Path(db);

	private static Wordnet wordnet;

	public static void initalize() {
		try {
			wordnet = new Wordnet();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getSynonyms(ArrayList<String> list) {
		// System.out.println("Test " + (new File(".")).getAbsolutePath());
		ArrayList<String> keyWords = new ArrayList<String>();
		for (String string : list) {

			String lemma = string;
			StringBuffer sb = new StringBuffer("");
			sb.append(lemma);
			Vector<String> synonym = wordnet.getSynonym(lemma);
			double[] score = new double[synonym.size()];
			int i = 0;
			for (String s : synonym) {
				if (s.contains(sb)) {
					score[i] = 0.0;
					i++;
					continue;
				}
				score[i] += wup.calcRelatednessOfWords(lemma, s);
				score[i] += path.calcRelatednessOfWords(lemma, s);
				System.out.println(s + " Score: " + score[i]);
				i++;
			}

			if (score.length > 0) {
				double max = score[0];
				int index = 0;
				for (int j = 1; j < score.length; j++) {
					if (max < score[j]) {
						max = score[j];
						index = j;
					}
				}

				// System.out.println("Max Score: "+ synonym.get(index) + " -> "
				// +
				// score[index] + " Index:" + index);
				keyWords.add(synonym.get(index));
			}
		}
		return keyWords;

	}
}
