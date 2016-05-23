package eu.ailao.hub.testing;

import eu.ailao.hub.Statics;
import eu.ailao.hub.traffic.analyze.StreetAnalyzer;
import eu.ailao.hub.traffic.analyze.TopicAnalyzer;
import eu.ailao.hub.traffic.analyze.dataclases.StreetCandidate;
import org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Petr Marek on 5/6/2016.
 * The class which computes precision and recall of traffic domain
 */
public class Main_TrafficTest_DomainPrecisionRecall {

	public static void main(String[] args) {
		if (args.length < 5) {
			System.err.println("Insert test traffic question .tsv file, test movies question .tsv file, Lookup Service URL, Dataset-STS URL and reference questions .tsv file as arguments please.");
			System.exit(-1);
		}
		String trafficQuestionsFile = args[0];
		String moviesQuestionsFile = args[1];
		Statics.labelLookupURL = args[2];
		Statics.datasetSTSURL = args[3];
		Statics.referenceQuestions = args[4];

		Main_TrafficTest_DomainPrecisionRecall main_trafficTestDomainPrecisionRecall = new Main_TrafficTest_DomainPrecisionRecall();
		ArrayList<String> trafficQuestions = main_trafficTestDomainPrecisionRecall.loadTrafficDataset(trafficQuestionsFile);
		ArrayList<String> moviesQuestions = main_trafficTestDomainPrecisionRecall.loadMoviesDataset(moviesQuestionsFile);

		ArrayList<Double> trafficTopicProbabilities = main_trafficTestDomainPrecisionRecall.getMaxTopicProbabilities(trafficQuestions);
		ArrayList<Double> moviesTopicProbabilities = main_trafficTestDomainPrecisionRecall.getMaxTopicProbabilities(moviesQuestions);

		ArrayList<Double> trafficStreetDistance = main_trafficTestDomainPrecisionRecall.getMinStreetDistance(trafficQuestions);
		ArrayList<Double> moviesStreetDistance = main_trafficTestDomainPrecisionRecall.getMinStreetDistance(moviesQuestions);

		main_trafficTestDomainPrecisionRecall.accuracy(trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0.9000000134110451, 1.4950000222772404);
		main_trafficTestDomainPrecisionRecall.recall(trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0.9000000134110451, 1.4950000222772404);
		main_trafficTestDomainPrecisionRecall.precision(trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0.9000000134110451, 1.4950000222772404);
	}

