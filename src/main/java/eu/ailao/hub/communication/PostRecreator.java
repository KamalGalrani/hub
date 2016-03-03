package eu.ailao.hub.communication;

import eu.ailao.hub.corefresol.concepts.Concept;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Petr Marek on 01.12.2015.
 * This class transfer POST request sent by web interface to correct format
 */
public class PostRecreator {

	/***
	 * Transfers POST request form Spark to POST request for yodaQA
	 * @param httpPost new POST request, which will be send to yodaQA
	 * @param request Spark POST from web interface
	 * @param concepts More concepts to add to POST
	 * @return POST for yodaQA
	 */
	public HttpPost recreatePost(HttpPost httpPost, Request request, String question, ArrayDeque<Concept> concepts, String prewiousBestAnswer) {
		httpPost = addHeadersToPost(httpPost, request);
		httpPost = addParamsToPost(httpPost, request, question, concepts, prewiousBestAnswer);
		return httpPost;
	}

	/***
	 * Adds headers to new POST from Spark POST
	 * @param httpPost POST for yodaQA
	 * @param request Spark POST from web interface
	 * @return POST for yodaQA
	 */
	private HttpPost addHeadersToPost(HttpPost httpPost, Request request) {
		for (Iterator<String> it = request.headers().iterator(); it.hasNext(); ) {
			String header = it.next();
			if (header.equals("Content-Length")) {
				continue;
			}
			httpPost.addHeader(header, request.headers(header));
		}
		return httpPost;
	}

	/***
	 * Adds parameters to new POST form Spark POST and adds new concepts
	 * @param httpPost POST from yodaQA
	 * @param request Spark POST from web interface
	 * @param concepts New concepts to add
	 * @return POST for yodaQA
	 */
	private HttpPost addParamsToPost(HttpPost httpPost, Request request, String question, ArrayDeque<Concept> concepts, String artificialClue) {
		try {
			Map<String, String[]> queryParamsMap = request.queryMap().toMap();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String[]> entry : queryParamsMap.entrySet()) {
				if (concepts != null && entry.getKey().equals("numberOfConcepts")) {
					urlParameters.add(new BasicNameValuePair(entry.getKey(), String.valueOf(Integer.parseInt(entry.getValue()[0]) + concepts.size())));
					continue;
				}
				if (question != null && entry.getKey().equals("text")) {
					urlParameters.add(new BasicNameValuePair(entry.getKey(), question));
					continue;
				}
				urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()[0]));
			}
			if (concepts != null) {
				urlParameters = addConcepts(urlParameters, concepts);
			}
			urlParameters.add(new BasicNameValuePair("artificialClue", artificialClue));
			urlParameters = removeEmptyUrlParameters(urlParameters);
			HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
			httpPost.setEntity(postParams);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpPost;
	}

	/***
	 * Adds concepts to parameters of POST
	 * @param urlParameters List of parameters, where concepts are add
	 * @param concepts concepts to add
	 * @return List of parameters with added concepts
	 */
	private List<NameValuePair> addConcepts(List<NameValuePair> urlParameters, ArrayDeque<Concept> concepts) {
		int conceptsSize = concepts.size();
		for (int i = 0; i < conceptsSize; i++) {
			Concept concept = concepts.poll();
			urlParameters.add(new BasicNameValuePair("pageID" + String.valueOf(i + 1), String.valueOf(concept.getPageID())));
			urlParameters.add(new BasicNameValuePair("fullLabel" + String.valueOf(i + 1), String.valueOf(concept.getFullLabel())));
		}
		return urlParameters;
	}

	/**
	 * Erase empty parameters, because there can be empty parameter and nonempty parameter with same name.
	 * It could happen, that empty parameter overwrites nonempty parameter. This method prevents it.
	 * @param urlParameters url parameters to clear
	 * @return lis of cleared url parameters
	 */
	private List<NameValuePair> removeEmptyUrlParameters(List<NameValuePair> urlParameters) {
		List<NameValuePair> newUrlParameters = new ArrayList<NameValuePair>();
		for (int i = 0; i < urlParameters.size(); i++) {
			if (!urlParameters.get(i).getValue().equals("")) {
				newUrlParameters.add(urlParameters.get(i));
			}
		}
		return newUrlParameters;
	}
}
