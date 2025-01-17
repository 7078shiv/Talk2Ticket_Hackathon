package com.Kapture.Talk2Ticket.service;

import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.io.FileInputStream;

@Service
public class SpeechToTextService {
    public String convertSpeechToText(String filePath) {
        try{

        // Path to your Vosk model
        String modelPath = "/home/shivang/Documents/vosk-model-small-en-us-0.15";

        // Load the Vosk model
        Model model = new Model(modelPath);

        // Open the audio file for input (replace with your file path)
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            System.out.println("File not found: " + filePath);
            return null;
        }

        FileInputStream fileInputStream = new FileInputStream(audioFile);
            // Create recognizer
            Recognizer recognizer = new Recognizer(model, 16000.0f);

            // Read the audio file and process in chunks
            byte[] buffer = new byte[4096];
            int bytesRead;
            StringBuilder transcription = new StringBuilder();

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    transcription.append(recognizer.getResult()).append("\n");
                }
            }

            // Get final result
            transcription.append(recognizer.getResult());

            return transcription.toString().trim();  // Return the transcribed text
        } catch (Exception e) {
            System.out.println("Error reading audio file: " + e.getMessage());
        }
        return null;
    }
}
