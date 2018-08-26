package wordChecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.softcorporation.suggester.util.Constants;
import com.softcorporation.suggester.util.BasicSuggesterConfiguration;
import com.softcorporation.suggester.dictionary.BasicDictionary;
import com.softcorporation.suggester.BasicSuggester;

public class WordChecker {
	public boolean GetWord(String word) {

		try {
			String dictFileName = "file://WordChecker/english.jar";

			try (BufferedReader keyboardInput = new BufferedReader(
					new InputStreamReader(System.in,
							Constants.CHARACTER_SET_ENCODING_DEFAULT))) {
				BasicDictionary dictionary = new BasicDictionary(dictFileName);

				BasicSuggesterConfiguration configuration = new BasicSuggesterConfiguration(
						"file://WordChecker/basicSuggester.config");

				BasicSuggester suggester = new BasicSuggester(configuration);
				suggester.attach(dictionary);

				// System.out.println("\nword: " + word);

				int result = suggester.hasWord(word);
				if (result == Constants.RESULT_ID_MATCH_EXACT) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

		} catch (Exception e) {
			System.err.println("Error: " + e);
			return false;
		}
	}
}
