// ---------------------------------------------------------------
// SimpleChatBot_PartB.java
//
// PART B CHALLENGE VERSION — Personality Switch
//
// This is a complete, working example of the optional "switch"
// feature. Students can study it, copy pieces into their own
// project, or run it as-is after pasting an API key.
//
// SETUP:
//   1. Get a free API key at https://aistudio.google.com/apikey
//   2. Set it in the GEMINI_API_KEY environment variable.
//
// HOW TO RUN (needs Java 11 or newer):
//   java SimpleChatBot_PartB.java
//
// COMMANDS WHILE RUNNING:
//   Type any message  → bot replies using the current personality
//   Type "switch"     → flip between Pirate and Tutor personalities
//   Type "quit"       → exit the program
//
// The API key is read from the GEMINI_API_KEY environment variable
// so it does not need to be stored in this source file.
// ---------------------------------------------------------------

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class SimpleChatBot_PartB {

    // ---------------------------------------------------------------
    // 1. YOUR API KEY
    // ---------------------------------------------------------------
    static final String API_KEY = System.getenv("GEMINI_API_KEY");

    // ---------------------------------------------------------------
    // 2. MODEL
    // gemini-3.5-flash-lite is fast and cheap — good for free-tier
    // classroom use. If you get a 404, check Google AI Studio for
    // a current model name and update this line.
    // ---------------------------------------------------------------
    static final String MODEL = "gemini-3.5-flash-lite";

    static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL + ":generateContent";

    // ---------------------------------------------------------------
    // 3. TWO PERSONALITIES
    // These are the two "system instructions" the bot can use.
    // Change the text to invent your own characters.
    // ---------------------------------------------------------------
    static final String PERSONALITY_A =
            "You are a cheerful pirate captain named Captain Redbeard. "
          + "Always speak like a pirate (arr, matey, aye, etc.). "
          + "Keep every reply under 3 sentences.";

    static final String PERSONALITY_B =
            "You are a strict but fair Java tutor named Professor Byte. "
          + "Explain concepts simply. Never write full working code for "
          + "the student — give hints and ask guiding questions instead.";

    // ---------------------------------------------------------------
    // main() — program starts here
    // ---------------------------------------------------------------
    public static void main(String[] args) {

        if (API_KEY == null || API_KEY.isBlank()) {
            System.out.println("Please set your GEMINI_API_KEY environment variable.");
            System.out.println("Get a free key at: https://aistudio.google.com/apikey");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        Scanner keyboard = new Scanner(System.in);

        // Start with Personality A (the pirate)
        String activeInstruction = PERSONALITY_A;

        System.out.println("=================================================");
        System.out.println("  Chatbot ready! (Part B — Personality Switch)");
        System.out.println("=================================================");
        System.out.println("Commands:");
        System.out.println("  switch  → change personality");
        System.out.println("  quit    → exit");
        System.out.println();
        System.out.println("Current personality:");
        System.out.println("  " + activeInstruction);
        System.out.println();

        while (true) {
            System.out.print("You: ");
            String userMessage = keyboard.nextLine().trim();

            if (userMessage.equalsIgnoreCase("quit")) {
                break;
            }
            if (userMessage.isEmpty()) {
                continue;   // ignore blank lines
            }

            // Handle the switch command BEFORE calling the AI
            if (userMessage.equalsIgnoreCase("switch")) {
                activeInstruction = switchPersonality(activeInstruction);
                System.out.println();
                continue;   // do not send the word "switch" to Gemini
            }

            try {
                // Pass the *current* active instruction
                String reply = askGemini(client, userMessage, activeInstruction);
                System.out.println("Bot: " + reply);
                System.out.println();
            } catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
                System.out.println();
            }
        }

        System.out.println("Goodbye!");
        keyboard.close();
    }

    // ---------------------------------------------------------------
    // NEW METHOD for Part B
    // Switches between the two personalities and returns the new one.
    // ---------------------------------------------------------------
    static String switchPersonality(String current) {
        if (current.equals(PERSONALITY_A)) {
            System.out.println(">>> Switched to Tutor mode (Professor Byte)");
            return PERSONALITY_B;
        } else {
            System.out.println(">>> Switched to Pirate mode (Captain Redbeard)");
            return PERSONALITY_A;
        }
    }

    // ---------------------------------------------------------------
    // Sends one message to Gemini and returns the text of its reply.
    // Notice the third parameter: systemInstruction.
    // This lets main() decide which personality to use on each call.
    // ---------------------------------------------------------------
    static String askGemini(HttpClient client, String message, String systemInstruction)
            throws Exception {

        // Build the JSON body using the personality that was passed in
        String json = "{\"systemInstruction\":{\"parts\":[{\"text\":\""
                + escape(systemInstruction) + "\"}]},"
                + "\"contents\":[{\"parts\":[{\"text\":\""
                + escape(message) + "\"}]}]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", API_KEY.trim())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        // Common status codes students will see:
        //   200 = success
        //   400 = bad request (often malformed JSON)
        //   401/403 = bad or missing API key
        //   404 = model name not found
        //   429 = rate limit / quota exceeded (free tier is limited)
        if (response.statusCode() != 200) {
            return "Error " + response.statusCode() + ": " + response.body();
        }

        return getReplyText(response.body());
    }

    // Makes text safe to put inside a JSON string
    static String escape(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }

    // Pulls the first "text" value out of Gemini's JSON reply.
    // Intentionally simple for Java 101. Real projects use a JSON library.
    static String getReplyText(String json) {
        int keyPosition = json.indexOf("\"text\"");
        if (keyPosition < 0) {
            return "(The AI sent back no text.)";
        }

        int start = json.indexOf("\"", json.indexOf(":", keyPosition)) + 1;
        StringBuilder reply = new StringBuilder();

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"') {
                break;                     // end of the string
            }
            if (c == '\\') {
                i++;
                char next = json.charAt(i);
                if (next == 'n') {
                    reply.append('\n');
                } else {
                    reply.append(next);    // \" or \\
                }
            } else {
                reply.append(c);
            }
        }
        return reply.toString();
    }
}
