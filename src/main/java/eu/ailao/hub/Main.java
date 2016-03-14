package eu.ailao.hub;

import eu.ailao.hub.communication.WebInterface;
import eu.ailao.hub.hereapi.StreetTrafficInfo;
import eu.ailao.hub.hereapi.TrafficConnector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 26.11.2015.
 */
public class Main {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Insert port and yodaQA url as arguments please.");
			System.exit(-1);
		}
		int port = Integer.parseInt(args[0]);
		String yodaQAURL = args[1];
		WebInterface web = new WebInterface(port, yodaQAURL);
		Thread webThread = new Thread(web);
		webThread.start();
	}
}
