package cz.Communication;

import cz.Concepts.Concept;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import spark.Request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;

/**
 * Created by Petr Marek on 01.12.2015.
 * This class handles sending POST and GET requests to yodaQA and retrieves responses
 */
public class CommunicationHandler {

	/***
	 * Recreates POST from web interface, sends it to yodaQA and gets response
	 * @param address Address of yodaQA
	 * @param request POST from web interface containing question
	 * @param concepts More concepts to send to yodaQA
	 * @return response of yodaQA
	 */
	public String getPOSTResponse(String address, Request request, ArrayDeque<Concept> concepts) {
		String result = "";
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(address);
			PostRecreator postRecreator = new PostRecreator();
			httpPost = postRecreator.recreatePost(httpPost, request, concepts);

			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));

			String inputLine;
			StringBuffer postResponse = new StringBuffer();

			while ((inputLine = reader.readLine()) != null) {
				postResponse.append(inputLine);
			}
			reader.close();
			httpClient.close();
			result = postResponse.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/***
	 * Recreates GET from web interface, sends it to yodaQA and retrieves response
	 * @param address Address of yodaQA with GET parameters
	 * @return response of yodaQA
	 */
	public String getGETResponse(String address) {
		String result = "";
		try {
			HttpClient client = HttpClients.createDefault();
			HttpGet outgoingRequest = new HttpGet(address);
			HttpResponse incommingResponse = client.execute(outgoingRequest);
			BufferedReader rd = new BufferedReader
					(new InputStreamReader(incommingResponse.getEntity().getContent()));

			String line;
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
