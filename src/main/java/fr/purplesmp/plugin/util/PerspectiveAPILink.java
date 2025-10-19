package fr.purplesmp.plugin.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PerspectiveAPILink {
    public static ToxicityScores check(String s) throws InterruptedException {
        try {
            String url = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=AIzaSyDNEdIGcMf6vvCCpHjgZv0NQjbZVgvlT9k";
            URL serverUrl = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) serverUrl.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setDoOutput(true);

            String requestBody = "{\"comment\": {\"text\": \"" + s + "\"},\"requestedAttributes\": {"
                    + "\"TOXICITY\": {},"
                    + "\"SEVERE_TOXICITY\": {},"
                    + "\"INSULT\": {},"
                    + "\"PROFANITY\": {},"
                    + "\"IDENTITY_ATTACK\": {}}}";

            try (OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream())) {
                out.write(requestBody);
            }

            int responseCode = httpConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("HTTP error code: " + responseCode);
                return new ToxicityScores(0, 0, 0, 0, 0);
            }

            try (BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()))) {
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = responseBuffer.readLine()) != null) {
                    jsonResponse.append(line);
                }

                JsonObject jsonObject = JsonParser.parseString(jsonResponse.toString()).getAsJsonObject();

                int toxicity = (int) (jsonObject.getAsJsonObject("attributeScores")
                        .getAsJsonObject("TOXICITY")
                        .getAsJsonObject("summaryScore")
                        .get("value")
                        .getAsDouble() * 100);

                int severeToxicity = (int) (jsonObject.getAsJsonObject("attributeScores")
                        .getAsJsonObject("SEVERE_TOXICITY")
                        .getAsJsonObject("summaryScore")
                        .get("value")
                        .getAsDouble() * 100);

                int insult = (int) (jsonObject.getAsJsonObject("attributeScores")
                        .getAsJsonObject("INSULT")
                        .getAsJsonObject("summaryScore")
                        .get("value")
                        .getAsDouble() * 100);

                int profanity = (int) (jsonObject.getAsJsonObject("attributeScores")
                        .getAsJsonObject("PROFANITY")
                        .getAsJsonObject("summaryScore")
                        .get("value")
                        .getAsDouble() * 100);

                int identityAttack = (int) (jsonObject.getAsJsonObject("attributeScores")
                        .getAsJsonObject("IDENTITY_ATTACK")
                        .getAsJsonObject("summaryScore")
                        .get("value")
                        .getAsDouble() * 100);

                httpConnection.disconnect();

                return new ToxicityScores(toxicity, severeToxicity, insult, profanity, identityAttack);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ToxicityScores(0, 0, 0, 0, 0);
        }
    }

    public static class ToxicityScores {
        private final int toxicity;
        private final int severeToxicity;
        private final int insult;
        private final int profanity;
        private final int identityAttack;
        private int averageScore;

        public ToxicityScores(int toxicity, int severeToxicity, int insult, int profanity, int identityAttack) {
            this.toxicity = toxicity;
            this.severeToxicity = severeToxicity;
            this.insult = insult;
            this.profanity = profanity;
            this.identityAttack = identityAttack;
        }

        public int getToxicity() {
            return toxicity;
        }

        public int getSevereToxicity() {
            return severeToxicity;
        }

        public int getInsult() {
            return insult;
        }

        public int getProfanity() {
            return profanity;
        }

        public int getIdentityAttack() {
            return identityAttack;
        }

        public int getAverageScore() {return averageScore;}

        public int get(ToxicityAttributes a) {
            return switch (a) {
                case TOXICITY -> toxicity;
                case SEVERE_TOXICITY -> severeToxicity;
                case INSULT -> insult;
                case PROFANITY -> profanity;
                case IDENTITY_ATTACK -> identityAttack;
                case AVERAGE_SCORE -> averageScore;
            };
        }

        public int calculateAverageScore() {
            int sum = toxicity + severeToxicity + insult + profanity + identityAttack;
            this.averageScore = sum / 5;
            return averageScore;
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
