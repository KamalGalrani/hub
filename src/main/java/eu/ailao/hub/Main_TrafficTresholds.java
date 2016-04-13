package eu.ailao.hub;

import eu.ailao.hub.traffic.analyze.StreetAnalyzer;
import eu.ailao.hub.traffic.analyze.TopicAnalyzer;
import eu.ailao.hub.traffic.analyze.dataclases.StreetCandidate;
import org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Petr Marek on 4/13/2016.
 * Class for finding the best tresholds for topic probabilities and street candidate distance to recognize the traffic question
 */
public class Main_TrafficTresholds {

	private static final int TOPIC_ONLY = 0;
	private static final int STREET_ONLY = 1;
	private static final int TOPIC_AND_STREET = 2;

	private final int ACCURACY = 0;
	private final int MIN_TOPIC_TRESHOLD = 1;
	private final int MAX_DISTANCE_TRESHOLD = 2;

	public static void main(String[] args) {
		if (args.length < 6) {
			System.err.println("Insert test question .tsv file, Lookup Service URL, Dataset-STS URL, reference questions TSV file and mode as arguments please.");
			System.exit(-1);
		}
		String tsvFile = args[0];
		Statics.labelLookupURL = args[1];
		Statics.datasetSTSURL = args[2];
		Statics.referenceQuestions = args[3];
		String moviesDataset = args[4];
		int mode = Integer.parseInt(args[5]);

		Main_TrafficTresholds main_trafficTresholds = new Main_TrafficTresholds();

		ArrayList<String> trafficQuestions = main_trafficTresholds.loadTrafficDataset(tsvFile);
		ArrayList<String> moviesQuestions = main_trafficTresholds.loadMoviesDataset(moviesDataset);

		ArrayList<Double> trafficTopicProbabilities = null;
		ArrayList<Double> moviesTopicProbabilities = null;
		ArrayList<Double> trafficStreetDistance = null;
		ArrayList<Double> moviesStreetDistance = null;

		if (mode == TOPIC_ONLY || mode == TOPIC_AND_STREET) {
			trafficTopicProbabilities = main_trafficTresholds.getMaxTopicProbabilities(trafficQuestions);
			moviesTopicProbabilities = main_trafficTresholds.getMaxTopicProbabilities(moviesQuestions);
		}

		if (mode == STREET_ONLY || mode == TOPIC_AND_STREET) {
			trafficStreetDistance = main_trafficTresholds.getMinStreetDistance(trafficQuestions);
			moviesStreetDistance = main_trafficTresholds.getMinStreetDistance(moviesQuestions);
		}

		double[] tresholds = main_trafficTresholds.findTresholds(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance);

		switch (mode) {
			case TOPIC_ONLY:
				System.out.println("Accuracy = " + tresholds[main_trafficTresholds.ACCURACY] + " Treshold = " + tresholds[main_trafficTresholds.MIN_TOPIC_TRESHOLD]);
				break;
			case STREET_ONLY:
				System.out.println("Accuracy = " + tresholds[main_trafficTresholds.ACCURACY] + " Distance = " + tresholds[main_trafficTresholds.MAX_DISTANCE_TRESHOLD]);
				break;
			case TOPIC_AND_STREET:
				System.out.println("Accuracy = " + tresholds[main_trafficTresholds.ACCURACY] + " Treshold = " + tresholds[main_trafficTresholds.MIN_TOPIC_TRESHOLD] + " Distance = " + tresholds[main_trafficTresholds.MAX_DISTANCE_TRESHOLD]);
				break;
		}

		if (mode == main_trafficTresholds.TOPIC_ONLY || mode == main_trafficTresholds.TOPIC_AND_STREET) {
			System.out.println("Max movie topic = " + main_trafficTresholds.getMax(moviesTopicProbabilities) + " Min traffic topic = " + main_trafficTresholds.getMin(trafficTopicProbabilities));
		}

		if (mode == main_trafficTresholds.STREET_ONLY || mode == main_trafficTresholds.TOPIC_AND_STREET) {
			System.out.println("Min movie distance = " + main_trafficTresholds.getMin(moviesStreetDistance) + " Max traffic distance= " + main_trafficTresholds.getMax(trafficStreetDistance));
		}
	}

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

	private float accuracy(int mode,ArrayList<Double> trafficProbabilities, ArrayList<Double> moviesProbabilities,
						   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance,
						   double treshold, double distance) {
		int TTraffic = 0;
		int TMovies = 0;

		switch (mode){
			case TOPIC_AND_STREET:
				for (int i = 0; i < trafficProbabilities.size(); i++) {
					if (trafficProbabilities.get(i) >= treshold && trafficStreetDistance.get(i) <= distance) {
						TTraffic++;
					}
				}

				for (int i = 0; i < moviesProbabilities.size(); i++) {
					if (moviesProbabilities.get(i) < treshold && moviesStreetDistance.get(i) > distance) {
						TMovies++;
					}
				}
				break;
			case TOPIC_ONLY:
				for (int i = 0; i < trafficProbabilities.size(); i++) {
					if (trafficProbabilities.get(i) >= treshold) {
						TTraffic++;
					}
				}

				for (int i = 0; i < moviesProbabilities.size(); i++) {
					if (moviesProbabilities.get(i) < treshold) {
						TMovies++;
					}
				}
				break;
			case STREET_ONLY:
				for (int i = 0; i < trafficProbabilities.size(); i++) {
					if (trafficStreetDistance.get(i) <= distance) {
						TTraffic++;
					}
				}

				for (int i = 0; i < moviesProbabilities.size(); i++) {
					if (moviesStreetDistance.get(i) > distance) {
						TMovies++;
					}
				}
				break;
		}

		return ((float) (TTraffic + TMovies)) / ((float) (trafficProbabilities.size() + moviesProbabilities.size()));
	}

	private double getMax(ArrayList<Double> arrayList) {
		double max = 0;
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i) > max) {
				max = arrayList.get(i);
			}
		}
		return max;
	}

	private double getMin(ArrayList<Double> arrayList) {
		double min = 1;
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i) < min) {
				min = arrayList.get(i);
			}
		}
		return min;
	}

	private double[] findTresholds(int mode, ArrayList<Double> trafficTopicProbabilities, ArrayList<Double> moviesTopicProbabilities,
								   ArrayList<Double> trafficStreetDistance, ArrayList<Double> moviesStreetDistance) {
		double maxAccuracy = 0;
		double bestTreshold = 0;
		double bestDistance = 0;
		switch (mode) {
			case TOPIC_AND_STREET:
				for (float i = 0; i < 1; i += 0.0000001f) {
					for (float j = 0; j < 10; j += 0.5) {
						double acc = this.accuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, i, j);
						if (maxAccuracy < acc) {
							maxAccuracy = acc;
							bestTreshold = i;
							bestDistance = j;
						}
					}
				}
				break;
			case TOPIC_ONLY:
				for (float i = 0; i < 1; i += 0.0000001f) {
					double acc = this.accuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, i, 0);
					if (maxAccuracy < acc) {
						maxAccuracy = acc;
						bestTreshold = i;

					}
				}
				break;
			case STREET_ONLY:
				for (float j = 0; j < 10; j += 0.5) {
					double acc = this.accuracy(mode, trafficTopicProbabilities, moviesTopicProbabilities, trafficStreetDistance, moviesStreetDistance, 0, j);
					if (maxAccuracy < acc) {
						maxAccuracy = acc;
						bestDistance = j;
					}
				}
				break;
		}

		return new double[]{maxAccuracy, bestTreshold, bestDistance};
	}
}
