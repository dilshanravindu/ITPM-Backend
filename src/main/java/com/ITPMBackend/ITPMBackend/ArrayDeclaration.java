package com.ITPMBackend.ITPMBackend;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class ArrayDeclaration {

    public class ArrayAnalysisResult {
        private int dimensions;
        private int totalElements;
        private int complexity;

        public ArrayAnalysisResult(int dimensions, int totalElements) {
            this.dimensions = dimensions;
            this.totalElements = totalElements;
            this.complexity = dimensions * totalElements;
        }

        public int getDimensions() {
            return dimensions;
        }

        public int getTotalElements() {
            return totalElements;
        }
        public int getComplexity() {
            return complexity;
        }
    }

    @PostMapping("/array-declaration")
    public ResponseEntity<ArrayAnalysisResult> analyzeArrayDeclaration(@RequestBody String code) {
        try {
            int dimensions = countDimensions(code);
            int totalElements = 0;

            // Find the start and end index of the array declaration
            int startIndex = code.indexOf("{");
            int endIndex = code.lastIndexOf("}");

            if (startIndex != -1 && endIndex != -1) {
                // Extract the content inside the curly braces {}
                String arrayContent = code.substring(startIndex + 1, endIndex);
                // Count the number of commas to determine the number of elements
                totalElements = arrayContent.isEmpty() ? 0 : arrayContent.split(",").length;
            }

            ArrayAnalysisResult result = new ArrayAnalysisResult(dimensions, totalElements);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private int countDimensions(String code) {
        // Find the array declaration and count the number of opening square brackets [ to determine dimensions
        Pattern pattern = Pattern.compile("\\s*(\\w+)\\s*(\\[\\s*\\]\\s*)+");
        Matcher matcher = pattern.matcher(code);

        int dimensions = 0;

        // Find the last matching array declaration
        while (matcher.find()) {
            String match = matcher.group();
            dimensions = match.split("\\[").length - 1; // Count the opening square brackets to determine dimensions
        }

        return dimensions;
    }
}