	/**
	 * Loads traffic questions from .tsv file
	 * @param tsvFile .tsv file
	 * @return List of questions
	 */
	private ArrayList<String> loadTrafficDataset(String tsvFile) {
		ArrayList<String> trafficQuestions = new ArrayList<>();
		BufferedReader TSVFile = null;
		try {
			TSVFile = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFile), "UTF8"));
			String dataRow = TSVFile.readLine();

			while (dataRow != null) {
				String[] dataArray = dataRow.split("\t");
				trafficQuestions.add(dataArray[0]);
				dataRow = TSVFile.readLine();
			}
			TSVFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trafficQuestions;
	}

	/**
	 * Loads movies questions from dataset
	 * @param moviesDataset JSON file
	 * @return List of movies questions
	 */
	private ArrayList<String> loadMoviesDataset(String moviesDataset) {
		ArrayList<String> moviesQuestions = new ArrayList<>();
		String file = "";

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(moviesDataset), "UTF8"));
			String output;
			while ((output = br.readLine()) != null) {
				file += output;
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray jsonArray = new JSONArray(file);
		for (int i = 0; i < jsonArray.length(); i++) {
			moviesQuestions.add(jsonArray.getJSONObject(i).getString("qText"));
		}
		return moviesQuestions;
	}

	private ArrayList<Double> getMaxTopicProbabilities(ArrayList<String> questions) {
		ArrayList<Double> probabilities = new ArrayList<>();
		TopicAnalyzer topicAnalyzer = new TopicAnalyzer();
		for (int i = 0; i < questions.size(); i++) {
			try {
				JSONArray jsonArray = topicAnalyzer.askDatasetSTS(questions.get(i)).getJSONArray("score");
				double[] topicProbabilities = topicAnalyzer.normalizeProbabilities(jsonArray);
				double maxProbability = -1;
				for (int j = 0; j < topicProbabilities.length; j++) {
					if (maxProbability < topicProbabilities[j]) {
						maxProbability = topicProbabilities[j];
					}
				}
				System.out.println(i + ") " + questions.get(i) + " Max topic probability = " + maxProbability);
				probabilities.add(maxProbability);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return probabilities;
	}

	private ArrayList<Double> getMinStreetDistance(ArrayList<String> questions) {
		ArrayList<Double> distances = new ArrayList<Double>();
		StreetAnalyzer streetAnalyzer = new StreetAnalyzer();
		for (int i = 0; i < questions.size(); i++) {
			StreetCandidate streetCandidate = streetAnalyzer.getStreetCandidate(questions.get(i));
			if (streetCandidate == null) {
				distances.add((double) Integer.MAX_VALUE);
				System.out.println(i + ") " + questions.get(i) + " Min street distance = infinity");
			} else {
				distances.add((double) streetCandidate.getDistance());
				System.out.println(i + ") " + questions.get(i) + " Min street distance = " + streetCandidate.getDistance());
			}
		}
		return distances;
	}

	private void accuracy(ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
						   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
						   double topicTreshold, double distanceTreshold) {
		int TTraffic = 0;
		int TMovies = 0;

		//BOTH
		for (int i = 0; i < trafficTopicProbabilities.size(); i++) {
			if (trafficTopicProbabilities.get(i) >= topicTreshold && trafficStreetDistance.get(i) <= distanceTreshold) {
				TTraffic++;
			}
		}

		for (int i = 0; i < moviesTopicProbabilities.size(); i++) {
			if (moviesTopicProbabilities.get(i) < topicTreshold || moviesStreetDistance.get(i) > distanceTreshold) {
				TMovies++;
			}
		}
		System.out.println("Topic and street accuracy: " + ((float) (TTraffic + TMovies)) / ((float) (trafficTopicProbabilities.size() + moviesTopicProbabilities.size())));

		TTraffic = 0;
		TMovies = 0;
		//TOPIC
		for (int i = 0; i < trafficTopicProbabilities.size(); i++) {
			if (trafficTopicProbabilities.get(i) >= topicTreshold) {
				TTraffic++;
			}
		}

		for (int i = 0; i < moviesTopicProbabilities.size(); i++) {
			if (moviesTopicProbabilities.get(i) < topicTreshold) {
				TMovies++;
			}
		}
		System.out.println("Topic accuracy: " + ((float) (TTraffic + TMovies)) / ((float) (trafficTopicProbabilities.size() + moviesTopicProbabilities.size())));

		TTraffic = 0;
		TMovies = 0;
		//STREET
		for (int i = 0; i < trafficStreetDistance.size(); i++) {
			if (trafficStreetDistance.get(i) <= distanceTreshold) {
				TTraffic++;
			}
		}

		for (int i = 0; i < moviesStreetDistance.size(); i++) {
			if (moviesStreetDistance.get(i) > distanceTreshold) {
				TMovies++;
			}
		}
		System.out.println("Street accuracy: " + ((float) (TTraffic + TMovies)) / ((float) (trafficStreetDistance.size() + moviesStreetDistance.size())));
	}

	private void recall(ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
						   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
						   double topicTreshold, double distanceTreshold) {
		int TTraffic = 0;

		//BOTH
		for (int i = 0; i < trafficTopicProbabilities.size(); i++) {
			if (trafficTopicProbabilities.get(i) >= topicTreshold && trafficStreetDistance.get(i) <= distanceTreshold) {
				TTraffic++;
			}
		}
		System.out.println("Topic and street recall: " + ((float) TTraffic) / ((float) trafficTopicProbabilities.size()));

		TTraffic = 0;
		//TOPIC
		for (int i = 0; i < trafficTopicProbabilities.size(); i++) {
			if (trafficTopicProbabilities.get(i) >= topicTreshold) {
				TTraffic++;

			}
		}

		System.out.println("Topic recall: " + ((float) TTraffic) / ((float) trafficTopicProbabilities.size()));

		TTraffic = 0;
		//STREET
		for (int i = 0; i < trafficStreetDistance.size(); i++) {
			if (trafficStreetDistance.get(i) <= distanceTreshold) {
				TTraffic++;
			}
		}
		System.out.println("Street recall: " + ((float) TTraffic) / ((float) trafficStreetDistance.size()));
	}

	private void precision(ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
						 ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
						 double topicTreshold, double distanceTreshold) {
		int TTraffic = 0;
		int ALLTraffic = 0;

		//BOTH
		for (int i = 0; i < trafficTopicProbabilities.size(); i++) {
			if (trafficTopicProbabilities.get(i) >= topicTreshold && trafficStreetDistance.get(i) <= distanceTreshold) {
				TTraffic++;
				ALLTraffic++;
			}
		}

		for (int i = 0; i < moviesTopicProbabilities.size(); i++) {
			if (moviesTopicProbabilities.get(i) >= topicTreshold && moviesTopicProbabilities.get(i) <= distanceTreshold) {
				ALLTraffic++;
			}
		}
		System.out.println("Topic and street precision: " + ((float) TTraffic) / ((float) ALLTraffic));

		TTraffic = 0;
		ALLTraffic = 0;
		//TOPIC
		for (int i = 0; i < trafficTopicProbabilities.size(); i++) {
			if (trafficTopicProbabilities.get(i) >= topicTreshold) {
				TTraffic++;
				ALLTraffic++;
			}
		}
		for (int i = 0; i < moviesTopicProbabilities.size(); i++) {
			if (moviesTopicProbabilities.get(i) >= topicTreshold) {
				ALLTraffic++;
			}
		}
		System.out.println("Topic precision: " + ((float) TTraffic) / ((float) ALLTraffic));

		TTraffic = 0;
		ALLTraffic = 0;
		//STREET
		for (int i = 0; i < trafficStreetDistance.size(); i++) {
			if (trafficStreetDistance.get(i) <= distanceTreshold) {
				ALLTraffic++;
				TTraffic++;
			}
		}
		for (int i = 0; i < moviesStreetDistance.size(); i++) {
			if (moviesStreetDistance.get(i) <= distanceTreshold) {
				ALLTraffic++;
			}
		}
		System.out.println("Street precision: " + ((float) TTraffic) / ((float) ALLTraffic));
	}
}