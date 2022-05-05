import java.net.URL;

import javax.xml.namespace.QName;

import com.exlibris.dps.IEWebServices_Service;
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
		if (!rosettaInstance.contentEquals("prod") ) {
			System.err.println("Bisher ist nur prod unterstützt.");
			throw new Exception();
		}
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
				retval = ieWS.getIEWebServicesPort().getIE(0L, iePid, null);
				repeat = false;
			} catch (Exception e) {
				System.err.println("getIE fehlgeschlagen");
				Thread.sleep(1000);
			}
		}
		if (retval == null) {
			System.err.println("getIE ergab Nullantwort");
			throw new Exception();
		}
		return retval;
	}

	public static void main(String[] args) throws Exception {
		String retval = getIE("IE19664668", "prod");
		System.out.println(retval);
	}

}
