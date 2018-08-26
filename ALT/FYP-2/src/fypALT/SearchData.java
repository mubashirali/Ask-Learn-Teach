package fypALT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cosine.ConsineScore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import possTagger.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import filters.StopWords;

public class SearchData {

	private static Document doc;
	private static StopWords spWords = new StopWords();
	public String DataExtract(String Url, String query,
			PossTagger TaggingClass, MaxentTagger tagger) throws IOException {

		List<String> Stringlist = new ArrayList<String>();
		
		String updateQuery = spWords.removeStopWords(query);
		
		String[] StrQuery = updateQuery.split(" ");
		ArrayList<String> sentenceList = new ArrayList<String>();
		boolean bool = false;
		
		try {
			doc = Jsoup.connect(Url).userAgent("Mozilla").get();
			Elements links = doc.body().select("p");

			for (Element link : links) {
				Stringlist.add(link.text().toLowerCase());
			}

			/*if (Stringlist.size() < 2) {
				Elements divs = doc.body().select("div");

				for (Element div : divs) {
					Stringlist.add(div.text());
				}
			}*/
			
			/* System.out.println(links.text()); */

			for (int index = 0; index < Stringlist.size(); index++) {
				if (Stringlist.get(index).matches("(.*)" + '\u00a9' + "(.*)")
						|| Stringlist
								.get(index)
								.toLowerCase()
								.matches(
										".*(www|html|asp|@|email|http|forum|website|websites|topic|question|photos|videos|shipping|free|member|\\?).*")
						|| Stringlist.get(index).toLowerCase().contains(".com")) {
					
				} else {
					for (String str : StrQuery) {
						if (Stringlist.get(index)
								.matches("(.*)" + str + "(.*)")) {
							 //System.out.println(Stringlist.get(index));
							bool = true;
						}
					}
					if (bool) {
						for (String str : Stringlist.get(index).split("(\\. )")) {
							if (str.length() > 20)
								if (TaggingClass
										.getSentence(str.substring(str
												.lastIndexOf(" ") + 1), tagger)) {
									sentenceList.add(str.toLowerCase());
									// System.out.println(str);
								}
						}
						bool = false;
					}
				}
			}

			if (sentenceList.size() > 1) {
				
				System.out.println(updateQuery +"----------------------------------------------------------*********");
				sentenceList.add(updateQuery);
				return sentenceList.get(ConsineScore.CosineMain(sentenceList));

			} else
				return "0";
		} catch (Exception e) {
			// System.out.println(e);
			return "0";

		}
	}
}
