package eu.ailao.hub.testing;

import eu.ailao.hub.Statics;
import eu.ailao.hub.traffic.analyze.StreetAnalyzer;
import eu.ailao.hub.traffic.analyze.TopicAnalyzer;
import eu.ailao.hub.traffic.analyze.dataclases.StreetCandidate;
import org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Petr Marek on 4/13/2016.
 * Class for finding the best tresholds for topic probabilities and street candidate distance
 * to recognize the traffic question
 * The class also shows the misclassified questions
 */
public class Main_TrafficTest_DomainThresholds {

	private static final int TOPIC_ONLY = 0;
	private static final int STREET_ONLY = 1;
	private static final int TOPIC_AND_STREET = 2;

	private static final int STREET = 3;
	private static final int TOPIC = 4;

	private final int ACCURACY = 0;
	private final int MIN_TOPIC_TRESHOLD = 1;
	private final int MAX_DISTANCE_TRESHOLD = 2;

	private final int BEST_TRESHOLD = 0;
	private final int MAX_ACCURACY = 1;

	public static void main(String[] args) {
		if (args.length < 6) {
			System.err.println("Insert test question .tsv file, Lookup Service URL, Dataset-STS URL, reference questions TSV file and mode as arguments please.");
			System.exit(-1);
		}
		String tsvFile = args[0];
		String moviesDataset = args[1];
		Statics.labelLookupURL = args[2];
		Statics.datasetSTSURL = args[3];
		Statics.referenceQuestions = args[4];
		int mode = Integer.parseInt(args[5]);

		Main_TrafficTest_DomainThresholds main_trafficTestDomainThresholds = new Main_TrafficTest_DomainThresholds();

		ArrayList<String> trafficQuestions = main_trafficTestDomainThresholds.loadTrafficDataset(tsvFile);
		ArrayList<String> moviesQuestions = main_trafficTestDomainThresholds.loadMoviesDataset(moviesDataset);

		ArrayList<Double> trafficTopicProbabilities = null;
		ArrayList<Double> moviesTopicProbabilities = null;
		ArrayList<Double> trafficStreetDistance = null;
		ArrayList<Double> moviesStreetDistance = null;

		if (mode == TOPIC_ONLY || mode == TOPIC_AND_STREET) {
			trafficTopicProbabilities = main_trafficTestDomainThresholds.getMaxTopicProbabilities(trafficQuestions);
			moviesTopicProbabilities = main_trafficTestDomainThresholds.getMaxTopicProbabilities(moviesQuestions);
		}

		if (mode == STREET_ONLY || mode == TOPIC_AND_STREET) {
			trafficStreetDistance = main_trafficTestDomainThresholds.getMinStreetDistance(trafficQuestions);
			moviesStreetDistance = main_trafficTestDomainThresholds.getMinStreetDistance(moviesQuestions);
		}

		double[] tresholds = main_trafficTestDomainThresholds.findTresholds(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance);

		switch (mode) {
			case TOPIC_ONLY:
				System.out.println("Accuracy = " + tresholds[main_trafficTestDomainThresholds.ACCURACY] + " Treshold = " + tresholds[main_trafficTestDomainThresholds.MIN_TOPIC_TRESHOLD]);
				break;
			case STREET_ONLY:
				System.out.println("Accuracy = " + tresholds[main_trafficTestDomainThresholds.ACCURACY] + " Distance = " + tresholds[main_trafficTestDomainThresholds.MAX_DISTANCE_TRESHOLD]);
				break;
			case TOPIC_AND_STREET:
				System.out.println("Accuracy = " + tresholds[main_trafficTestDomainThresholds.ACCURACY] + " Treshold = " + tresholds[main_trafficTestDomainThresholds.MIN_TOPIC_TRESHOLD] + " Distance = " + tresholds[main_trafficTestDomainThresholds.MAX_DISTANCE_TRESHOLD]);
				break;
		}

		if (mode == main_trafficTestDomainThresholds.TOPIC_ONLY || mode == main_trafficTestDomainThresholds.TOPIC_AND_STREET) {
			System.out.println("Max movie topic = " + main_trafficTestDomainThresholds.getMax(moviesTopicProbabilities) + " Min traffic topic = " + main_trafficTestDomainThresholds.getMin(trafficTopicProbabilities));
		}

		if (mode == main_trafficTestDomainThresholds.STREET_ONLY || mode == main_trafficTestDomainThresholds.TOPIC_AND_STREET) {
			System.out.println("Min movie distance = " + main_trafficTestDomainThresholds.getMin(moviesStreetDistance) + " Max traffic distance= " + main_trafficTestDomainThresholds.getMax(trafficStreetDistance));
		}
		main_trafficTestDomainThresholds.printMistakes(mode, tresholds[main_trafficTestDomainThresholds.MIN_TOPIC_TRESHOLD],
				tresholds[main_trafficTestDomainThresholds.MAX_DISTANCE_TRESHOLD],
				trafficTopicProbabilities, moviesTopicProbabilities,
				trafficStreetDistance, moviesStreetDistance,
				trafficQuestions, moviesQuestions);
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

	/**
	 * Calculate probabilities of most probable topics
	 * @param questions questions to calculate topics
	 * @return list of max probabilities for questions
	 */
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

	/**
	 * Calculate min lexical distances of streets in the questions
	 * @param questions questions to calculate distances
	 * @return list of minimal lexical distances of street
	 */
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

	/**
	 * Calculates accuracy of decisioning with given treshold
	 * @param mode mode of decisioning (street_only, topic_only, both)
	 * @param trafficTopicProbabilities list of topic probabilities for traffic questions
	 * @param moviesTopicProbabilities list of movies probabilities for movies questions
	 * @param trafficStreetDistance list of lexical distances of street for traffic questions
	 * @param moviesStreetDistance list of lexical distances of street for movies questions
	 * @param topicTreshold topic treshold
	 * @param distanceTreshold street distance treshold
	 * @return accuracy of decisioning
	 */
	private float accuracy(int mode, ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
						   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
						   double topicTreshold, double distanceTreshold) {
		int TTraffic = 0;
		int TMovies = 0;

		switch (mode) {
			case TOPIC:
			case STREET:
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
				break;
			case TOPIC_ONLY:
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
				break;
			case STREET_ONLY:
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
				break;
		}

		switch (mode) {
			case TOPIC_ONLY:
				return ((float) (TTraffic + TMovies)) / ((float) (trafficTopicProbabilities.size() + moviesTopicProbabilities.size()));
			case STREET_ONLY:
				return ((float) (TTraffic + TMovies)) / ((float) (trafficStreetDistance.size() + moviesStreetDistance.size()));
			default:
				return ((float) (TTraffic + TMovies)) / ((float) (trafficTopicProbabilities.size() + moviesTopicProbabilities.size()));
		}

	}

	/**
	 * Returns maximal value from list
	 * @param arrayList list to find max
	 * @return maximum value
	 */
	private double getMax(ArrayList<Double> arrayList) {
		double max = 0;
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i) > max) {
				max = arrayList.get(i);
			}
		}
		return max;
	}

	/**
	 * Returns minimal value from list
	 * @param arrayList list to find min
	 * @return minimal value
	 */
	private double getMin(ArrayList<Double> arrayList) {
		double min = 1;
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i) < min) {
				min = arrayList.get(i);
			}
		}
		return min;
	}

	/**
	 * Finds best tresholds for decisioning
	 * @param mode mode to finding (topic_only, street_only, both)
	 * @param trafficTopicProbabilities list of topic probabilities for traffic questions
	 * @param moviesTopicProbabilities list of movies probabilities for movies questions
	 * @param trafficStreetDistance list of lexical distances of street for traffic questions
	 * @param moviesStreetDistance list of lexical distances of street for movies questions
	 * @return array [ACCURACY, TOPIC_TRESHOLD, DISTANCE_TRESHOLD]
	 */
	private double[] findTresholds(int mode, ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
								   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance) {
		double results[] = null;
		double resultsFirst[];
		switch (mode) {
			case TOPIC_AND_STREET:
				resultsFirst = findMaximumAccuracy(TOPIC_ONLY, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0, 10000, 1);
				results = findMaximumAccuracy(STREET, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, resultsFirst[BEST_TRESHOLD], 1000, 10);
				return new double[]{results[MAX_ACCURACY], resultsFirst[BEST_TRESHOLD], results[BEST_TRESHOLD]};
			case TOPIC_ONLY:
				results = findMaximumAccuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0, 10000, 1);
				return new double[]{results[MAX_ACCURACY], results[BEST_TRESHOLD], 0};
			case STREET_ONLY:
				results = findMaximumAccuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0, 1000, 10);
				return new double[]{results[MAX_ACCURACY], 0, results[BEST_TRESHOLD]};
			default:
				return new double[]{0, 0, 0};
		}
	}

	/**
	 * Finds the best treshold
	 * @param mode mode to finding (topic_only, street_only, both)
	 * @param trafficTopicProbabilities list of topic probabilities for traffic questions
	 * @param moviesTopicProbabilities list of movies probabilities for movies questions
	 * @param trafficStreetDistance list of lexical distances of street for traffic questions
	 * @param moviesStreetDistance list of lexical distances of street for movies questions
	 * @param secondTreshold topic treshold or distance trehold
	 * @param iterations number of iterations to use
	 * @param upper_bound maximum value to find
	 * @return array [BEST_TRESHOLD, HIGHEST_ACCURACY]
	 */
	private double[] findMaximumAccuracy(int mode, ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
										 ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
										 double secondTreshold, int iterations, double upper_bound) {
		double maxBestTreshold = 0;
		double lower_bound = 0;
		double step = 0.1f;
		double max = 0;
		double maxAccuracy = 0;
		double minBestTreshold = 0;
		int k = 0;
		for (double i = lower_bound; k < iterations && i < upper_bound; i += step) {
			if (i > upper_bound) {
				i = lower_bound;
				step = step / 10;
			}
			double acc = 0;
			if (mode == TOPIC_ONLY || mode == TOPIC) {
				acc = this.accuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, i, secondTreshold);
			} else if (mode == STREET_ONLY || mode == STREET) {
				acc = this.accuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, secondTreshold, i);
			}
			if (max < acc) {
				max = acc;
				lower_bound = i;

				maxAccuracy = acc;
				maxBestTreshold = i;
				minBestTreshold = i;
			} else if (max == acc) {
				maxAccuracy = acc;
				maxBestTreshold = i;
			} else {
				upper_bound = i;
				i = lower_bound;
				step = step / 10;
			}
			k++;
		}
		double bestTreshold = (maxBestTreshold + minBestTreshold) / 2;
		return new double[]{bestTreshold, maxAccuracy};
	}

	/**
	 * Goes through all questions and prints out all badly labeled questions
	 * @param mode mode to finding (topic_only, street_only, both)
	 * @param topicTreshold treshold of topic
	 * @param distanceTreshold treshold of street lexical distance
	 * @param trafficProbabilities list of topic probabilities for traffic questions
	 * @param moviesProbabilities list of movies probabilities for movies questions
	 * @param trafficStreetDistance list of lexical distances of street for traffic questions
	 * @param moviesStreetDistance list of lexical distances of street for movies questions
	 * @param trafficQuestions list of traffic questions
	 * @param moviesQuestions list of movies questions
	 */
	private void printMistakes(int mode, double topicTreshold, double distanceTreshold,
							   ArrayList<Double> trafficProbabilities, ArrayList<Double> moviesProbabilities,
							   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
							   ArrayList<String> trafficQuestions, ArrayList<String> moviesQuestions) {
		ArrayList<Integer> mistakesTraffic = new ArrayList<>();
		ArrayList<Integer> mistakesMovies = new ArrayList<>();
		switch (mode) {
			case TOPIC_AND_STREET:
				for (int i = 0; i < trafficProbabilities.size(); i++) {
					if (trafficProbabilities.get(i) < topicTreshold || trafficStreetDistance.get(i) > distanceTreshold) {
						mistakesTraffic.add(i);
					}
				}

				for (int i = 0; i < moviesProbabilities.size(); i++) {
					if (moviesProbabilities.get(i) >= topicTreshold && moviesStreetDistance.get(i) < distanceTreshold) {
						mistakesMovies.add(i);
					}
				}
				break;
			case TOPIC_ONLY:
				for (int i = 0; i < trafficProbabilities.size(); i++) {
					if (trafficProbabilities.get(i) < topicTreshold) {
						mistakesTraffic.add(i);
					}
				}

				for (int i = 0; i < moviesProbabilities.size(); i++) {
					if (moviesProbabilities.get(i) > topicTreshold) {
						mistakesMovies.add(i);
					}
				}
				break;
			case STREET_ONLY:
				for (int i = 0; i < trafficStreetDistance.size(); i++) {
					if (trafficStreetDistance.get(i) > distanceTreshold) {
						mistakesTraffic.add(i);
					}
				}

				for (int i = 0; i < moviesStreetDistance.size(); i++) {
					if (moviesStreetDistance.get(i) <= distanceTreshold) {
						mistakesMovies.add(i);
					}
				}
				break;
		}

		System.out.println();
		System.out.println("TRAFFIC MISTAKES");
		for (int i = 0; i < mistakesTraffic.size(); i++) {
			System.out.println(trafficQuestions.get(mistakesTraffic.get(i)));
		}

		System.out.println();
		System.out.println("MOVIES MISTAKES");
		for (int i = 0; i < mistakesMovies.size(); i++) {
			System.out.println(moviesQuestions.get(mistakesMovies.get(i)));
		}
	}
}
