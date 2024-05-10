package com.example.itpm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class ArrayDeclaration {

    public static class ArrayAnalysisResult {
        private int dimensions;
        private int totalElements;
        private int complexity;

        public ArrayAnalysisResult(int dimensions, int totalElements, int complexity) {
            this.dimensions = dimensions;
            this.totalElements = totalElements;
            this.complexity = complexity;
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
            int complexity;

            // Find the start and end index of the array declaration
            int startIndex = code.indexOf("{");
            int endIndex = code.lastIndexOf("}");

            if (startIndex != -1 && endIndex != -1) {
                // Extract the content inside the curly braces {}
                String arrayContent = code.substring(startIndex + 1, endIndex);
                // Count the total number of elements within the array
                totalElements = countTotalElements(arrayContent);
            }

            complexity = dimensions * totalElements;

            ArrayAnalysisResult result = new ArrayAnalysisResult(dimensions, totalElements, complexity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private int countTotalElements(String arrayContent) {
        // Remove the outer curly braces
        arrayContent = arrayContent.substring(arrayContent.indexOf("{{") + 2, arrayContent.lastIndexOf("}}"));

        // Split the content by commas and curly braces to extract individual elements
        String[] elements = arrayContent.split("\\s*[,\\{\\}]\\s*");

        // Initialize variable to count elements
        int totalElements = 0;

        // Count non-empty elements
        for (String element : elements) {
            if (!element.isEmpty()) {
                totalElements++;
            }
        }

        return totalElements;
    }




    private int countDimensions(String code) {
        // Find the array declaration and count the number of pairs of square brackets [] to determine dimensions
        Pattern pattern = Pattern.compile("\\w+(\\s*\\[\\s*\\]\\s*)+");
        Matcher matcher = pattern.matcher(code);

        int maxDimensions = 0;

        // Find all matching array declarations
        while (matcher.find()) {
            String match = matcher.group();
            int bracketsCount = match.split("\\[").length - 1; // Count the opening square brackets to determine dimensions
            maxDimensions = Math.max(maxDimensions, bracketsCount);
        }

        return maxDimensions;
    }
}
