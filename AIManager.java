import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class AIManager {

    private static final String API_KEY =
            System.getenv(
                    "OPENAI_API_KEY"
            );

    // =========================================================
    // ASK AI
    // =========================================================

    public static String askAI(
            String question) {

        try {

            if (API_KEY == null ||
                    API_KEY.isEmpty()) {

                return
                        "ERROR:\n"
                        + "OPENAI_API_KEY was not found.\n\n"
                        + "Set the environment variable "
                        + "and restart NetBeans.";
            }

            HttpClient client =
                    HttpClient.newHttpClient();

            JSONObject json =
                    new JSONObject();

            json.put(
                    "model",
                    "gpt-6-luna"
            );

            json.put(
                    "input",
                    question
            );

            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            "https://api.openai.com/v1/responses"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer "
                                            + API_KEY
                            )
                            .POST(
                                    HttpRequest
                                            .BodyPublishers
                                            .ofString(
                                                    json.toString()
                                            )
                            )
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );

            String body =
                    response.body();

            if (response.statusCode() < 200 ||
                    response.statusCode() >= 300) {

                return
                        "OpenAI API Error:\n\n"
                        + body;
            }

            JSONObject result =
                    new JSONObject(body);

            JSONArray output =
                    result.optJSONArray(
                            "output"
                    );

            if (output == null ||
                    output.length() == 0) {

                return
                        "AI returned no output.";
            }

            for (int i = 0;
                    i < output.length();
                    i++) {

                JSONObject item =
                        output.optJSONObject(i);

                if (item == null) {
                    continue;
                }

                JSONArray content =
                        item.optJSONArray(
                                "content"
                        );

                if (content == null) {
                    continue;
                }

                for (int j = 0;
                        j < content.length();
                        j++) {

                    JSONObject contentItem =
                            content.optJSONObject(j);

                    if (contentItem == null) {
                        continue;
                    }

                    String type =
                            contentItem.optString(
                                    "type",
                                    ""
                            );

                    if ("output_text"
                            .equals(type)) {

                        String text =
                                contentItem.optString(
                                        "text",
                                        ""
                                );

                        if (!text.isEmpty()) {

                            return text;
                        }
                    }
                }
            }

            return
                    "AI response received, "
                    + "but text could not be extracted.";

        } catch (Exception e) {

            return
                    "AI Connection Error:\n\n"
                    + e.getMessage();
        }
    }
}