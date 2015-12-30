package eu.ailao.hub.transformations;

import eu.ailao.hub.questions.Question;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import static eu.ailao.hub.Statics.isContain;

/**
 * Created by Petr Marek on 30.12.2015.
 */
public class AgeTransformation extends Transformation {

	@Override
	public boolean transformDetection(String questionText) {
		String howOldQuestion = "How old is";
		if (isContain(questionText, howOldQuestion)) {
			return true;
		}
		return false;
	}

	@Override
	public String transform(String questionText) {
		String transformationFirstPart = "When was";
		String transformationSecondPart = "born?";
		String stripedQuestion = questionText.replace("How old is", "");
		stripedQuestion = stripedQuestion.replace("?", " ");
		return transformationFirstPart + stripedQuestion + transformationSecondPart;
	}

	@Override
	public JSONObject transformBack(JSONObject answer) {
		JSONArray answers = answer.getJSONArray("answers");
		for (Object o : answers) {
			JSONObject singleAnswer = (JSONObject) o;
			String answerText = (String) singleAnswer.get("text");

			LocalDate date = parseToLocalDate(answerText);
			if (date == null){
				singleAnswer.put("text", answerText);
				continue;
			}

			LocalDate now = new LocalDate();
			Years age = Years.yearsBetween(date, now);
			singleAnswer.put("text", String.valueOf(age.getYears()));
		}
		answer.put("answers", answers);
		return answer;
	}

	private LocalDate parseToLocalDate(String answerText) {
		LocalDate dt=null;
		String[] patterns={"yyyy", "yyyy-MM-dd"};
		int i=0;
		while(dt == null) {
			if (i >= patterns.length){
				break;
			}
			dt = parseByPattern(answerText, patterns[i]);
			i++;
		}
		return dt;
	}

	private LocalDate parseByPattern(String answerText,String pattern){
		LocalDate dt=null;
		try {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dt = dtf.parseLocalDate(answerText);
		} catch (Exception e) {}
		return dt;
	}
}
