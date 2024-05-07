package com.ITPMBackend.ITPMBackend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class CombinedComplexityCalculator {

    @PostMapping("/calculate-complexity")
    public ResponseEntity<ComplexityResult> analyzeComplexity(@RequestBody String payload) {
        try {
            String code = extractCodeFromPayload(payload);
            if (code == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ComplexityResult("Invalid payload", -1));
            }

            // Perform the complexity analysis for all types
            int switchCount = countSwitchComplexity(code);
            int threadCount = detectConcurrentThreadsComplexity(code);
            int arrayComplexity = analyzeArrayComplexity(code);

            // Sum up the total complexity count
            int totalComplexity = switchCount + threadCount + arrayComplexity;

            return ResponseEntity.ok(new ComplexityResult("Success", totalComplexity));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ComplexityResult("Error occurred", -1));
        }
    }

    private String extractCodeFromPayload(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);
            return node.get("code").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int countSwitchComplexity(String code) {
        String casePattern = "\\bcase\\s*[^:]+:";
        Pattern pattern = Pattern.compile(casePattern);
        Matcher matcher = pattern.matcher(code);

        int conditionCount = 0;
        while (matcher.find()) {
            String match = matcher.group();
            String[] conditions = match.split("\\s*&&\\s*|\\s*\\|\\|\\s*");
            conditionCount += conditions.length;
        }
        return conditionCount;
    }

    private int detectConcurrentThreadsComplexity(String code) {
        String[] lines = code.split("\n");
        int threadDeclarationCount = 0;
        int threadStartCount = 0;

        Pattern threadDeclarationPattern = Pattern.compile("\\bThread\\s+(\\w+)\\s*=\\s*new\\s+Thread\\(.*\\)");
        Pattern threadStartPattern = Pattern.compile("\\b(\\w+)\\s*\\.start\\(\\)");

        for (String line : lines) {
            Matcher declarationMatcher = threadDeclarationPattern.matcher(line);
            if (declarationMatcher.find()) {
                threadDeclarationCount++;
            }

            Matcher startMatcher = threadStartPattern.matcher(line);
            if (startMatcher.find()) {
                threadStartCount++;
            }
        }

        return threadDeclarationCount + threadStartCount;
    }

    private int analyzeArrayComplexity(String code) {
        int dimensions = countArrayDimensions(code);
        int totalElements = 0;

        int startIndex = code.indexOf("{");
        int endIndex = code.lastIndexOf("}");

        if (startIndex != -1 && endIndex != -1) {
            String arrayContent = code.substring(startIndex + 1, endIndex);
            totalElements = arrayContent.isEmpty() ? 0 : arrayContent.split(",").length;
        }

        return dimensions * totalElements;
    }

    private int countArrayDimensions(String code) {
        Pattern pattern = Pattern.compile("\\s*(\\w+)\\s*(\\[\\s*\\]\\s*)+");
        Matcher matcher = pattern.matcher(code);

        int dimensions = 0;
        while (matcher.find()) {
            String match = matcher.group();
            dimensions = match.split("\\[").length - 1;
        }
        return dimensions;
    }

    public class ComplexityResult {
        private String message;
        private int complexityCount;

        public ComplexityResult(String message, int complexityCount) {
            this.message = message;
            this.complexityCount = complexityCount;
        }

        public String getMessage() {
            return message;
        }

        public int getComplexityCount() {
            return complexityCount;
        }
    }
}
