// ---------------------------------------------------------------
// SimpleChatBot.java
//
// A tiny chatbot: you type a message, it sends the message to
// Google's Gemini AI over the internet, and prints the reply.
//
// SETUP:
//   1. Get a free API key at https://aistudio.google.com/apikey
//   2. Set it in the GEMINI_API_KEY environment variable.
//
// HOW TO RUN (needs Java 11 or newer), from the folder with this file:
//   java SimpleChatBot.java
//
// The API key is read from the GEMINI_API_KEY environment variable
// so it does not need to be stored in this source file.
// ---------------------------------------------------------------

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class SimpleChatBotV1 {

    // ---------------------------------------------------------------
    // 1. YOUR API KEY
    // Keep the key outside the source file in an environment variable.
    // ---------------------------------------------------------------
    static final String API_KEY = System.getenv("GEMINI_API_KEY");

    // ---------------------------------------------------------------
    // 2. MODEL
    // gemini-3.5-flash-lite is fast and cheap — good for free-tier
    // classroom use. Model names change over time. If you get a 404,
    // look up a current model name in Google AI Studio and update this.
    // ---------------------------------------------------------------
    static final String MODEL = "gemini-3.5-flash-lite";

    // The full URL for the generateContent endpoint.
    static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL + ":generateContent";

    // ---------------------------------------------------------------
    // 3. SYSTEM INSTRUCTION (the "personality" of the bot)
    //
    // This text is sent with EVERY request. It tells the model how
    // to behave before it sees the user's message.
    //
    // PASTE ONE OF THE EXAMPLES BELOW into the SYSTEM_INSTRUCTION
    // constant (replace the current text). Then re-run and notice
    // how the bot's answers change.
    //
    // ----- Example 1: Friendly helper -----
    // "You are a helpful, friendly chatbot named Tommy. Keep answers
    //  short and clear. If you don't know something, say so."
    //
    // ----- Example 2: Pirate -----
    // "You are a cheerful pirate captain. Always speak like a pirate
    //  (arr, matey, etc.). Keep replies under 3 sentences."
    //
    // ----- Example 3: Strict tutor -----
    // "You are a strict but fair Java tutor. Explain concepts simply.
    //  Never write full working code for the student — give hints
    //  and ask guiding questions instead."
    //
    // ----- Example 4: Sassy teenager -----
    // "You are a sarcastic but ultimately helpful teenager. Use modern
    //  slang sparingly. Still give correct information."
    //
    // ----- Example 5: One-sentence expert -----
    // "You are a concise expert. Answer every question in exactly one
    //  clear sentence. No extra commentary."
    // ---------------------------------------------------------------
    static final String SYSTEM_INSTRUCTION =
            "You are a helpful chatbot named Tommy. Keep answers short and clear.";

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

        System.out.println("Chatbot ready! Type 'quit' to exit.");
        System.out.println("(Current personality: " + SYSTEM_INSTRUCTION + ")");
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

            try {
                String reply = askGemini(client, userMessage);
                System.out.println("Bot: " + reply);
                System.out.println();
            } catch (Exception e) {
                // Show the real error so students can debug (bad key,
                // rate limit, wrong model name, network problems, etc.)
                System.out.println("Something went wrong: " + e.getMessage());
                System.out.println();
            }
        }

        System.out.println("Goodbye!");
        keyboard.close();
    }

    // ---------------------------------------------------------------
    // Sends one message to Gemini and returns the text of its reply.
    // ---------------------------------------------------------------
    static String askGemini(HttpClient client, String message) throws Exception {

        // Build the JSON body.
        // Shape:
        // {
        //   "systemInstruction": { "parts": [ { "text": "..." } ] },
        //   "contents": [ { "parts": [ { "text": "user message" } ] } ]
        // }
        // We call escape() so quotes or newlines the user typed
        // cannot break the JSON string.
        String json = "{\"systemInstruction\":{\"parts\":[{\"text\":\""
                + escape(SYSTEM_INSTRUCTION) + "\"}]},"
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

        // Status 200 = success. Anything else is an error from the API.
        // Common codes students will see:
        //   400 = bad request (often malformed JSON)
        //   401/403 = bad or missing API key
        //   404 = model name not found
        //   429 = rate limit / quota exceeded (free tier is limited)
        if (response.statusCode() != 200) {
            return "Error " + response.statusCode() + ": " + response.body();
        }

        return getReplyText(response.body());
    }

    // Makes text safe to put inside a JSON string.
    static String escape(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }

    // Pulls the first "text" value out of Gemini's JSON reply.
    // This is intentionally simple for Java 101. Real projects use
    // a JSON library (Gson, Jackson, etc.). The simple version can
    // grab the wrong field if the response contains multiple "text"
    // keys, but it is good enough for this exercise.
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

/*
 * ===================================================================
 * CLASS EXERCISE — Build Your Own Personality Bot
 * ===================================================================
 *
 * Goal: Create a new chatbot with its own name, personality, and
 *       rules by changing ONLY the SYSTEM_INSTRUCTION (and maybe
 *       the welcome message).
 *
 * Steps for students:
 *
 * 1. Copy this whole file and rename the class (and the file) to
 *    something like MyBot.java  (remember: public class name must
 *    match the file name).
 *
 * 2. Change the SYSTEM_INSTRUCTION to describe YOUR bot.
 *    Examples of things you can control:
 *      - Name
 *      - Tone (polite, funny, strict, poetic, etc.)
 *      - Length of answers
 *      - Special rules ("always end with a question", "never use the
 *        letter e", "speak only in haiku", etc.)
 *
 * 3. Update the welcome message in main() so it matches your bot.
 *
 * 4. (Optional challenge) Add a second constant for a different
 *    personality and let the user type "switch" to change between
 *    them at runtime. You will need to:
 *      - Store two system-instruction strings
 *      - Keep a variable that remembers which one is active
 *      - Rebuild or change the instruction used inside askGemini
 *
 *    Hint: you can ask an AI coding assistant for help with the
 *    extra methods, but you must be able to explain every line
 *    you add.
 *
 * 5. Test with at least three different user messages and be ready
 *    to show the class how the personality affects the replies.
 *
 * Grading focus:
 *   - Does the bot stay in character?
 *   - Is the system instruction clear and specific?
 *   - Did you keep the key secret (no real key in shared files)?
 *
 * ===================================================================
 */