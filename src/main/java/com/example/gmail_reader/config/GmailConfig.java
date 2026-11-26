package com.example.gmail_reader.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class GmailConfig {

    private static final String APPLICATION_NAME = "Gmail Email Reader";

    private static final JsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private static final String CREDENTIALS_FILE =
            "credentials/credentials.json";

    private static final String TOKENS_DIRECTORY =
            "tokens";

    public Gmail createGmailService() throws Exception {

        HttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        GoogleClientSecrets clientSecrets;

        try (Reader reader = new InputStreamReader(
                new FileInputStream(CREDENTIALS_FILE),
                StandardCharsets.UTF_8)) {

            clientSecrets = GoogleClientSecrets.load(
                    JSON_FACTORY,
                    reader
            );
        }

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport,
                        JSON_FACTORY,
                        clientSecrets,
                        Collections.singletonList(
                                GmailScopes.GMAIL_METADATA
                        )
                )
                        .setDataStoreFactory(
                                new FileDataStoreFactory(
                                        new File(TOKENS_DIRECTORY)
                                )
                        )
                        .setAccessType("offline")
                        .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder()
                        .setPort(8888)
                        .build();

        Credential credential =
                new AuthorizationCodeInstalledApp(
                        flow,
                        receiver
                ).authorize("user");

        return new Gmail.Builder(
                httpTransport,
                JSON_FACTORY,
                credential
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}