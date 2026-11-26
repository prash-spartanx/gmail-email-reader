# Gmail Email Reader

A Spring Boot application that retrieves the latest 200 Gmail emails using the Gmail API and OAuth 2.0 authentication.

The application displays the sender and subject of each email through a REST API.

## Features

- Retrieves the latest 200 Gmail messages.
- Displays the sender and subject of each email.
- Uses the official Gmail API.
- Authenticates users through Google OAuth 2.0.
- Requests only Gmail metadata access.
- Stores OAuth tokens locally.
- Does not require a database.
- Supports testing with different Gmail accounts.

## Technology Stack

- Java
- Spring Boot
- Spring Web
- Gmail API
- Google OAuth 2.0
- Maven

## Prerequisites

Before running the application, ensure the following are installed:

- Java (use the Java version configured in the project)
- Git
- A Google account
- Internet connection

## Getting Started

### 1. Clone the repository

Open a terminal and run:

```bash
git clone https://github.com/prash-spartanx/gmail-email-reader.git
```

Navigate to the project directory:

```bash
cd gmail-email-reader
```

### 2. Create a Google Cloud project

Each user must configure their own Google Cloud project to authenticate with Gmail.

1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project.
3. Select the newly created project.

### 3. Enable Gmail API

1. Open the API Library in Google Cloud Console.
2. Search for **Gmail API**.
3. Open Gmail API.
4. Click **Enable**.

### 4. Configure OAuth consent screen

1. Open **Google Auth Platform**.
2. Navigate to the **Branding** section.
3. Configure the application name and contact information if requested.
4. Navigate to **Audience**.
5. Select **External** as the audience if using a personal Gmail account.
6. Keep the application in **Testing** mode.

### 5. Add your Gmail account as a test user

Under:

**Google Auth Platform → Audience → Test users**

1. Click **Add users**.
2. Enter the Gmail address you want to use for testing.
3. Save the configuration.

Only authorized test users can grant permission while the application remains in Testing mode.

### 6. Create OAuth credentials

1. Open **Google Auth Platform → Clients**.
2. Click **Create Client**.
3. Select **Desktop app** as the application type.
4. Enter a name, such as `Gmail Reader Desktop`.
5. Click **Create**.
6. Download the OAuth client JSON file.

The downloaded file contains the OAuth client configuration required by the application.

### 7. Add your credentials to the project

Inside the cloned project directory, create a folder named:

```text
credentials
```

Place the downloaded OAuth JSON file inside this folder and rename it to:

```text
credentials.json
```

The final directory structure should look like this:

```text
gmail-email-reader/
│
├── credentials/
│   └── credentials.json
│
├── src/
│   └── main/
│       └── java/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── .gitignore
```

**Important:** The `credentials` folder is not included in the GitHub repository. Each user must provide their own OAuth credentials.

### 8. Run the application

On Windows, open PowerShell in the project directory and execute:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

Wait until Spring Boot starts successfully.

The application will run on:

```text
http://localhost:8080
```

### 9. Authenticate with Gmail

Open the following endpoint in your browser:

```text
http://localhost:8080/api/emails
```

**During the first API request:**

1. The application will open the Google OAuth authorization page.
2. Select the Gmail account you added as a test user.
3. Review the requested permissions.
4. Grant permission to access Gmail metadata.
5. Google will redirect the authorization response to the local application.

After successful authentication, the application will retrieve the emails and return the results.

The OAuth token will be stored locally in the `tokens` directory.

Subsequent requests will reuse the stored token and normally will not require authentication again.

## API Documentation

### Get the latest 200 emails

**Endpoint:**

```http
GET /api/emails
```

**Full URL:**

```text
http://localhost:8080/api/emails
```

### Sample Response

```json
[
  {
    "sender": "example@gmail.com",
    "subject": "Interview Invitation"
  },
  {
    "sender": "recruiter@example.com",
    "subject": "Assessment Details"
  },
  {
    "sender": "notifications@example.com",
    "subject": "Account Notification"
  }
]
```

The response contains the sender and subject of each retrieved email.

## Gmail Permissions

The application uses the following Gmail OAuth scope:

```text
https://www.googleapis.com/auth/gmail.metadata
```

This permission allows the application to access email metadata without requesting access to the full email body.

The application retrieves only the following headers:

- From
- Subject

## Testing With Another Gmail Account

To test the application with a different Gmail account on the same computer:

1. Stop the running Spring Boot application.
2. Delete the locally generated `tokens` directory.
3. Add the new Gmail address as a test user in Google Cloud Console.
4. Restart the application.
5. Open `http://localhost:8080/api/emails`.
6. Select the new Gmail account during Google authentication.
7. Grant the requested permission.

The application will now retrieve emails from the newly authorized Gmail account.

## Security

The following files contain sensitive authentication information and must never be committed to GitHub:

```text
credentials/
tokens/
```

The `.gitignore` file excludes these directories.

Each evaluator must use their own OAuth credentials and Gmail account.

The application does not require or store Gmail passwords.

## Troubleshooting

### OAuth authorization error

Ensure that:

- Gmail API is enabled.
- Your Gmail account is added as a test user.
- The OAuth client type is Desktop app.
- The credentials file is located at `credentials/credentials.json`.

### Application keeps using the previous Gmail account

Delete the locally stored `tokens` directory and authenticate again.

### Credentials file not found

Ensure the downloaded OAuth JSON file is renamed to:

```text
credentials.json
```

And placed inside:

```text
gmail-email-reader/credentials/
```

### Application takes time to retrieve emails

The current implementation retrieves email metadata through individual Gmail API requests. Retrieving a large number of messages may take some time.

## Author

Prashant Naik
