package eu.ailao.hub;

import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.TrafficQuestionInfo;

import java.io.*;

/**
 * Created by Petr Marek on 3/23/2016.
 */
public class Main_TrafficTest {
	private final int TOPIC = 0;
	private final int STREET = 1;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Insert .tsv file and Lookup Service URL as arguments please.");
			System.exit(-1);
		}
		String tsvFile = args[0];
		Statics.labelLookupURL = args[1];

		Main_TrafficTest main_trafficTest = new Main_TrafficTest();

		main_trafficTest.test(tsvFile);
	}

	private void test(String tsvFile) {
		int totalQuestions = 0;
		int correctTopics = 0;
		int correctStreets = 0;
		int correctAll = 0;

		BufferedReader TSVFile = null;
		try {
			TSVFile = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFile), "UTF8"));
			String dataRow = TSVFile.readLine();

			while (dataRow != null) {
				String[] dataArray = dataRow.split("\t");
				String secondStreet = null;
				if (dataArray.length >= 4) {
					secondStreet = dataArray[3];
				}
				boolean[] result = testQuestion(totalQuestions, dataArray[0], dataArray[1], dataArray[2], secondStreet);
				if (result[TOPIC]) {
					correctTopics++;
				}
				if (result[STREET]) {
					correctStreets++;
				}
				if (result[TOPIC] && result[STREET]) {
					correctAll++;
				}
				totalQuestions++;
				dataRow = TSVFile.readLine();
			}

			TSVFile.close();

			System.out.println("Topic score: " + (float) correctTopics / (float) totalQuestions);
			System.out.println("Street score: " + (float) correctStreets / (float) totalQuestions);
			System.out.println("Total score: " + (float) correctAll / (float) totalQuestions);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean[] testQuestion(int questionNumber, String question, String topic, String street, String streetTwo) {
		boolean[] results = new boolean[2];
		QuestionAnalyzer questionAnalyzer = new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		if (trafficQuestionInfo.getTrafficTopic().toString().equals(topic)) {
			results[TOPIC] = true;
		}
		if (streetTwo != null) {
			if (trafficQuestionInfo.getStreetNameFrom() != null && trafficQuestionInfo.getStreetNameFrom().toLowerCase().equals(street.toLowerCase()) &&
					trafficQuestionInfo.getStreetNameTo() != null && trafficQuestionInfo.getStreetNameTo().toLowerCase().equals(streetTwo.toLowerCase())) {
				results[STREET] = true;
			}
		} else {
			if (trafficQuestionInfo.getStreetName() != null && trafficQuestionInfo.getStreetName().toLowerCase().equals(street.toLowerCase())) {
				results[STREET] = true;
			} else if (trafficQuestionInfo.getStreetNameFrom() != null && trafficQuestionInfo.getStreetNameFrom().toLowerCase().equals(street.toLowerCase())) {
				results[STREET] = true;
			}
		}
		if (streetTwo != null) {
			System.out.println(questionNumber + ") " + question + " " + topic + " " + street + " " + streetTwo);
		} else {
			System.out.println(questionNumber + ") " + question + " " + topic + " " + street);
		}
		if (streetTwo != null) {
			System.out.println("Detected: " + trafficQuestionInfo.getTrafficTopic() + " " + trafficQuestionInfo.getStreetNameFrom() + " " + trafficQuestionInfo.getStreetNameTo());
		} else {
			if (trafficQuestionInfo.getStreetName() != null) {
				System.out.println("Detected: " + trafficQuestionInfo.getTrafficTopic() + " " + trafficQuestionInfo.getStreetName());
			} else {
				System.out.println("Detected: " + trafficQuestionInfo.getTrafficTopic() + " " + trafficQuestionInfo.getStreetNameFrom());
			}
		}
		System.out.println("Topic: " + results[TOPIC] + " Street: " + results[STREET]);
		System.out.println();
		return results;
	}
}
