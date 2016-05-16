package eu.ailao.hub.communication;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Petr Marek on 5/16/2016.
 */
public class QuestionGoogleDocLogger {

	String formURL = "https://docs.google.com/forms/d/1N7LQqvPbVDHpxmoIvP6BgM8IQv5Mpv-0Z7vQ_-MRmng/formResponse?";
	String field = "entry.1300952397";
	String submit = "&submit=Submit";

	public void log(String question){
		String submitURL = null;
		try {
			submitURL = formURL + field + "=" + URLEncoder.encode(question, "UTF-8") + "&" + submit;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			sendPost(submitURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendPost(String url) throws Exception {
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	}

}
