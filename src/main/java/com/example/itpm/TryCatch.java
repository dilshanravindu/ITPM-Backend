package com.example.itpm;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class TryCatch {

    @PostMapping("/trycatch")
    public int countCatches(@RequestBody String code) {
        // Remove comments from the code
        code = removeComments(code);

        // Count the occurrences of 'catch' keyword with parentheses and brackets within the method body
        int catchCount = countCatchOccurrencesWithParenthesesAndBrackets(code);

        return catchCount;
    }

    private String removeComments(String code) {
        // Remove single-line comments
        code = code.replaceAll("//.*", "");

        // Remove multi-line comments
        code = code.replaceAll("/\\*(.|[\\r\\n])*?\\*/", "");

        return code;
    }

    private int countCatchOccurrencesWithParenthesesAndBrackets(String code) {
        // Use regex to find 'catch' blocks with both parentheses and brackets
        Pattern catchPattern = Pattern.compile("\\bcatch\\s*\\([^)]*\\)\\s*\\{");
        Matcher matcher = catchPattern.matcher(code);

        int catchCount = 0;
        // Iterate through matches and count occurrences
        while (matcher.find()) {
            catchCount++;
        }

        return catchCount;
    }
}