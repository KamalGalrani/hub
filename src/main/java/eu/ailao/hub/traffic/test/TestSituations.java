package eu.ailao.hub.traffic.test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Petr Marek on 3/29/2016.
 */
public class TestSituations {

	public JSONObject getTrafficIncidents() {
		File file = new File("src/main/java/eu/ailao/hub/traffic/test/Incidents.txt");
		return new JSONObject(readFile(file));
	}

	private String readFile(File file) {
		String result = "";

		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(file));

			while ((sCurrentLine = br.readLine()) != null) {
				result += sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}


}
