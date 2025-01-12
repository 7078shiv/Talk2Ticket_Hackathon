package com.Kapture.Talk2Ticket.service;

import com.google.cloud.texttospeech.v1.*;
import org.springframework.stereotype.Service;


@Service
public class TextToSpeechService {

    public byte[] convertTextToSpeech(String text) throws Exception {
        // Create the Text-to-Speech client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {

            // Prepare the input text
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Set voice parameters (language code and gender)
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US").setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // Set audio configuration (audio format)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16) // Correct audio encoding for Text-to-Speech
                    .build();

            // Perform the synthesis request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Return the audio content in byte array format
            return response.getAudioContent().toByteArray();
        }
    }
}
