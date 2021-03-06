package eu.ailao.hub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Petr Marek on 30.12.2015.
 * Class of statics functions
 */
public final class Statics {

	/***
	 * Checks if word appears in text
	 * @param text
	 * @param word
	 * @return TRUE if word appears in text, FALSE if word doesn't appear in text
	 */
	public static boolean isContain(String text, String word){
		String pattern = "\\b"+word+"\\b";
		Pattern p=Pattern.compile(pattern);
		Matcher m=p.matcher(text);
		return m.find();
	}
}
