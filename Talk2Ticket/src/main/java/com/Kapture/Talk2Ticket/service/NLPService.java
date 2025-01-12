package com.Kapture.Talk2Ticket.service;

import com.google.cloud.dialogflow.v2.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NLPService {
    public String extractIntent(String text, String sessionId) throws Exception {
        sessionId = UUID.randomUUID().toString();
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName session = SessionName.of("kaphack3", sessionId);

            TextInput.Builder textInput = TextInput.newBuilder()
                    .setText(text)
                    .setLanguageCode("en-US");

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(request);
            return response.getQueryResult().getIntent().getDisplayName();
        }
    }
}
