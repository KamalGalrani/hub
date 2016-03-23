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
		if (args.length < 1) {
			System.err.println("Insert .tsv file as argument please.");
			System.exit(-1);
		}
		String tsvFile = args[0];
		Main_TrafficTest main_trafficTest = new Main_TrafficTest();

		main_trafficTest.test(tsvFile);
	}

	private void test(String tsvFile) {
		int totalQuestions = 0;
		int correctTopics = 0;
		int correctStreets = 0;

		BufferedReader TSVFile = null;
		try {
			TSVFile = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFile), "UTF8"));
			String dataRow = TSVFile.readLine();

			while (dataRow != null) {
				String[] dataArray = dataRow.split("\t");
				boolean[] result = testQuestion(totalQuestions, dataArray[0], dataArray[1], dataArray[2]);
				if (result[TOPIC]) {
					correctTopics++;
				}
				if (result[STREET]) {
					correctStreets++;
				}
				totalQuestions++;
				dataRow = TSVFile.readLine();
			}

			TSVFile.close();

			System.out.println("Topic score: " + (float) correctTopics / (float) totalQuestions);
			System.out.println("Street score: " + (float) correctStreets / (float) totalQuestions);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean[] testQuestion(int questionNumber, String question, String topic, String street) {
		boolean[] results = new boolean[2];
		QuestionAnalyzer questionAnalyzer = new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		if (trafficQuestionInfo.getTrafficTopic().toString().equals(topic)) {
			results[TOPIC] = true;
		}
		if (trafficQuestionInfo.getStreetName() != null && trafficQuestionInfo.getStreetName().equals(street)) {
			results[STREET] = true;
		}
		System.out.println(questionNumber + ") " + question + " " + topic + " " + street);
		System.out.println("Detected: " + trafficQuestionInfo.getTrafficTopic() + " " + trafficQuestionInfo.getStreetName());
		System.out.println();
		return results;
	}
}