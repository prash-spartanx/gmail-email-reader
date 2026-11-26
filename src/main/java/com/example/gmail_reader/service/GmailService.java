package com.example.gmail_reader.service;


import com.example.gmail_reader.config.GmailConfig;
import com.example.gmail_reader.model.EmailDto;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailService {

    private Gmail gmail;

    private final GmailConfig gmailConfig = new GmailConfig();

    private Gmail getGmailClient() throws Exception {

        if (gmail == null) {
            gmail = gmailConfig.createGmailService();
        }

        return gmail;
    }

    public List<EmailDto> getLast200Emails() throws Exception {

        Gmail gmailClient = getGmailClient();

        List<EmailDto> emailList = new ArrayList<>();

        ListMessagesResponse response =
                gmailClient.users()
                        .messages()
                        .list("me")
                        .setMaxResults(200L)
                        .execute();

        List<Message> messages = response.getMessages();

        if (messages == null || messages.isEmpty()) {
            return emailList;
        }

        for (Message message : messages) {

            Message fullMessage =
                    gmailClient.users()
                            .messages()
                            .get("me", message.getId())
                            .setFormat("METADATA")
                            .setMetadataHeaders(
                                    List.of("From", "Subject")
                            )
                            .execute();

            String sender = "";
            String subject = "";

            if (fullMessage.getPayload() != null
                    && fullMessage.getPayload().getHeaders() != null) {

                for (var header :
                        fullMessage.getPayload().getHeaders()) {

                    if (header.getName().equalsIgnoreCase("From")) {
                        sender = header.getValue();
                    }

                    if (header.getName().equalsIgnoreCase("Subject")) {
                        subject = header.getValue();
                    }
                }
            }

            EmailDto emailDto =
                    new EmailDto(sender, subject);

            emailList.add(emailDto);
        }

        return emailList;
    }
}