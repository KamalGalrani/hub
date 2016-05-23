package eu.ailao.hub.testing;

import eu.ailao.hub.Statics;
import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.dataclases.TrafficQuestionInfo;

import java.io.*;

/**
 * Created by Petr Marek on 5/18/2016.
 * Class computing precision and recall of traffic topics
 */
public class Main_TrafficTest_TopicPrecisionRecall {

	private final int QUESTION = 0;
	private final int TOPIC = 1;

	private final int TRAFIC_SITUATION = 0;
	private final int INCIDENT = 1;
	private final int CONSTRUCTION = 2;
	private final int CLOSURE = 3;
	private final int RESTRICTION_END = 4;
	private final int FASTEST_ROUTE = 5;

	private final int CORRECTLY_CLASSIFIED = 0;
	private final int ALL_CLASIFIED_AS_CLASS = 1;
	private final int TOTAL_IN_CLASS = 2;

	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Insert test question .tsv file, Lookup Service URL, Dataset-STS URL and reference questions TSV file as arguments please.");
			System.exit(-1);
		}
		String tsvFile = args[0];
		Statics.labelLookupURL = args[1];
		Statics.datasetSTSURL = args[2];
		Statics.referenceQuestions = args[3];

		Main_TrafficTest_TopicPrecisionRecall main_trafficTestTopicPrecisionRecall = new Main_TrafficTest_TopicPrecisionRecall();

		main_trafficTestTopicPrecisionRecall.test(tsvFile);
	}

	private void test(String tsvFile) {
		int totalQuestions = 0;
		int correctTopics = 0;
		int correctStreets = 0;
		int correctAll = 0;
		QuestionAnalyzer questionAnalyzer = new QuestionAnalyzer();

		BufferedReader TSVFile = null;
		int[][] counts = new int[6][3];

		try {
			TSVFile = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFile), "UTF8"));
			String dataRow = TSVFile.readLine();

			while (dataRow != null) {
				String[] dataArray = dataRow.split("\t");
				System.out.println(dataArray[QUESTION]);
				TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(dataArray[QUESTION]);
				trafficQuestionInfo.getTrafficTopic();
				if (dataArray[TOPIC].equals("TRAFFIC_SITUATION")){
					counts[TRAFIC_SITUATION][TOTAL_IN_CLASS]++;
					if(trafficQuestionInfo.getTrafficTopic().toString().equals("TRAFFIC_SITUATION")){
						counts[TRAFIC_SITUATION][CORRECTLY_CLASSIFIED]++;
					}
				}
				if (dataArray[TOPIC].equals("INCIDENT")){
					counts[INCIDENT][TOTAL_IN_CLASS]++;
					if(trafficQuestionInfo.getTrafficTopic().toString().equals("INCIDENT")){
						counts[INCIDENT][CORRECTLY_CLASSIFIED]++;
					}
				}
				if (dataArray[TOPIC].equals("CONSTRUCTION")){
					counts[CONSTRUCTION][TOTAL_IN_CLASS]++;
					if(trafficQuestionInfo.getTrafficTopic().toString().equals("CONSTRUCTION")){
						counts[CONSTRUCTION][CORRECTLY_CLASSIFIED]++;
					}
				}
				if (dataArray[TOPIC].equals("CLOSURE")){
					counts[CLOSURE][TOTAL_IN_CLASS]++;
					if(trafficQuestionInfo.getTrafficTopic().toString().equals("CLOSURE")){
						counts[CLOSURE][CORRECTLY_CLASSIFIED]++;
					}
				}
				if (dataArray[TOPIC].equals("RESTRICTION_END")) {
					counts[RESTRICTION_END][TOTAL_IN_CLASS]++;
					if(trafficQuestionInfo.getTrafficTopic().toString().equals("RESTRICTION_END")){
						counts[RESTRICTION_END][CORRECTLY_CLASSIFIED]++;
					}
				}
				if (dataArray[TOPIC].equals("FASTEST_ROUTE")){
					counts[FASTEST_ROUTE][TOTAL_IN_CLASS]++;
					if(trafficQuestionInfo.getTrafficTopic().toString().equals("FASTEST_ROUTE")){
						counts[FASTEST_ROUTE][CORRECTLY_CLASSIFIED]++;
					}
				}

				if(trafficQuestionInfo.getTrafficTopic().toString().equals("TRAFFIC_SITUATION")){
					counts[TRAFIC_SITUATION][ALL_CLASIFIED_AS_CLASS]++;
				}
				if(trafficQuestionInfo.getTrafficTopic().toString().equals("INCIDENT")){
					counts[INCIDENT][ALL_CLASIFIED_AS_CLASS]++;
				}
				if(trafficQuestionInfo.getTrafficTopic().toString().equals("CONSTRUCTION")){
					counts[CONSTRUCTION][ALL_CLASIFIED_AS_CLASS]++;
				}
				if(trafficQuestionInfo.getTrafficTopic().toString().equals("CLOSURE")){
					counts[CLOSURE][ALL_CLASIFIED_AS_CLASS]++;
				}
				if(trafficQuestionInfo.getTrafficTopic().toString().equals("RESTRICTION_END")){
					counts[RESTRICTION_END][ALL_CLASIFIED_AS_CLASS]++;
				}
				if(trafficQuestionInfo.getTrafficTopic().toString().equals("FASTEST_ROUTE")){
					counts[FASTEST_ROUTE][ALL_CLASIFIED_AS_CLASS]++;
				}

				totalQuestions++;
				dataRow = TSVFile.readLine();
			}

			TSVFile.close();

			System.out.println("TRAFFIC_SITUATION precision " + (float) counts[TRAFIC_SITUATION][CORRECTLY_CLASSIFIED] / (float) counts[TRAFIC_SITUATION][ALL_CLASIFIED_AS_CLASS]);
			System.out.println("TRAFFIC_SITUATION recall " + (float) counts[TRAFIC_SITUATION][CORRECTLY_CLASSIFIED] / (float) counts[TRAFIC_SITUATION][TOTAL_IN_CLASS]);

			System.out.println("INCIDENT precision " + (float) counts[INCIDENT][CORRECTLY_CLASSIFIED] / (float) counts[INCIDENT][ALL_CLASIFIED_AS_CLASS]);
			System.out.println("INCIDENT recall " + (float) counts[INCIDENT][CORRECTLY_CLASSIFIED] / (float) counts[INCIDENT][TOTAL_IN_CLASS]);

			System.out.println("CONSTRUCTION precision " + (float) counts[CONSTRUCTION][CORRECTLY_CLASSIFIED] / (float) counts[CONSTRUCTION][ALL_CLASIFIED_AS_CLASS]);
			System.out.println("CONSTRUCTION recall " + (float) counts[CONSTRUCTION][CORRECTLY_CLASSIFIED] / (float) counts[CONSTRUCTION][TOTAL_IN_CLASS]);

			System.out.println("CLOSURE precision " + (float) counts[CLOSURE][CORRECTLY_CLASSIFIED] / (float) counts[CLOSURE][ALL_CLASIFIED_AS_CLASS]);
			System.out.println("CLOSURE recall " + (float) counts[CLOSURE][CORRECTLY_CLASSIFIED] / (float) counts[CLOSURE][TOTAL_IN_CLASS]);

			System.out.println("RESTRICTION_END precision " + (float) counts[RESTRICTION_END][CORRECTLY_CLASSIFIED] / (float) counts[RESTRICTION_END][ALL_CLASIFIED_AS_CLASS]);
			System.out.println("RESTRICTION_END recall " + (float) counts[RESTRICTION_END][CORRECTLY_CLASSIFIED] / (float) counts[RESTRICTION_END][TOTAL_IN_CLASS]);

			System.out.println("FASTEST_ROUTE precision " + (float) counts[FASTEST_ROUTE][CORRECTLY_CLASSIFIED] / (float) counts[FASTEST_ROUTE][ALL_CLASIFIED_AS_CLASS]);
			System.out.println("FASTEST_ROUTE recall " + (float) counts[FASTEST_ROUTE][CORRECTLY_CLASSIFIED] / (float) counts[FASTEST_ROUTE][TOTAL_IN_CLASS]);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean testQuestion(int questionNumber, String question, String topic) {
		boolean result=false;
		QuestionAnalyzer questionAnalyzer = new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		if (trafficQuestionInfo.getTrafficTopic().toString().equals(topic)) {
			result = true;
		}
		System.out.println("Topic: " + result);
		System.out.println();
		return result;
	}
}
