/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author Hamza Ali
 */
public class Chatbot {

    private final String credentials_path = "azmah-5e3b2-5bbc11e9b281.json";
    private final GoogleCredentials credentials;
    private final String sessionId = UUID.randomUUID().toString();
    private final String projectId;
    private String intent;

    public Chatbot() throws IOException {
        credentials = GoogleCredentials.fromStream(new FileInputStream(credentials_path));
        projectId = ((ServiceAccountCredentials) credentials).getProjectId();
    }

    public String respond(String input) throws IOException {
        String languageCode = "en_US"; //TODO maybe dont need this

        try {
            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
            
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);

            // Set the text and language code (en-US) for the query
            Builder textInput = TextInput.newBuilder().setText(input).setLanguageCode(languageCode); //TODO same as above

            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            // Display the query result
            QueryResult queryResult = response.getQueryResult();

            /* DEBUG STUFF
            System.out.println("Session Path: " + session.toString());
            System.out.println("====================");
            System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
            System.out.format("Detected Intent: %s (confidence: %f)\n", queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
            System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText()); */
            
            intent = queryResult.getIntent().getDisplayName();
            
            return queryResult.getFulfillmentText();
            
        } catch (IOException ex) {
            return "hi im a chat bot... i dont really work right now :D\n\n" + ex.toString();
        }
    }
    
    public String getIntent() {
        return intent;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new Chatbot().respond("hello"));
    }
}
