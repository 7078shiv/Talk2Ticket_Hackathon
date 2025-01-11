package com.Kapture.Talk2Ticket.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Service
public class SpeechToTextService {
    public String convertSpeechToText(String filePath) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.readFrom(new FileInputStream(filePath));

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);

            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcription.append(result.getAlternativesList().get(0).getTranscript()).append(" ");
            }
            return transcription.toString().trim();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
