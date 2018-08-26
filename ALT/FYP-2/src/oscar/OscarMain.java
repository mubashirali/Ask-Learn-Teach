package oscar;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.ch.wwmm.oscar.Oscar;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ChemicalStructure;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.FormatType;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity;

public class OscarMain {
	private static Oscar oscar = new Oscar();

	public void initalize() {
		oscar.getRecogniser();
	}

	public ArrayList<String> tagChemistryKeyWords(String query) {
		// String s = "acid and base molecule";

		ArrayList<String> keyWords = new ArrayList<String>();

		try {
			List<ResolvedNamedEntity> entities = oscar
					.findAndResolveNamedEntities(query);
			for (ResolvedNamedEntity ne : entities) {
				keyWords.add(ne.getSurface().toString());
			}
			if (query.matches("(.*)chemistry(.*)")
					|| query.contains("chemistry"))
				keyWords.add("chemistry");

			return keyWords;

		} catch (Exception e) {
			return keyWords;
		}

	}

	public String chemicalFormula(String keyWords) {
		String formula = null;
		try {
			List<ResolvedNamedEntity> entities = oscar
					.findAndResolveNamedEntities(keyWords);
			for (ResolvedNamedEntity ne : entities) {
				ChemicalStructure inchi = ne
						.getFirstChemicalStructure(FormatType.INCHI);
				if (inchi != null) {

					String[] str = inchi.getValue().split("/");
					formula = ne.getSurface() + ": " + str[1];
				}
			}
		} catch (Exception e) {
			return formula;
		}
		return formula;
	}
}
