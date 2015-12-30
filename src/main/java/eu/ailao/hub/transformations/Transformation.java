package eu.ailao.hub.transformations;

import eu.ailao.hub.questions.Question;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 30.12.2015.
 */
public abstract class Transformation {

	public abstract boolean transformDetection(String questionText);

	public abstract String transform(String questionText);

	public abstract JSONObject transformBack(JSONObject answer);
}
