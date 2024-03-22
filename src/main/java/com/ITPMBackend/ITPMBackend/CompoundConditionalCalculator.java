package com.ITPMBackend.ITPMBackend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@RestController
@CrossOrigin
public class CompoundConditionalCalculator {
    @PostMapping("/calculate-complexity-if")
    public int countIf(@RequestBody String code) {
        // Define regular expressions to match 'if' and 'if else' statements
        String ifPattern = "\\bif\\s*\\(.+?\\)\\s*\\{";
        String elseIfPattern = "\\belse\\s+if\\s*\\(.+?\\)\\s*\\{";

        // Combine the patterns and create a regex pattern
        String combinedPattern = "(" + ifPattern + "|" + elseIfPattern + ")";
        Pattern pattern = Pattern.compile(combinedPattern);

        // Use a Matcher to find matches in the code
        Matcher matcher = pattern.matcher(code);

        int conditionCount = 0;

        // Count conditions in 'if' and 'if else' statements
        while (matcher.find()) {
            String match = matcher.group(); // Get the matched statement
            int openingBraceIndex = match.indexOf("{");
            if (openingBraceIndex != -1) {
                String conditionString = match.substring(match.indexOf("(") + 1, openingBraceIndex).trim();
                // Split the conditionString by "&&" and "||" to count conditions
                String[] conditions = conditionString.split("\\s*&&\\s*|\\s*\\|\\|\\s*");
                conditionCount += conditions.length;
            }
        }

        return conditionCount;
    }

    @PostMapping("/calculate-complexity-switch")
    public int countSwitch(@RequestBody String code) {
        // Define regular expression pattern to match 'case' statements
        String casePattern = "\\bcase\\s*[^:]+:";

        // Create a regex pattern
        Pattern pattern = Pattern.compile(casePattern);

        // Use a Matcher to find matches in the code
        Matcher matcher = pattern.matcher(code);

        int conditionCount = 0;

        // Count conditions in 'case' statements
        while (matcher.find()) {
            String match = matcher.group(); // Get the matched statement
            // Split the match by "&&" and "||" to count conditions
            String[] conditions = match.split("\\s*&&\\s*|\\s*\\|\\|\\s*");
            conditionCount += conditions.length;
        }

        return conditionCount;
    }
}
