package eu.ailao.hub.traffic.analyze.dataclases;

import eu.ailao.hub.traffic.analyze.TrafficTopic;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Petr Marek on 4/11/2016.
 */
public class LoadedDataset {

	ArrayList<String[]> loadedDataset;

	public LoadedDataset() {
		loadedDataset = loadDataset();
	}

	public ArrayList<String[]> getLoadedDataset() {
		return loadedDataset;
	}

	public String getQuestion(int i) {
		return loadedDataset.get(i)[0];
	}

	public TrafficTopic getTrafficTopic(int i) {
		return TrafficTopic.valueOf(loadedDataset.get(i)[1]);
	}

	public int size(){
		return loadedDataset.size();
	}

	private ArrayList<String[]> loadDataset() {
		ArrayList<String[]> loadedDataset = new ArrayList<>();
		BufferedReader TSVFile = null;
		try {
			//TODO send tsv file as program argument
			TSVFile = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\ermrk\\OneDrive\\Dokumenty\\TrafficDataset.tsv"), "UTF8"));
			String dataRow = TSVFile.readLine();
			while (dataRow != null) {
				String[] dataArray = dataRow.split("\t");
				loadedDataset.add(new String[]{dataArray[0],dataArray[1]});
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
