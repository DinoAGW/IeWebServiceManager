package de.zbmed;

import java.io.File;
import java.util.List;

import org.w3c.dom.*;

import de.zbmed.utilities.CsvHelper;
import de.zbmed.utilities.IeHelper;
import de.zbmed.utilities.XmlHelper;

public class ChangeIEs {
	private static String getIdFromMdHta(String iePid, String rosettaInstance) throws Exception {
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

	private static String getIdFromMdJournals(String iePid, String rosettaInstance, String md) throws Exception {
		if (md == null) md = IeHelper.getMD(iePid, rosettaInstance);
		// System.out.println(md);
		Document doc = XmlHelper.parse(md);
		Node node = doc;
		node = XmlHelper.getFirstChildByName(node, "mets:mets");
		node = XmlHelper.getFirstChildByNameWithAttrStartsWithValue(node, "mets:dmdSec", "ID", "ie-dmd");
		node = XmlHelper.getFirstChildByName(node, "mets:mdWrap");
		node = XmlHelper.getFirstChildByName(node, "mets:xmlData");
		node = XmlHelper.getFirstChildByName(node, "dc:record");
		node = XmlHelper.getFirstChildByNameWithTextContains(node, "dc:identifier", "/journals/");
		if (node == null) {
			throw new Exception("Ein solcher dc:identifier konnte nicht gefunden werden");
		}
		String identifier = node.getTextContent();
		if (!identifier.startsWith("http://www.egms.de/en/journals/")) {
			throw new Exception("Identifier beginnt nicht wie erwartet: " + identifier);
		}
		if (!identifier.endsWith(".shtml")) {
			throw new Exception("Identifier endet nicht wie erwartet: " + identifier);
		}
		int anf = identifier.lastIndexOf("/");
		return identifier.substring(anf + 1, identifier.length() - 6);
	}

	private static String id2UserDefinedAHta(String id) throws Exception {
		return "GMSHTA_".concat(id);
	}

	private static String id2UserDefinedAJournals(String id) throws Exception {
		return "GMSJN_".concat(id);
	}
	
	private static String id2UserDefinedBJournals(String iePid,String rosettaInstance, String md) throws Exception{
		if (md == null) md = IeHelper.getMD(iePid, rosettaInstance);
//		 System.out.println(md);
		Document doc = XmlHelper.parse(md);
		Node node = doc;
		node = XmlHelper.getFirstChildByName(node, "mets:mets");
		node = XmlHelper.getFirstChildByNameWithAttrStartsWithValue(node, "mets:amdSec", "ID", "ie-amd");
		node = XmlHelper.getFirstChildByNameWithAttrValue(node, "mets:techMD", "ID", "ie-amd-tech");
		node = XmlHelper.getFirstChildByName(node, "mets:mdWrap");
		node = XmlHelper.getFirstChildByName(node, "mets:xmlData");
		node = XmlHelper.getFirstChildByName(node, "dnx");
		node = XmlHelper.getFirstChildByNameWithAttrValue(node, "section", "id", "objectCharacteristics");
		node = XmlHelper.getFirstChildByName(node, "record");
		node = XmlHelper.getFirstChildByNameWithAttrValue(node, "key", "id", "creationDate");
		String date = node.getTextContent();
		if (date.charAt(4) != '-') throw new Exception("Unerwartetes Format = " + date);
		if (date.charAt(7) != '-') throw new Exception("Unerwartetes Format = " + date);
		if (date.charAt(10) != ' ') throw new Exception("Unerwartetes Format = " + date);
		int intDate = Integer.parseInt(date.substring(0, 4).concat(date.substring(5, 7)).concat(date.substring(8, 10)));
		return Integer.toString(intDate - 1);
	}

	public static void main(String[] args) throws Exception {
		String csvDatei = "Journals_mit_UDA.csv";
		String rosettaInstance = "prod";
//		String csvDatei = "HTA-Berichte_nur_IE_PIDs.csv";
//		String rosettaInstance = "prod";
		List<String> rows = CsvHelper.readCsvEinspaltig(new File(csvDatei));
		boolean skip = true;
		for (String row : rows) {
			String iePid = row.substring(0, row.indexOf(","));
			String uda = row.substring(row.indexOf(",") + 1);
			if (iePid.contentEquals("IE3087348")) skip = false;
//			if (iePid.contentEquals("IE3087348")) continue;
			if (skip) continue;
			String md = IeHelper.getMD(iePid, rosettaInstance);
//			System.out.println("'" + iePid + "'");
//			IeHelper.updateIeAmd(iePid, rosettaInstance, "dc:language", "dc:language2", false);
//			IeHelper.updateIeAmd(iePid, rosettaInstance, "dc:medium", "dcterms:medium", false);
//			IeHelper.rollbackIE(iePid, rosettaInstance);
//			String uda = id2UserDefinedAHta(getIdFromMdHta(iePid, rosettaInstance));
//			String uda = id2UserDefinedAJournals(getIdFromMdJournals(iePid, rosettaInstance));
			String udb = id2UserDefinedBJournals(iePid, rosettaInstance, md);
			System.out.println(iePid + "," + udb);
			IeHelper.updateUserDefined(iePid, rosettaInstance, "B", udb, true, md);
		}
	}
}
