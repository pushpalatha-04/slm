package com.example.slmback.util;

import com.example.slmback.model.Question;
import java.util.ArrayList;
import java.util.List;

public class GeminiParser {

    public static List<Question> parseQuestions(String geminiText) {
        List<Question> questions = new ArrayList<>();

        if (geminiText == null || geminiText.trim().isEmpty()) {
            System.err.println("GeminiParser: received empty text");
            return questions;
        }

        // Split on "Question:" label (case-insensitive)
        String[] blocks = geminiText.split("(?i)\\*{0,2}\\s*Question\\s*\\d*[:.)]?\\s*\\*{0,2}");

        int id = 1;
        for (String block : blocks) {
            block = block.trim();
            if (block.isEmpty()) continue;

            String[] lines = block.split("\n");
            if (lines.length < 5) continue;

            // Collect non-empty lines
            List<String> clean = new ArrayList<>();
            for (String line : lines) {
                String l = line.trim();
                if (!l.isEmpty()) clean.add(l);
            }
            if (clean.size() < 5) continue;

            Question q = new Question();
            q.setId(id++);

            // Line 0 = question text (strip leading numbering like "1." or "1)")
            q.setQuestion(clean.get(0).replaceAll("^\\d+[.):]?\\s*", "").trim());

            // Lines 1-4 = options A B C D
            q.setOptionA(clean.get(1).replaceAll("(?i)^\\*{0,2}A[).:]\\s*\\*{0,2}", "").trim());
            q.setOptionB(clean.get(2).replaceAll("(?i)^\\*{0,2}B[).:]\\s*\\*{0,2}", "").trim());
            q.setOptionC(clean.get(3).replaceAll("(?i)^\\*{0,2}C[).:]\\s*\\*{0,2}", "").trim());
            q.setOptionD(clean.get(4).replaceAll("(?i)^\\*{0,2}D[).:]\\s*\\*{0,2}", "").trim());

            // Search remaining lines for "Answer:" line
            for (int i = 5; i < clean.size(); i++) {
                String line = clean.get(i);
                if (line.toLowerCase().startsWith("answer")) {
                    // Extract just the letter A/B/C/D
                    String ans = line.replaceAll("(?i)\\*{0,2}answer\\s*[:.)]?\\s*\\*{0,2}", "").trim();
                    if (!ans.isEmpty()) {
                        q.setCorrectAnswer(ans.substring(0, 1).toUpperCase());
                    }
                    break;
                }
            }

            // Only add if we got a valid answer
            if (q.getCorrectAnswer() != null && !q.getCorrectAnswer().isEmpty()) {
                questions.add(q);
            } else {
                System.err.println("GeminiParser: skipped question (no answer found): " + q.getQuestion());
            }
        }

        System.out.println("GeminiParser: parsed " + questions.size() + " questions");
        return questions;
    }
}