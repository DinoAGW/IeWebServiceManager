package de.zbmed.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Stack;

public class CsvHelper {
	
	public static List<String> readCsvEinspaltig(File csvFile) throws Exception {
		if (!csvFile.exists()) {
			throw new Exception("Detei " + csvFile.getPath() + " existiert nicht");
		}
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		List<String> ret = new Stack<>();
		String line;
		while ((line = br.readLine()) != null) {
			ret.add(line);
		}
		br.close();
		return ret;
	}

	public static void main(String[] args) throws Exception {
		String csvDatei = "Test.csv";
		List<String> iePids = readCsvEinspaltig(new File(csvDatei));
		for (String iePid : iePids) {
			System.out.println(iePid);
		}
	}

}
