package com.Kapture.Talk2Ticket.service;

import com.google.cloud.dialogflow.v2.*;
import facebook4j.internal.org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class NLPService {

    @Value("${OPEN_AI_KEY}")
    private String openAIKey;

    public JSONObject extractIntent(String transcriptionPayload) throws Exception {
        JSONObject inputJson = new JSONObject(transcriptionPayload);
        String transcription = inputJson.optString("text");

        // Ensure the transcription is escaped properly for JSON
        String safeTranscription = transcription.replace("\"", "\\\"");

        String openAiApiUrl = "https://api.openai.com/v1/chat/completions";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openAiApiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAIKey)
                .POST(HttpRequest.BodyPublishers.ofString(
                        """
                        {
                          "model": "gpt-3.5-turbo",
                          "messages": [
                            {
                              "role": "system",
                              "content": "You are an assistant that extracts structured JSON objects from user transcriptions. Based on the transcription, map the user's intent to one of the following actions: 'create_ticket', 'check_ticket_status', 'update_ticket', 'close_ticket', 'reopen_ticket', 'cancel_ticket', 'ticket_not_found', 'get_ticket_details', 'escalate_ticket', 'add_note_to_ticket', 'check_ticket_history', 'delete_ticket', 'search_tickets', 'change_ticket_priority', 'get_ticket_comments', 'ticket_assigned_to_me', 'confirm_ticket_resolution', 'ticket_not_eligible'. If the intent is unclear, use 'default'. Return a JSON object with the fields: 'intent', 'id', 'ticket_id', 'cm_id', 'status', 'priority', 'description', 'created_at', and 'customer_mobile_no'."
                            },
                            {
                              "role": "user",
                              "content": "Transcription: \\"%s\\""
                            }
                          ]
                        }
                        """.formatted(safeTranscription)
                ))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            String responseContent = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            return new JSONObject(responseContent); // Parse the response content as JSON
        } else {
            throw new RuntimeException("Failed to extract intent and details: " + response.body());
        }
    }
}
