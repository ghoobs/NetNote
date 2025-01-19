package client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;

public class StringReplacer {
    /**
     * Replaces the given input with a specialised lambda expression
     * @param input Input string
     * @param regex Regex expression
     * @param callback How to handle the expression
     * @return Resulting string
     */
    public static String replace(String input, Pattern regex, Function<Matcher, String> callback) {
        StringBuilder resultString = new StringBuilder();
        Matcher regexMatcher = regex.matcher(input);
        while (regexMatcher.find()) {
            regexMatcher.appendReplacement(resultString, callback.apply(regexMatcher));
        }
        regexMatcher.appendTail(resultString);
        return resultString.toString();
    }
}
