package de.zbmed.utilities;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import com.exlibris.dps.Action;
import com.exlibris.dps.IEWebServices_Service;
import com.exlibris.dps.IeStatusInfo;
import com.exlibris.dps.MetaData;
import com.exlibris.dps.sdk.pds.HeaderHandlerResolver;

public class IeHelper {
	static final String fs = System.getProperty("file.separator");

	private static String defaultRosettaURL(String rosettaInstance) throws Exception {
		if (rosettaInstance.equals("dev")) {
			return "https://rosetta.develop.lza.tib.eu";
		} else if (rosettaInstance.equals("test")) {
			return "https://rosetta.test.lza.tib.eu";
		} else if (rosettaInstance.equals("prod")) {
			return "https://rosetta.lza.tib.eu";
		} else {
			System.err.println("invalider Wert für rosettaInstance '" + rosettaInstance + "'.");
			throw new Exception();
		}
	}

	private static String defaultInstitution(String rosettaInstance) throws Exception {
		if (rosettaInstance.equals("dev")) {
			return "ZBM";
		} else if (rosettaInstance.equals("test")) {
			return "ZBMED";
		} else if (rosettaInstance.equals("prod")) {
			return "ZBMED";
		} else {
			System.err.println("invalider Wert für rosettaInstance '" + rosettaInstance + "'.");
			throw new Exception();
		}
	}

	private static String defaultUserName(String rosettaInstance) throws Exception {
		if (rosettaInstance.equals("dev")) {
			return "SubApp ZB MED";
		} else if (rosettaInstance.equals("test")) {
			return "SubApp ZB MED";
		} else if (rosettaInstance.equals("prod")) {
			return "SubApp ZB MED";
		} else {
			System.err.println("invalider Wert für rosettaInstance '" + rosettaInstance + "'.");
			throw new Exception();
		}
	}

	private static String defaultPassword(String rosettaInstance) throws Exception {
		if (rosettaInstance.equals("dev")) {
			String propertyDateiPfad = System.getProperty("user.home").concat(fs).concat("Rosetta_Properties.txt");
			PropertiesManager prop = new PropertiesManager(propertyDateiPfad);
			return prop.readStringFromProperty("SubApp_Passwort");
		} else if (rosettaInstance.equals("test")) {
			String propertyDateiPfad = System.getProperty("user.home").concat(fs).concat("Rosetta_Properties.txt");
			PropertiesManager prop = new PropertiesManager(propertyDateiPfad);
			return prop.readStringFromProperty("SubApp_Passwort");
		} else if (rosettaInstance.equals("prod")) {
			String propertyDateiPfad = System.getProperty("user.home").concat(fs).concat("Rosetta_Properties.txt");
			PropertiesManager prop = new PropertiesManager(propertyDateiPfad);
			return prop.readStringFromProperty("SubApp_Passwort");
		} else {
			System.err.println("invalider Wert für rosettaInstance '" + rosettaInstance + "'.");
			throw new Exception();
		}
	}

	private static String defaultIE_WSDL_URL(String rosettaURL) {
		return rosettaURL.concat("/dpsws/repository/IEWebServices?wsdl");
	}

	public static String getIE(String iePid, String rosettaInstance) throws Exception {
		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		Boolean repeat = true;
		String retval = null;
		while (repeat) {
			try {
				Thread.sleep(1000);
				retval = ieWS.getIEWebServicesPort().getIE(null, iePid, null);
				repeat = false;
			} catch (Exception e) {
				System.err.println("getIE fehlgeschlagen");
			}
		}
		if (retval == null) {
			System.err.println("getIE ergab Nullantwort");
			throw new Exception();
		}
		String test = "                <record>\n                  <key id=\"internalIdentifierType\">PID</key>\n                  <key id=\"internalIdentifierValue\">"
				+ iePid + "</key>\n                </record>";
		if (!retval.contains(test)) {
			System.err.println("getIE Antwort hat Test nicht bestanden.");
			throw new Exception();
		}
		return retval;
	}

