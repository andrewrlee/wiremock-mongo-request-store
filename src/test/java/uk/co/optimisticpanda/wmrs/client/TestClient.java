package uk.co.optimisticpanda.wmrs.client;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class TestClient {

    private final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {

        TestClient client = new TestClient();

        client.login("bob", "password-1");

        client.addBook("bob", "Catcher In The Rye", "isbn-1");

        client.login("jim", "password-2");
        client.addBook("jim", "Moby Dick", "isbn-3");
        client.addBook("jim", "Catcher In The Rye", "isbn-1");

        client.addBook("bob", "Far from the Madding Crowd", "isbn-2");
        client.logout("bob");
        client.logout("jim");

    }

    private void addBook(String username, String title, String isbn) {

        String url = "http://localhost:8080/user/" + username + "/books/" + title;

        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
            httpBuider.addQueryParameter("query1", "aaaa");
            httpBuider.addQueryParameter("query1", "bbbb");
            httpBuider.addQueryParameter("query2", "ccc");

        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("header1", "aaa")
                .addHeader("header1", "bbb")
                .header("header1", "bbb")
                .addHeader("Cookie", "PHPSESSID=298zf09hf012fh2; csrftoken=u32t4o3tb3gg43; _gat=1")
                .header("header2", "ccc")
                .put(RequestBody.create(
                        MediaType.parse("application/json"),
                        "{\"isbn\": \"" + isbn + "\"}")).build();

        call(request);
    }

    private void logout(String username) {

        Request request = new Request.Builder()
                .url("http://localhost:8080/user/" + username + "/session")
                .delete().build();

        call(request);
    }

    private void login(String username, String password) {

        Request request = new Request.Builder()
                .url("http://localhost:8080/login")
                .method("POST", RequestBody.create(
                        MediaType.parse("application/json"),
                        "{\"username\": \"" + username + "\", "
                                + "\"password\":\"" + password + "\","
                                + "\"other-things\":\"" + new Date() + "\"}"
                ))
                .build();

        call(request);
    }


    private void call(Request request) {
        try {
            Response response = client.newCall(request).execute();
            checkState(response.isSuccessful(), "response not successful, code: '%s', body: '%s'",
                    response.code(), response.body());
        } catch (IOException e) {
            throw new RuntimeException("Error making call", e);
        }
    }
}
