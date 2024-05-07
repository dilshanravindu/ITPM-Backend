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
        // Regular expression pattern to match switch blocks
        String switchPattern = "switch\\s*\\(([^\\)]*)\\)\\s*\\{([^}]*)\\}";
        Pattern switchPat = Pattern.compile(switchPattern, Pattern.DOTALL);
        Matcher switchMatcher = switchPat.matcher(code);

        int totalConditions = 0;

        // For each `switch` block found
        while (switchMatcher.find()) {
            // Extract the condition expression inside `switch(condition)`
            String switchCondition = switchMatcher.group(1);

            // Count logical conditions in the switch expression itself
            int conditionTerms = countTermsInCondition(switchCondition);

            // Extract the contents of the switch block
            String switchBlockContent = switchMatcher.group(2);

            // Pattern to count `case` statements
            String casePattern = "\\bcase\\b";
            Pattern casePat = Pattern.compile(casePattern);
            Matcher caseMatcher = casePat.matcher(switchBlockContent);

            // Count the number of `case` statements excluding `default`
            int caseCount = 0;
            while (caseMatcher.find()) {
                caseCount++;
            }

            // Multiply the logical terms by the case count (ignoring the default)
            totalConditions += conditionTerms * caseCount;
        }

        return totalConditions;
    }

    /**
     * Utility method to count logical terms in a condition expression,
     * considering logical operators `&&` and `||`.
     */
    private int countTermsInCondition(String condition) {
        // Count the occurrences of logical operators
        int andOperators = condition.split("&&", -1).length - 1;
        int orOperators = condition.split("\\|\\|", -1).length - 1;

        // The number of logical terms will be total operators + 1
        return andOperators + orOperators + 1;
    }
}