	public static String getMD(String iePid, String rosettaInstance) throws Exception {
		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		String retval = ieWS.getIEWebServicesPort().getMD(null, iePid, null, null, null);
		return retval;
	}

	public static String lockIE(String iePid, String rosettaInstance) throws Exception {
		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		Action action = Action.valueOf("LOCK");
		IeStatusInfo iesi = ieWS.getIEWebServicesPort().manageIE(action, iePid, null);
		return iesi.getLockedBy();
	}

	public static String rollbackIE(String iePid, String rosettaInstance) throws Exception {
		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		Action action = Action.valueOf("ROLLBACK");
		IeStatusInfo iesi = ieWS.getIEWebServicesPort().manageIE(action, iePid, null);
		return iesi.getLockedBy();
	}

	public static String commitIE(String iePid, String rosettaInstance) throws Exception {
		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		Action action = Action.valueOf("COMMIT");
		IeStatusInfo iesi = ieWS.getIEWebServicesPort().manageIE(action, iePid, null);
		return iesi.getLockedBy();
	}

	public static void updateIeAmd(String iePid, String rosettaInstance, String von, String nach, Boolean commit)
			throws Exception {
		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		String md = getMD(iePid, rosettaInstance);
		List<MetaData> metadata = new ArrayList<>();
		MetaData md1 = new MetaData();
		md1.setType("descriptive");
		md1.setSubType("dc");
//		md1.setMid(getIeDmdMid(md));
		md1.setContent(changeIeAmd(getIeDmd(md), von, nach));
		metadata.add(md1);
		ieWS.getIEWebServicesPort().updateMD(commit, metadata, iePid, null);
	}

	public static String getIeDmd(String md) throws Exception {
		String suchstring = "<mets:dmdSec";
		int anf = md.indexOf(suchstring);
		if (anf == -1)
			throw new Exception("Suchstring konnte nicht gefunden werden");
		if (md.indexOf(suchstring, anf + 1) != -1)
			throw new Exception("Suchstring wurde mehr als ein Mal gefunden");
		suchstring = "</mets:dmdSec>";
		int end = md.indexOf(suchstring, anf) + suchstring.length();
		String davor = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><mets:mets xmlns:mets=\"http://www.loc.gov/METS/\">";
		String danach = "</mets:mets>";
		return davor + md.substring(anf, end) + danach;
	}

	public static String changeIeAmd(String md, String von, String nach) {
		return md.replace("<" + von + ">", "<" + nach + ">").replace("</" + von + ">", "</" + nach + ">").replace("<" + von + "/>", "<" + nach + "/>");
	}

	public static String getIeDmdMid(String md) throws Exception {
		String suchstring = "ie-dmd-MID-";
		int anf = md.indexOf(suchstring);
		if (anf == -1)
			throw new Exception("Suchstring konnte nicht gefunden werden");
		if (md.indexOf(suchstring, anf + 1) != -1)
			throw new Exception("Suchstring wurde mehr als ein Mal gefunden");
		anf += suchstring.length();
		int end = md.indexOf("\"", anf);
		return md.substring(anf, end);
	}

	public static void updateUserDefined(String iePid, String rosettaInstance, String ABorC, String wert,
			Boolean commit) throws Exception {
		if (!ABorC.contentEquals("A") && !ABorC.contentEquals("B") && !ABorC.contentEquals("C"))
			throw new Exception("ABorC sollte A, B oder C sein, ist aber '" + ABorC + "'");

		final String rosettaURL = defaultRosettaURL(rosettaInstance);
		final String institution = defaultInstitution(rosettaInstance);
		final String userName = defaultUserName(rosettaInstance);
		final String password = defaultPassword(rosettaInstance);
		final String IE_WSDL_URL = defaultIE_WSDL_URL(rosettaURL);

		IEWebServices_Service ieWS = new IEWebServices_Service(new URL(IE_WSDL_URL),
				new QName("http://dps.exlibris.com/", "IEWebServices"));
		ieWS.setHandlerResolver(new HeaderHandlerResolver(userName, password, institution));

		String md = getMD(iePid, rosettaInstance);

		ieWS.getIEWebServicesPort().updateDNX(commit, changeUserDefined2(md, ABorC, wert), iePid, null);
	}

