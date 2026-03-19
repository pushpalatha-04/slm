package com.example.slmback.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/code")
@CrossOrigin("*")
public class CodeExecutionController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";

    // ── Run code endpoint ────────────────────────────────────
    @PostMapping("/run")
    public Map<String, Object> runCode(@RequestBody Map<String, String> request) {

        String code     = request.getOrDefault("code", "").trim();
        String language = request.getOrDefault("language", "python");
        String stdin    = request.getOrDefault("stdin", "");

        if (code.isEmpty()) {
            return Map.of("stdout", "", "stderr", "No code provided.");
        }

        String prompt = buildRunPrompt(language, code, stdin);
        String text   = callGemini(prompt);
        return parseRunOutput(text);
    }

    // ── Visualize endpoint — returns raw Gemini JSON trace ───
    @PostMapping("/visualize")
    public Map<String, Object> visualizeCode(@RequestBody Map<String, String> request) {

        String code     = request.getOrDefault("code", "").trim();
        String language = request.getOrDefault("language", "python");

        if (code.isEmpty()) {
            return Map.of("stdout", "", "stderr", "No code provided.");
        }

        String prompt = buildVizPrompt(language, code);
        String text   = callGemini(prompt);

        // Return raw text as stdout so frontend can parse the JSON trace
        Map<String, Object> result = new HashMap<>();
        result.put("stdout", text);
        result.put("stderr", null);
        return result;
    }

    // ── Shared Gemini caller ─────────────────────────────────
    private String callGemini(String prompt) {
        String escaped = prompt
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");

        String body = "{\"contents\":[{\"parts\":[{\"text\":\"" + escaped + "\"}]}]}";

        try {
            String response = WebClient.create().post()
                .uri(GEMINI_URL + "?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            return extractGeminiText(response);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // ── Run prompt ───────────────────────────────────────────
    private String buildRunPrompt(String language, String code, String stdin) {
        String lang = language.substring(0, 1).toUpperCase() + language.substring(1);
        return "You are a " + lang + " code executor. "
            + "Execute the following " + lang + " code exactly as a real compiler/interpreter would. "
            + "Reply in EXACTLY this format, nothing else:\n\n"
            + "STDOUT:\n<exact output the code would print, or empty if nothing>\n\n"
            + "STDERR:\n<exact error message if there is one, or empty if no error>\n\n"
            + "Do not add any explanation, markdown, or extra text.\n\n"
            + "Code:\n" + code
            + (stdin.isEmpty() ? "" : "\n\nInput (stdin):\n" + stdin);
    }

    // ── Visualize prompt ─────────────────────────────────────
    private String buildVizPrompt(String language, String code) {
        return "You are a code execution tracer for " + language.toUpperCase() + ". "
            + "Trace this code step by step and return ONLY a JSON array. No markdown, no explanation, just the JSON array.\n\n"
            + "Each element must be an object with these exact fields:\n"
            + "- \"line\": the exact source line being executed (string)\n"
            + "- \"lineNum\": line number (integer)\n"
            + "- \"label\": short label like \"Line 3 - Assign\" or \"Line 5 - Loop iter 2\" (string)\n"
            + "- \"what\": one-line human explanation of what happens at this step (string)\n"
            + "- \"note\": detailed explanation connecting this step to the final output (string)\n"
            + "- \"vars\": object with ALL current variable names and their current values after this step\n"
            + "- \"output\": if this step produces console output, the exact output string; else null\n"
            + "- \"type\": one of: assign, augmented, loop_start, loop_iter, loop_end, condition_true, condition_false, method_call, return, print, declare, sql_op\n\n"
            + "Rules:\n"
            + "- For loops: include one step for loop start, then one step PER ITERATION\n"
            + "- Show all variable changes including intermediate states\n"
            + "- For every print/output: include the output field with exact text\n"
            + "- Show clearly HOW the final output was reached step by step\n"
            + "- Keep vars updated at EVERY single step showing all variables in scope\n"
            + "- For SQL: after each statement add a table field: {\"name\":\"t\",\"cols\":[...],\"rows\":[[...]]}\n\n"
            + "Code:\n```" + language + "\n" + code + "\n```\n\n"
            + "Return ONLY the JSON array starting with [ and ending with ]. Nothing else.";
    }

    // ── Extract text from Gemini response ────────────────────
    private String extractGeminiText(String json) {
        if (json == null || json.isEmpty()) return "";
        int textIdx = json.indexOf("\"text\":");
        if (textIdx == -1) return "";
        int start = json.indexOf('"', textIdx + 7);
        if (start == -1) return "";
        start++;
        StringBuilder sb = new StringBuilder();
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case 'n':  sb.append('\n');  i += 2; continue;
                    case 'r':  sb.append('\r');  i += 2; continue;
                    case 't':  sb.append('\t');  i += 2; continue;
                    case '"':  sb.append('"');   i += 2; continue;
                    case '\\': sb.append('\\');  i += 2; continue;
                    default:   sb.append(next);  i += 2; continue;
                }
            }
            if (c == '"') break;
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    // ── Parse run output ─────────────────────────────────────
    private Map<String, Object> parseRunOutput(String text) {
        Map<String, Object> result = new HashMap<>();
        String stdout = "", stderr = "";
        int stdoutIdx = text.indexOf("STDOUT:");
        int stderrIdx = text.indexOf("STDERR:");
        if (stdoutIdx != -1 && stderrIdx != -1) {
            stdout = text.substring(stdoutIdx + 7, stderrIdx).trim();
            stderr = text.substring(stderrIdx + 7).trim();
        } else if (stdoutIdx != -1) {
            stdout = text.substring(stdoutIdx + 7).trim();
        } else {
            stdout = text.trim();
        }
        result.put("stdout", stdout.isEmpty() ? null : stdout + "\n");
        result.put("stderr", stderr.isEmpty() ? null : stderr);
        result.put("status", Map.of(
            "id",          stderr.isEmpty() ? 3 : 11,
            "description", stderr.isEmpty() ? "Accepted" : "Runtime Error"
        ));
        return result;
    }
}