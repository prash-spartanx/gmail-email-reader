package com.example.gmail_reader.controller;



import com.example.gmail_reader.model.EmailDto;
import com.example.gmail_reader.service.GmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class GmailController {

    private final GmailService gmailService;

    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping
    public List<EmailDto> getEmails() {

        try {
            return gmailService.getLast200Emails();

        } catch (Exception exception) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to fetch emails from Gmail"
            );
        }
    }
}