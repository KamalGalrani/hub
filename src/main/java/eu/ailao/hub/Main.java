package eu.ailao.hub;

import eu.ailao.hub.communication.WebInterface;

/**
 * Created by Petr Marek on 26.11.2015.
 */
public class Main {

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Insert port to run, YodaQA url and Lookup Service URL as arguments please.");
			System.exit(-1);
		}
		int port = Integer.parseInt(args[0]);
		String yodaQAURL = args[1];
		Statics.labelLookupURL = args[2];

		WebInterface web = new WebInterface(port, yodaQAURL);
		Thread webThread = new Thread(web);
		webThread.start();
	}
}
