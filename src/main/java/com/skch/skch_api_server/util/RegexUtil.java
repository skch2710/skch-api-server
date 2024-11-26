package com.skch.skch_api_server.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author sathish.ch
 *
 *         Java regular expressions, or "regex," allow you to match a pattern of
 *         text in a string. You create a pattern using special characters and
 *         constructs, and then use it to search for matches in an input string.
 *         For example, you could use a pattern to find all email addresses in a
 *         string, or to replace all occurrences of a specific word with another
 *         word. Java provides built-in support for regular expressions through
 *         the java.util.regex package, which makes it easy to use regex in your
 *         Java applications.
 */

public class RegexUtil {
	
	/**
	 * method to check date validation.
	 * 
	 * @param dates
	 * @return Boolean
	 */
	public static Boolean checkDateInjection(List<String> dates) {
		Pattern p = Pattern.compile(
				"^(?:(?:(?:0?[13578]|1[02])(\\/|-|\\.)31)\\1|(?:(?:0?[1,3-9]|1[0-2])(\\/|-|\\.)(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:0?2(\\/|-|\\.)29\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:(?:0?[1-9])|(?:1[0-2]))(\\/|-|\\.)(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$");
		Boolean result = true;
		for (String date : dates) {
			Matcher m = p.matcher(date);
			if (date != null && !date.isEmpty() && !m.matches()) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * method to check email validation.
	 * 
	 * @param mailId
	 * @return Boolean
	 */
	public static Boolean checkEmailInjection(String mailId) {
		String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mailId);
		if (mailId != null && !mailId.isEmpty() && !m.matches()) {
			return false;
		}
		return true;
	}
	
	public static Boolean isAlphabets(String name) {
		return name != null && name.matches("^[a-zA-Z\\s]*$");
	}

	public static Boolean isAlphaNumeric(String name) {
		return name != null && name.matches("^[a-zA-Z0-9]*$");
	}
	
	public static Boolean isAlphaNumericSpace(String name) {
	    return name != null && name.matches("^[a-zA-Z0-9\\s]*$");
	}

	public static Boolean isAlphaNumericWithSpace(String name) {
		return name != null && name.matches("^[a-zA-Z0-9\\s]*$");
	}

	public static Boolean isNumeric(String number) {
		return number != null && number.matches("^[0-9]*$");
	}
	
	public static boolean isDecimal(String number) {
	    return number != null && number.matches("[-]?[0-9]+([.][0-9]+)?");
	}
	
	public static boolean isDecimalWith(String number) {
	    return number != null && number.matches("[-]?\\d{1,3}(,\\d{3})*(\\.\\d+)?");
	}


	public static Boolean isDate(String date) {
//		String datePattern = "^(0[1-9]|1[0-2])/(0[1-9]|1\\d|2\\d|3[01])/\\d{4}$"; // Format : MM/dd/yyyy
		String datePattern = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$"; // dd-MM-yyyy
		Pattern p = Pattern.compile(datePattern);
		Matcher m = p.matcher(date);
		if (date != null && !date.isEmpty() && !m.matches()) {
			return false;
		}
		return true;
	}

	public static Boolean isDecimalNumeric(String number) {
	    return number != null && number.matches("^\\d*\\.?\\d*$");
	}
	
	public static Boolean checkDate(String date) {
		String datePattern = "^(0[1-9]|1[0-2])/(0[1-9]|1\\d|2\\d|3[01])/\\d{4}$";
		Pattern p = Pattern.compile(datePattern);
		Matcher m = p.matcher(date);
		if (date != null && !date.isEmpty() && !m.matches()) {
			return false;
		}
		return true;
	}
	
	public static String getDBColumn(String input) {
		return input!=null && !input.isBlank() ? 
				input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase() : "";
	}
	
	public static String titleCase(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String result = input.replaceAll("([a-z])([A-Z])", "$1 $2");
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;
    }
	
	public static String camelCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String result = input.replaceAll(" ", "");
        result = result.substring(0, 1).toLowerCase() + result.substring(1);
        return result;
    }
	
	public static String capitalize(String input) {
		if (input != null && !input.isEmpty()) {
			Pattern pattern = Pattern.compile("(\\b\\w)(\\w*)");
			Matcher matcher = pattern.matcher(input);
			StringBuilder output = new StringBuilder();
			while (matcher.find()) {
//				output.append(matcher.group(1).toUpperCase()).append(matcher.group(2)).append(" ");
				output.append(matcher.group(1).toUpperCase())
				.append(matcher.group(2).toLowerCase())
				.append(" ");
			}
			return output.toString().trim();
		}
		return "";
	}
	
	public static String capitalizeNew(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] words = input.trim().split("\\s+");
        String output = "";

        for (String word : words) {
            if (!word.isEmpty()) {
            	output = output.concat(word.substring(0, 1).toUpperCase())
//                      .concat(word.substring(1).toLowerCase())
                      .concat(word.substring(1))
                      .concat(" ");
            }
        }
        return output.trim();
    }
	
	public static String applyBrace(String input) {
		String result = "";
		if (input != null && !input.isEmpty() && input.length() == 10) {
			Pattern pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{4})");
			Matcher matcher = pattern.matcher(input);
			while (matcher.find()) {
				result = "(" + matcher.group(1) + ") " + matcher.group(2) + "-" + matcher.group(3);
			}
		}
		return result;
	}
	
	private static final String NUMBER_PATTERN = "^-?\\d{1,3}(,\\d{3})*(\\.\\d{1,3})?$|^-?\\d+(\\.\\d{1,3})?$";

    public static boolean isValidNumber(String number) {
        return Pattern.matches(NUMBER_PATTERN, number);
    }

//    public static void main(String[] args) {
//        String[] testNumbers = {
//            "1200", "1200.00", "-1200", "-1200.00",
//            "1,200", "-1,200.00", "-1,200", "12,00",
//            "1,200.0", "1200.000", "-1,111,200.000", "-1200.0"
//        };
//
//        for (String number : testNumbers) {
//            System.out.println(number + " is valid: " + isValidNumber(number));
//        }
//    }
}
