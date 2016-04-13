package eu.ailao.hub.traffic.analyze.dataclases;

import eu.ailao.hub.Statics;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Petr Marek on 4/11/2016.
 * SINGLETON
 * Loads reference question from tsv file
 */
public class LoadedReferenceQuestions {

	private static LoadedReferenceQuestions instance = null;

	private LoadedReferenceQuestions() {
		// Exists only to defeat instantiation.
	}

	public static LoadedReferenceQuestions getInstance() {
		if (instance == null) {
			loadedDataset = loadReferenceQuestions();
			instance = new LoadedReferenceQuestions();
		}
		return instance;
	}

	private static ArrayList<String[]> loadedDataset;

	public ArrayList<String[]> getReferenceQuestion() {
		return loadedDataset;
	}

	public String getQuestion(int i) {
		return loadedDataset.get(i)[0];
	}

	public TrafficTopic getTrafficTopic(int i) {
		return TrafficTopic.valueOf(loadedDataset.get(i)[1]);
	}

	public int size() {
		return loadedDataset.size();
	}

	/**
	 * Load reference question
	 * @return
	 */
	private static ArrayList<String[]> loadReferenceQuestions() {
		ArrayList<String[]> loadedDataset = new ArrayList<>();
		BufferedReader TSVFile = null;
		try {
			TSVFile = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.referenceQuestions), "UTF8"));
			String dataRow = TSVFile.readLine();
			while (dataRow != null) {
				String[] dataArray = dataRow.split("\t");
				loadedDataset.add(new String[]{dataArray[0], dataArray[1]});
				dataRow = TSVFile.readLine();
			}
			TSVFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loadedDataset;
	}
}
