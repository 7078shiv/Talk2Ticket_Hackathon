package com.Kapture.Talk2Ticket.controller;

import com.Kapture.Talk2Ticket.service.NLPService;
import com.Kapture.Talk2Ticket.service.SpeechToTextService;
import com.Kapture.Talk2Ticket.service.TextToSpeechService;
import com.Kapture.Talk2Ticket.service.TicketService;
import com.kapturecrm.ticket.objects.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private SpeechToTextService speechToTextService;

    @Autowired
    private NLPService nlpService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TextToSpeechService textToSpeechService;

    @PostMapping("/create")
    public ResponseEntity<byte[]> createTicket(@RequestParam("audioFile") MultipartFile audioFile) throws Exception {

        try {
            if (audioFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Audio file is missing.".getBytes());
            }

            // Step 2: Save file locally (optional, for processing)
            String filePath = "/tmp/" + Objects.requireNonNull(audioFile.getOriginalFilename());
            audioFile.transferTo(new java.io.File(filePath));

            // Step 1: Transcribe audio
            String transcription = speechToTextService.convertSpeechToText(filePath);

            // Step 2: Extract intent
            String intent = nlpService.extractIntent(transcription, "o");

            // Step 3: Perform action based on intent
            String responseMessage;
            switch (intent) {
                case "create_ticket":
                    ticketService.createTicket(transcription, 1);
                    responseMessage = "Ticket created successfully.";
                    break;

                case "check_ticket_status":
                    responseMessage = "Here is your ticket status: Open.";
                    break;

                default:
                    responseMessage = "Sorry, I didn't understand your request. Can you rephrase?";
                    break;
            }

            byte[] responseAudio = textToSpeechService.convertTextToSpeech(responseMessage);

            // Return audio content as the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "responseAudio.wav");

            return new ResponseEntity<>(responseAudio, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }
}
