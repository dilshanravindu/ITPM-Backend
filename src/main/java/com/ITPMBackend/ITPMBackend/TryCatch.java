package com.ITPMBackend.ITPMBackend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class TryCatch {
    @PostMapping("/trycatch")
    public int calculateComplexity(@RequestBody String code) {
        String[] lines = code.split("\n");
        int totalComplexity = 0;
        boolean insideTryCatch = false; // To track if we're inside a 'try-catch' block

        for (String line : lines) {
            // Remove curly braces, brackets, and access modifiers
            String cleanedLine = line.replaceAll("[{}]", "")
                    .replaceAll("\\b(public|private|protected)\\b", "")
                    .replaceAll("\\s+", " ").trim();

            // Remove single-line and multi-line comments
            cleanedLine = cleanedLine.replaceAll("//.*|/\\*(.|[\\r\\n])*?\\*/", "");

            // Check for 'try' and 'catch' keywords
            if (cleanedLine.contains("try")) {
                if (!insideTryCatch) {
                    totalComplexity++; // Increment for 'try'
                    insideTryCatch = true; // Set the flag to true
                }
            }
            if (cleanedLine.contains("catch")) {
                if (insideTryCatch) {
                    totalComplexity++; // Increment for 'catch'
                    insideTryCatch = false; // Reset the flag
                }
            }
        }

        return totalComplexity;
    }

}