	private static String changeUserDefined2(String md, String ABorC, String wert) throws Exception {
		if (!ABorC.contentEquals("A") && !ABorC.contentEquals("B") && !ABorC.contentEquals("C"))
			throw new Exception("ABorC sollte A, B oder C sein, ist aber '" + ABorC + "'");
		String id = "UserDefined".concat(ABorC);

		Document doc = XmlHelper.parse(md);
		String vorher = XmlHelper.getStringFromDocument(doc);
//		System.out.println(vorher);
		
		Node node = doc;
		node = XmlHelper.getFirstChildByName(node, "mets:mets");
		node = XmlHelper.getFirstChildByNameWithAttrStartsWithValue(node, "mets:amdSec", "ID", "ie-amd");
		node = XmlHelper.getFirstChildByNameWithAttrValue(node, "mets:techMD", "ID", "ie-amd-tech");
		node = XmlHelper.getFirstChildByName(node, "mets:mdWrap");
		node = XmlHelper.getFirstChildByName(node, "mets:xmlData");
		Node dnx = XmlHelper.getFirstChildByName(node, "dnx");
		Node section = XmlHelper.getFirstChildByNameWithAttrValue(dnx, "section", "id", "generalIECharacteristics");
		if(section == null) {
			section = dnx.appendChild(XmlHelper.newNodeWithAttr(doc, "section", "id", "generalIECharacteristics"));
		}
		Node record = XmlHelper.getFirstChildByName(section, "record");
		if (record == null) {
			record = section.appendChild(XmlHelper.newNode(doc, "record"));
		}
		Node key = XmlHelper.getFirstChildByNameWithAttrValue(record, "key", "id", id);
		if (key == null) {
			key = record.appendChild(XmlHelper.newNodeWithAttr(doc, "key", "id", id));
		}
		
		key.setTextContent(wert);

		String nachher = XmlHelper.getStringFromDocument(doc);
//		System.out.println(nachher);
//		printDiff(vorher, nachher);
//		printDiff(md, nachher);
		return nachher;
	}

