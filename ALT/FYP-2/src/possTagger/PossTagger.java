package possTagger;

import java.util.ArrayList;
import java.util.Collections;
import wordChecker.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PossTagger {

	private static ArrayList<String> taggedList = new ArrayList<String>();
	private static ArrayList<String> returnTagges = new ArrayList<String>();

	public ArrayList<String> getTagged(String question, MaxentTagger tagger) {

		try {
			// The tagged string
			String taggedWords = tagger.tagString(question);
			Collections.addAll(taggedList, taggedWords.split(" "));

			for (String str : taggedList) {
				// System.out.print(str);
				if (str.contains("_V") || str.contains("_JJ"))
					returnTagges.add(str.substring(0, str.indexOf('_')));

			}
			return returnTagges;
		} catch (Exception e) {
			return returnTagges;
		}
	}

	public boolean getSentence(String word, MaxentTagger tagger) {

		try {

			WordChecker check = new WordChecker();

			if (check.GetWord(word)) {
				// The tagged string
				String taggedWord = tagger.tagString(word);

				if (!taggedWord
						.matches(".*(_IN|_JJR|_RBS|_UH|_RBR|_MD|_JJS|_FW|_DT|_CC).*")) {
					//System.out.println(taggedWord);
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}
}
