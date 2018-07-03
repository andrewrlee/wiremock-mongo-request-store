package uk.co.optimisticpanda.wmrs.client;

import okhttp3.*;

import java.io.IOException;
import java.util.Date;

public class TestClient {

     private final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws IOException {

        TestClient client = new TestClient();

        client.login("bob", "password-1");

        client.addBook("bob", "Catcher In The Rye", "isbn-1");
        client.addBook("bob", "Far from the Madding Crowd", "isbn-2");

        client.logout("bob");
    }

    private String addBook(String username, String title, String isbn) throws IOException {

        Request request = new Request.Builder()
                .url("http://localhost:8080/user/" +username +"/books/" + title)
                .put(RequestBody.create(
                        MediaType.parse("application/json"),
                        "{\"isbn\": \"" + isbn + "\"}")).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String logout(String username) throws IOException {

        Request request = new Request.Builder()
                .url("http://localhost:8080/user/" + username + "/session")
                .delete().build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String login(String username, String password) throws IOException {


            Request request = new Request.Builder()
                    .url("http://localhost:8080/login")
                    .method("POST", RequestBody.create(
                            MediaType.parse("application/json"),
                            "{" +
                                    "\"username\": \""+ username +"\", " +
                                    "\"password\":\"" + password+ "\"," +
                                    "\"other-things\":\"" + new Date() + "\"}"
                    )).build();

            Response response = client.newCall(request).execute();
            return response.body().string();
    }
}
