package eu.ailao.hub.transformations;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import static eu.ailao.hub.Statics.isContain;

/**
 * Created by Petr Marek on 30.12.2015.
 * Transformation transforming "How old is" question to "When he/she was born" and back.
 *
 * EXAMPLE: Question "How old is Travolta?" is transformed to "When was Travolta born?"
 * Answer "1954" is than transformed back to "61" (2015-1954).
 */
public class AgeTransformation extends Transformation {

	/***
	 * Patterns of date
	 */
	String[] patterns = {"yyyy", "yyyy-MM-dd"};

	/***
	 * Detect if question contains "How old is" string;
	 * @param questionText Text of question to test
	 * @return TRUE if it can be transformed
	 */
	@Override
	public boolean transformDetection(String questionText) {
		String howOldQuestion = "How old is";
		if (isContain(questionText, howOldQuestion)) {
			return true;
		}
		return false;
	}

	/***
	 * "How old is" question to "When he/she was born"
	 * @param questionText Text of question to transform
	 * @return Transformed text of question
	 */
	@Override
	public String transform(String questionText) {
		String transformationFirstPart = "When was";
		String transformationSecondPart = "born?";
		String stripedQuestion = questionText.replace("How old is", "");
		stripedQuestion = stripedQuestion.replace("?", " ");
		return transformationFirstPart + stripedQuestion + transformationSecondPart;
	}

	/***
	 * Transforms answer back by getting date of born and subtracting it from actual date
	 * @param answer Answer to transform back
	 * @return Transformed answer
	 */
	@Override
	public JSONObject transformBack(JSONObject answer) {
		JSONArray answers = answer.getJSONArray("answers");
		for (Object o : answers) {
			JSONObject singleAnswer = (JSONObject) o;
			String answerText = (String) singleAnswer.get("text");

			LocalDate date = parseToLocalDate(answerText);
			if (date == null) {
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

	/***
	 * Transforms answer sentence by applying back transformations in reverse order
	 * @param answerSentence Sentence of answer to transform back
	 * @return Sentence transformed back
	 */
	@Override
	public String transformBackAnswerSentence(String answerSentence) {
		answerSentence = answerSentence.replace("birth date", "age");
		answerSentence = answerSentence.replace("date of birth", "age");
		return answerSentence;
	}

	/***
	 * Tries to parse string of question to LocalDate, uses all patterns from patterns array
	 * @param answerText Text of single answer
	 * @return LocalDate or null
	 */
	private LocalDate parseToLocalDate(String answerText) {
		LocalDate dt = null;
		int i = 0;
		while (dt == null) {
			if (i >= patterns.length) {
				break;
			}
			dt = parseByPattern(answerText, patterns[i]);
			i++;
		}
		return dt;
	}

	/***
	 * Tries to parse text of answer by pattern
	 * @param answerText Text of single answer
	 * @param pattern Pattern of LocalDate
	 * @return LocalDate or null
	 */
	private LocalDate parseByPattern(String answerText, String pattern) {
		LocalDate dt = null;
		try {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dt = dtf.parseLocalDate(answerText);
		} catch (Exception e) {
		}
		return dt;
	}
}
