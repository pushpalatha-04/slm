package com.example.slmback.service;

import com.example.slmback.model.Question;
import com.example.slmback.util.GeminiParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public List<Question> generateQuestions(String course) {

        WebClient webClient = WebClient.create();

        String prompt = "Generate exactly 20 multiple choice questions about " + course + ". "
                + "Use EXACTLY this format for every question, no extra commentary:\n\n"
                + "Question: <question text>\n"
                + "A) <option>\n"
                + "B) <option>\n"
                + "C) <option>\n"
                + "D) <option>\n"
                + "Answer: <A or B or C or D>\n\n"
                + "Repeat this block 20 times.";

        // Build JSON body safely (avoid multi-line text breaking JSON)
        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapedPrompt + "\"}]}]}";

        System.out.println("GeminiService: calling Gemini for course = " + course);

        String response = webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("GeminiService: raw response length = " + (response != null ? response.length() : 0));

        // Extract the text content from Gemini's JSON response
        String text = extractText(response);
        System.out.println("GeminiService: extracted text length = " + text.length());

        return GeminiParser.parseQuestions(text);
    }

    /**
     * Extracts the generated text from Gemini API JSON response.
     * Gemini response shape:
     * { "candidates": [ { "content": { "parts": [ { "text": "..." } ] } } ] }
     */
    private String extractText(String json) {
        if (json == null || json.isEmpty()) return "";

        // Find the "text": field inside the response
        int textIdx = json.indexOf("\"text\":");
        if (textIdx == -1) {
            System.err.println("GeminiService: no 'text' field found in response");
            return "";
        }

        // Skip past "text": and opening quote
        int start = json.indexOf('"', textIdx + 7);
        if (start == -1) return "";
        start++; // move past the opening quote

        // Find the closing quote (unescaped)
        StringBuilder sb = new StringBuilder();
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case 'n': sb.append('\n'); i += 2; continue;
                    case 'r': sb.append('\r'); i += 2; continue;
                    case 't': sb.append('\t'); i += 2; continue;
                    case '"': sb.append('"'); i += 2; continue;
                    case '\\': sb.append('\\'); i += 2; continue;
                    default: sb.append(next); i += 2; continue;
                }
            }
            if (c == '"') break; // end of text value
            sb.append(c);
            i++;
        }

        return sb.toString();
    }
}