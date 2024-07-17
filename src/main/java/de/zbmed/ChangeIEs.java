package de.zbmed;

import java.io.File;
import java.util.List;

import org.w3c.dom.*;

import de.zbmed.utilities.CsvHelper;
import de.zbmed.utilities.IeHelper;
import de.zbmed.utilities.XmlHelper;

public class ChangeIEs {
	private static String getIdFromMd(String iePid, String rosettaInstance) throws Exception {
		String md = IeHelper.getMD(iePid, rosettaInstance);
		// System.out.println(md);
		Document doc = XmlHelper.parse(md);
		Node node = doc;
		node = XmlHelper.getFirstChildByName(node, "mets:mets");
		node = XmlHelper.getFirstChildByNameWithAttrStartsWithValue(node, "mets:dmdSec", "ID", "ie-dmd");
		node = XmlHelper.getFirstChildByName(node, "mets:mdWrap");
		node = XmlHelper.getFirstChildByName(node, "mets:xmlData");
		node = XmlHelper.getFirstChildByName(node, "dc:record");
		node = XmlHelper.getFirstChildByNameWithAttrValue(node, "dc:identifier", "xsi:type", "dcterms:URI");
		if (node == null) {
			throw new Exception("Ein solcher dc:identifier konnte nicht gefunden werden");
		}
		String identifier = node.getTextContent();
		String suchstringa = "http://portal.dimdi.de/de/hta/hta_berichte/hta";
		String suchstringb = "https://portal.dimdi.de/de/hta/hta_berichte/hta";
		if (!identifier.startsWith(suchstringa) && !identifier.startsWith(suchstringb)) {
			throw new Exception("Identifier beginnt falsch: " + identifier);
		}
		int laenge = identifier.startsWith(suchstringa) ? suchstringa.length() : suchstringb.length();
		identifier = identifier.substring(laenge);
		suchstringa = "_bericht_de.pdf";
		suchstringb = "_addendum_de.pdf";
		if (!identifier.endsWith(suchstringa) && !identifier.endsWith(suchstringb)) {
			throw new Exception("Identifier ender falsch: " + identifier);
		}
		return identifier.substring(0, identifier.length() - 7);
	}

	private static String id2UserDefinedA(String id) throws Exception {
		return "GMSHTA_".concat(id);
	}

	public static void main(String[] args) throws Exception {
		String csvDatei = "Test.csv";
		String rosettaInstance = "dev";
//		String csvDatei = "HTA-Berichte_nur_IE_PIDs.csv";
//		String rosettaInstance = "prod";
		List<String> iePids = CsvHelper.readCsvEinspaltig(new File(csvDatei));
		for (String iePid : iePids) {
			System.out.println("'" + iePid + "'");
//			IeHelper.updateIeAmd(iePid, rosettaInstance, "dc:language", "dc:language2", false);
//			IeHelper.updateIeAmd(iePid, rosettaInstance, "dc:medium", "dcterms:medium", false);
//			IeHelper.rollbackIE(iePid, rosettaInstance);
			String uda = id2UserDefinedA(getIdFromMd(iePid, rosettaInstance));
//			IeHelper.updateUserDefined(iePid, rosettaInstance, "A", uda, true);
		}
	}
}
