package com.Kapture.Talk2Ticket.controller;

import com.Kapture.Talk2Ticket.service.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/text-to-speech")
public class TextToSpeechController {

    @Autowired
    private TextToSpeechService textToSpeechService;

    @GetMapping("/speak")
    public ResponseEntity<byte[]> speak(@RequestParam String text) {
        try {
            byte[] audio = textToSpeechService.convertTextToSpeech(text);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "output.wav");
            return new ResponseEntity<>(audio, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

