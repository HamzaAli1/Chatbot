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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private User currentUser;

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
            String out = queryResult.getFulfillmentText();

            if (intent.equals("Username")) {
                String name = out.substring(3, out.indexOf("!"));
                currentUser = new User(name);
            }

            sessionsClient.shutdown();

            return out;

        } catch (IOException ex) {
            System.out.println(ex.toString());
            return "Something went wrong...";
        }
    }

    public String search(String input) {
        String output = "";

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://googledictionaryapi.eu-gb.mybluemix.net").newBuilder();
        urlBuilder.addQueryParameter("define", input);
        urlBuilder.addQueryParameter("lang", "en");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            String jsonString = response.body().string();
            //System.out.println(jsonString);
            
            String prevName = "", description = "";
            JsonReader reader = new JsonReader(new StringReader(jsonString));
            while (reader.hasNext() && description.isEmpty()) {
                JsonToken nextToken = reader.peek();
                //System.out.println(nextToken);

                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    reader.beginObject();
                } else if (JsonToken.BEGIN_ARRAY.equals(nextToken)) {
                    reader.beginArray();
                } else if (JsonToken.END_OBJECT.equals(nextToken)) {
                    reader.endObject();
                } else if (JsonToken.END_ARRAY.equals(nextToken)) {
                    reader.endArray();
                }
                else if (JsonToken.NAME.equals(nextToken)) {
                    String name = reader.nextName();
                    if (name.equals("definition")) {
                        description = reader.nextString();
                    }
                    else
                        prevName = name;
                    //System.out.println(name);
                } else if (JsonToken.STRING.equals(nextToken)) {
                    String value = reader.nextString();
                    //System.out.println(value);
                } else if (JsonToken.NUMBER.equals(nextToken)) {
                    long value = reader.nextLong();
                    //System.out.println(value);
                }
                //System.out.println();
            }
            output = input.substring(0,1).toUpperCase() + input.substring(1).toLowerCase() + (prevName.equals("Verb") ? " means to " : " is ") + description.toLowerCase();
            //System.out.println(output);
            return output;
        } catch (IOException ex) {
            System.out.println(ex.toString());
            return "Something went wrong...";
        }
    }

    public String getIntent() {
        return intent;
    }

    public User getUser() {
        return currentUser;
    }

    public static void main(String[] args) throws IOException {
        Chatbot test = new Chatbot();
        //System.out.println(test.respond("hello"));
        //System.out.println(test.respond("My name is Hamza."));
        //System.out.println(test.respond("yes"));
        //System.out.println(test.currentUser);
        System.out.println(test.search("hello"));
        //System.out.println("=====================================================");
        //System.out.println(test.search("pokemon"));
    }
}
