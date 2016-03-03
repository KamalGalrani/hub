package eu.ailao.hub;

import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petr Marek on 23.02.2016.
 * Class generating sentence to answer
 */
public class AnswerSentenceGenerator {


	/**
	 * Creates answer sentence to answer
	 * @param answer answer from which answer sentence will be generated
	 * @return answer sentence
	 */
	public String getAnswerSentence(JSONObject answer) {
		try {
			JSONObject a0 = answer.getJSONArray("answers").getJSONObject(0);
			List<Integer> revSIDs = Lists.reverse(jsonArrayToList(a0.getJSONArray("snippetIDs")));
			JSONObject s0 = answer.getJSONObject("snippets").getJSONObject(revSIDs.get(0).toString());
			for (Integer wsid : revSIDs) {
				JSONObject sw = answer.getJSONObject("snippets").getJSONObject(wsid.toString());
				if (sw.has("witnessLabel") && sw.get("witnessLabel") != null) {
					s0 = sw;
					break;
				}
			}
			JSONObject src0 = answer.getJSONObject("sources").getJSONObject(s0.get("sourceID").toString());

			if (s0.get("propertyLabel") != null) {
				boolean showIsBeforeProperty = s0.getString("propertyLabel").toLowerCase().endsWith(" by");

				StringBuilder sb = new StringBuilder();
				sb.append(src0.getString("title"));
				sb.append(" ");
				if (showIsBeforeProperty)
					sb.append("is ");
				sb.append(s0.getString("propertyLabel").replaceAll(".*: ", "").toLowerCase());
				sb.append(" ");
				if (s0.has("witnessLabel") && s0.get("witnessLabel") != null) {
					sb.append("(for ");
					sb.append(s0.get("witnessLabel"));
					sb.append(") ");
				}
				if (!showIsBeforeProperty)
					sb.append("is ");
				sb.append(a0.getString("text"));
				sb.append(".");
				return sb.toString();
			} else {
				// TODO
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates list form JSON array
	 * @param jsonArray to crate list
	 * @return list
	 */
	private List jsonArrayToList(JSONArray jsonArray) {
		ArrayList list = new ArrayList();
		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				list.add(jsonArray.get(i));
			}
		}
		return list;
	}
}
