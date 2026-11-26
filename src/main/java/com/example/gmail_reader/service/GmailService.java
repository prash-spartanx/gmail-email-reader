package com.example.gmail_reader.service;

import com.example.gmail_reader.config.GmailConfig;


import com.example.gmail_reader.model.EmailDto;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GmailService {

    private final GmailConfig gmailConfig;

    public GmailService(GmailConfig gmailConfig) {
        this.gmailConfig = gmailConfig;
    }

    public List<EmailDto> getLast200Emails() throws Exception {

        Gmail gmail = gmailConfig.createGmailService();

        // Get the latest 200 email IDs
        ListMessagesResponse response = gmail.users()
                .messages()
                .list("me")
                .setMaxResults(200L)
                .execute();

        List<Message> messages = response.getMessages();

        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }

        List<EmailDto> emails =
                Collections.synchronizedList(new ArrayList<>());

        int batchSize = 100;

        for (int start = 0; start < messages.size(); start += batchSize) {

            int end = Math.min(start + batchSize, messages.size());

            BatchRequest batch = gmail.batch();

            JsonBatchCallback<Message> callback =
                    new JsonBatchCallback<>() {

                        @Override
                        public void onSuccess(
                                Message message,
                                HttpHeaders responseHeaders) {

                            String sender = "";
                            String subject = "";

                            if (message.getPayload() != null
                                    && message.getPayload().getHeaders() != null) {

                                for (MessagePartHeader header :
                                        message.getPayload().getHeaders()) {

                                    if ("From".equalsIgnoreCase(header.getName())) {
                                        sender = header.getValue();
                                    }

                                    if ("Subject".equalsIgnoreCase(header.getName())) {
                                        subject = header.getValue();
                                    }
                                }
                            }

                            emails.add(new EmailDto(sender, subject));
                        }

                        @Override
                        public void onFailure(
                                GoogleJsonError error,
                                HttpHeaders responseHeaders) {

                            System.err.println(
                                    "Email request failed: " + error.getMessage());
                        }
                    };

            for (int i = start; i < end; i++) {

                String messageId = messages.get(i).getId();

                gmail.users()
                        .messages()
                        .get("me", messageId)
                        .setFormat("METADATA")
                        .setMetadataHeaders(List.of("From", "Subject"))
                        .queue(batch, callback);
            }

            batch.execute();
        }

        return emails;
    }
}