	private static String changeUserDefined(String md, String ABorC, String wert) throws Exception {
		if (!ABorC.contentEquals("A") && !ABorC.contentEquals("B") && !ABorC.contentEquals("C"))
			throw new Exception("ABorC sollte A, B oder C sein, ist aber '" + ABorC + "'");

		String suchstring;
		int anf, end;

		String vorher;
		String suchbereich = md;
		String nachher;

		suchstring = "<mets:techMD ID=\"ie-amd-tech\">";
		anf = suchbereich.indexOf(suchstring);
		if (anf == -1) {
			throw new Exception("Konnte keine ie-amd-tech finden - das sollte nicht sein");
		}
		vorher = suchbereich.substring(0, anf + suchstring.length());
		suchbereich = suchbereich.substring(anf + suchstring.length());

		suchstring = "</mets:techMD>";
		end = suchbereich.indexOf(suchstring);
		nachher = suchbereich.substring(end);
		suchbereich = suchbereich.substring(0, end);

		// Jetzt bin ich innerhalb der ie-amd-techMD
//		System.out.println("276:\n" + vorher + "$" + suchbereich + "$" + nachher);

		suchstring = "<section id=\"generalIECharacteristics\">";
		anf = suchbereich.indexOf(suchstring);
		if (anf == -1) {
			// Füge GeneralIECharacteristics Section hinzu
			suchstring = "<dnx version=\"5.0\" xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">";
			anf = suchbereich.indexOf(suchstring);
			vorher = vorher.concat(suchbereich.substring(0, anf + suchstring.length()))
					.concat("<section id=\"generalIECharacteristics\"><record>");
			suchbereich = "</record></section>".concat(suchbereich.substring(anf + suchstring.length()));
		} else {
			suchstring = "<record>";
			anf = suchbereich.indexOf(suchstring, anf);
			vorher = vorher.concat(suchbereich.substring(0, anf + suchstring.length()));
			suchbereich = suchbereich.substring(anf + suchstring.length());
		}

		suchstring = "</record>";
		end = suchbereich.indexOf(suchstring);
		nachher = suchbereich.substring(end).concat(nachher);
		suchbereich = suchbereich.substring(0, end);

		// Jetzt bin ich innerhalb der generalIECharacteristics Section
		System.out.println("297:\n" + vorher + "$" + suchbereich + "$" + nachher);

		suchstring = "<key id=\"UserDefined" + ABorC + "\">";
		anf = suchbereich.indexOf(suchstring);
		if (anf == -1) {
			// Füge UserDefined Hinzu
			suchbereich = suchstring + wert + "</key>";
			System.out.println("Geplant:\n" + vorher + "$" + suchbereich + "$" + nachher);
			return vorher.concat(suchbereich).concat(nachher);
		} else {
			anf = md.indexOf(suchstring) + suchstring.length();
			if (md.indexOf(suchstring, anf) != -1)
				throw new Exception("Suchstring wurde mehr als ein Mal gefunden");
			suchstring = "</key>";
			end = md.indexOf(suchstring, anf);
			return md.substring(0, anf) + wert + md.substring(end);
		}
	}

	public static void printDiff(String vorher, String nachher) {
		int anf = -1;
		for (int i = 0; i < vorher.length() && i < nachher.length(); ++i) {
			if (vorher.charAt(i) != nachher.charAt(i)) {
				anf = i;
				break;
			}
		}
		if (anf == -1) {
			System.out.println("Unverändert");
			return;
		}
		int end = -1;
		for (int i = 0; i < vorher.length() && i < nachher.length(); ++i) {
			if (vorher.charAt(vorher.length() - i - 1) != nachher.charAt(nachher.length() - i - 1)) {
				end = i + 1;
				break;
			}
		}
		System.out.println("==========================  vorher ===========================");
		if (anf + end <= vorher.length()) {
			System.out.println(vorher.substring(anf - 5, vorher.length() - end + 6));
		} else {
			System.out.println("Hier nichts Neues");
		}
		System.out.println("========================== nachher ===========================");
		if (anf + end <= nachher.length()) {
			System.out.println(nachher.substring(anf - 5, nachher.length() - end + 6));
		} else {
			System.out.println("Hier nichts Neues");
		}
	}

	public static void main(String[] args) throws Exception {
//		System.out.println(getIE("IE28266070", "prod"));
		String devIe = "IE1457252";
//		String devIe = "IE1457253";
//		System.out.println(getIE(devIe, "dev"));
//		System.out.println(getMD(devIe, "dev"));
		String vorher = getMD(devIe, "dev");
		System.out.println(vorher);
		String wert = "anders10";
//		printDiff(changeUserDefined(vorher, "C", wert), changeUserDefined2(vorher, "C", wert));
//		System.out.println(changeUserDefined(vorher, "C", wert));
//		System.out.println(changeUserDefined2(vorher, "C", wert));
//		System.out.println(lockIE(devIe, "dev"));
//		System.out.println(rollbackIE(devIe, "dev"));
//		updateIeAmd(devIe, "dev", "dc:language", "dc:language1", false);
		updateUserDefined(devIe, "dev", "A", wert, true);
//		System.out.println(getMD(devIe, "dev"));
//		System.out.println(commitIE(devIe, "dev"));
//		String nachher = getMD(devIe, "dev");
//		printDiff(vorher, nachher);
	}

}
