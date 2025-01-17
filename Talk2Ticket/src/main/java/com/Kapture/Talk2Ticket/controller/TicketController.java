package com.Kapture.Talk2Ticket.controller;

import com.Kapture.Talk2Ticket.service.NLPService;
import com.Kapture.Talk2Ticket.service.SpeechToTextService;
import com.Kapture.Talk2Ticket.service.TextToSpeechService;
import com.Kapture.Talk2Ticket.service.TicketService;
import com.kapturecrm.ticket.objects.Ticket;
import com.kapturecrm.utilobj.StringUtilityClass;
import facebook4j.internal.org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.Kapture.Talk2Ticket.util.TicketUtil.convertToWavIfNecessary;

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
    public ResponseEntity<byte[]> createTicket(@RequestParam("audioFile") MultipartFile audioFile) {

        try {
            if (audioFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Audio file is missing.".getBytes());
            }

            // Step 2: Save file locally (optional, for processing)
//            String filePath = "/tmp/" + Objects.requireNonNull(audioFile.getOriginalFilename());
//            audioFile.transferTo(new java.io.File(filePath));

            Path tempFile = Files.createTempFile("audio", audioFile.getOriginalFilename());
            audioFile.transferTo(tempFile.toFile());
            String originalFilePath = tempFile.toString();
            System.out.println("File saved to: " + originalFilePath);

            String processedFilePath = convertToWavIfNecessary(originalFilePath);

            // Step 1: Transcribe audio
            String transcription = speechToTextService.convertSpeechToText(processedFilePath);

            // Step 2: Extract intent
            JSONObject jsonIntent = nlpService.extractIntent(transcription);

            String value = jsonIntent.optString("intent");

            // Step 3: Perform action based on intent
            String responseMessage = switch (value) {
                case "create_ticket" -> {
                    ticketService.createTicket(jsonIntent);
                    yield "Ticket created successfully.";
                }
                case "check_ticket_status" -> "Here is your ticket status: Open.";
                case "update_ticket" -> "Ticket updated successfully.";
                case "close_ticket" -> "Ticket has been closed.";
                case "reopen_ticket" -> "Ticket has been reopened.";
                case "cancel_ticket" -> "Ticket has been cancelled.";
                case "ticket_not_found" -> "Ticket not found. Please check the ticket ID.";
                case "get_ticket_details" -> "Here are the details of your ticket.";
                case "escalate_ticket" -> "Ticket has been escalated.";
                case "add_note_to_ticket" -> "Note has been added to your ticket.";
                case "check_ticket_history" -> "Here is the history of your ticket.";
                case "delete_ticket" -> "Ticket has been deleted.";
                case "search_tickets" -> "Here are the tickets matching your search criteria.";
                case "change_ticket_priority" -> "Ticket priority has been updated.";
                case "get_ticket_comments" -> "Here are the comments for your ticket.";
                case "ticket_assigned_to_me" -> "Ticket has been assigned to you.";
                case "confirm_ticket_resolution" -> "Ticket resolution has been confirmed.";
                case "ticket_not_eligible" -> "Ticket is not eligible for the requested action.";
                default -> "Sorry, I didn't understand your request. Can you rephrase?";
            };


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
