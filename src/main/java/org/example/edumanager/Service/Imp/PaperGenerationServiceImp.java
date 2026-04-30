package org.example.edumanager.Service.Imp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.edumanager.Dto.PaperGenerationRequest;
import org.example.edumanager.Service.IPaperGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Calls the OpenAI Chat Completions API (GPT-4o by default) to generate a
 * formal university exam paper from the staff-supplied configuration.
 *
 * Required property in application.properties:
 *   openai.api.key=sk-...
 *   openai.model=gpt-4o          (optional, defaults to gpt-4o)
 */
@Service
public class PaperGenerationServiceImp implements IPaperGenerationService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Public API ─────────────────────────────────────────────────────────────

    @Override
    public String generatePaper(PaperGenerationRequest request) {
        String prompt = buildPrompt(request);
        try {
            String requestBody = buildRequestBody(prompt);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "OpenAI API error " + response.statusCode() + ": " + response.body());
            }

            return extractContent(response.body());

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * Builds the full prompt that is sent to ChatGPT.
     */
    private String buildPrompt(PaperGenerationRequest req) {
        StringBuilder sb = new StringBuilder();

        sb.append("Generate a complete formal university exam paper with the following specifications:\n\n");
        sb.append("Subject: ").append(req.getSubject()).append("\n");
        sb.append("Course Code: ").append(nullSafe(req.getCourseCode(), "N/A")).append("\n");
        sb.append("Department: ").append(nullSafe(req.getDepartment(), "N/A")).append("\n");
        sb.append("Semester: ").append(nullSafe(req.getSemester(), "N/A")).append("\n");
        sb.append("Total Questions: ").append(req.getTotalQuestions()).append("\n");
        sb.append("Total Marks: ").append(req.getTotalMarks()).append("\n");
        sb.append("Duration: ").append(nullSafe(req.getDuration(), "3 Hours")).append("\n");
        sb.append("Difficulty: ").append(nullSafe(req.getDifficulty(), "Medium")).append("\n");

        // Optional section breakdown
        boolean hasSections = hasValue(req.getSectionAQuestions())
                || hasValue(req.getSectionBQuestions())
                || hasValue(req.getSectionCQuestions());

        if (hasSections) {
            sb.append("\nOrganize into sections:");
            if (hasValue(req.getSectionAQuestions()) && hasValue(req.getSectionAMarksEach())) {
                sb.append("\n- Section A: ").append(req.getSectionAQuestions())
                        .append(" Short-Answer questions, ").append(req.getSectionAMarksEach())
                        .append(" marks each");
            }
            if (hasValue(req.getSectionBQuestions()) && hasValue(req.getSectionBMarksEach())) {
                sb.append("\n- Section B: ").append(req.getSectionBQuestions())
                        .append(" Medium-Answer questions, ").append(req.getSectionBMarksEach())
                        .append(" marks each");
            }
            if (hasValue(req.getSectionCQuestions()) && hasValue(req.getSectionCMarksEach())) {
                sb.append("\n- Section C: ").append(req.getSectionCQuestions())
                        .append(" Long-Answer questions, ").append(req.getSectionCMarksEach())
                        .append(" marks each");
            }
        }

        sb.append("\n\nSyllabus / Topics:\n").append(req.getSyllabus());

        sb.append("\n\nRequirements:");
        sb.append("\n1. Start with a formal header (University Exam, subject, code, date placeholder, time, max marks)");
        sb.append("\n2. Include General Instructions (attempt all/any, marks in brackets, etc.)");
        sb.append("\n3. Generate exactly ").append(req.getTotalQuestions())
                .append(" questions covering the given syllabus");
        sb.append("\n4. Each question must clearly show marks in brackets, e.g. [5 marks]");
        sb.append("\n5. Ensure total marks add up to ").append(req.getTotalMarks());
        sb.append("\n6. Difficulty should be ").append(nullSafe(req.getDifficulty(), "Medium"));
        sb.append("\n7. Questions should be varied: definitions, explain, compare, solve, design, etc.");
        sb.append("\n8. Format it professionally like a real university exam paper");

        return sb.toString();
    }

    /**
     * Builds the JSON request body for the OpenAI Chat Completions API.
     */
    private String buildRequestBody(String userPrompt) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("max_tokens", 3000);

        // System message
        ObjectNode systemMsg = objectMapper.createObjectNode();
        systemMsg.put("role", "system");
        systemMsg.put("content",
                "You are an expert academic exam paper creator. "
                        + "Generate clear, well-structured, and professional university exam papers. "
                        + "Always follow the exact specifications provided by the user.");

        // User message
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);

        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(systemMsg);
        messages.add(userMsg);
        root.set("messages", messages);

        return objectMapper.writeValueAsString(root);
    }

    /**
     * Parses the OpenAI response JSON and returns the assistant message text.
     */
    private String extractContent(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }

    private String nullSafe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private boolean hasValue(Integer value) {
        return value != null && value > 0;
    }
}
