package advisor.controllers;

import advisor.config.Configuration;
import advisor.DataEntities.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class RequestController {




    public RequestController() {

    }


    public void getAccessCode() { //request to get access code
        String uri = Configuration.SERVER_PATH + "/authorize" + "?client_id=" +
                Configuration.CLIENT_ID + "&redirect_uri=" + Configuration.REDIRECT_URI +
                "&response_type=code";
        System.out.println("Use this link to request the access code:\n" + uri);
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.start();
            server.createContext("/",
                    new HttpHandler() {
                        public void handle(HttpExchange exchange)throws IOException {
                            String query = exchange.getRequestURI().getQuery();
                            String request;
                            if(query != null && query.contains("code")) {
                                Configuration.ACCESS_CODE = query.substring(5);
                                System.out.println("code received");
                                System.out.println(Configuration.ACCESS_CODE);
                                request = "Got the code. Return back to your program.";
                            } else {
                                request = "Authorization code not found. Try again.";
                            }
                            exchange.sendResponseHeaders(200, request.length());
                            exchange.getResponseBody().write(request.getBytes());
                            exchange.getResponseBody().close();
                        }
                    });
            System.out.println("waiting for code...");
            while (Configuration.ACCESS_CODE.length() == 0) {
                Thread.sleep(100);
            }
            server.stop(10);
        } catch (IOException | InterruptedException e ) {
            System.out.println("Error occurred while creating server");
        }

    }
    public void getAccessToken() { //sending post request to get access token
        System.out.println("making http request for access_token...");
        System.out.println("response:");
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + Configuration.ENCODED_BASE64_CLIENT_ID_SECRET)
                .uri(URI.create(Configuration.SERVER_PATH + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code&code=" + Configuration.ACCESS_CODE +
                                "&redirect_uri=" + Configuration.REDIRECT_URI))
                .build();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            Configuration.ACCESS_TOKEN = jo.get("access_token").getAsString();
            System.out.println(Configuration.ACCESS_TOKEN);
            System.out.println("---SUCCESS---");
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred while sending request");
        }
    }
    private HttpRequest createGetRequest(String path) { // general template of get request
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization","Bearer " + Configuration.ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .uri(URI.create(Configuration.RESOURCE_PATH + path))
                .GET()
                .build();
        return request;
    }
    public List<Song> getNewReleases() { //sending request for new songs and processing data
        HttpRequest request = createGetRequest("/v1/browse/new-releases");
        List<Song> songList = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject albums = jo.getAsJsonObject("albums");
            JsonArray items = albums.getAsJsonArray("items");
            List<String> artistList = new ArrayList<>();
            for (JsonElement element : items) {
                JsonArray artists = element.getAsJsonObject().getAsJsonArray("artists");
                for (JsonElement artist : artists) {
                    artistList.add(artist.getAsJsonObject().get("name").getAsString());
                }
                JsonObject externalUrls = element.getAsJsonObject().getAsJsonObject("external_urls");
                songList.add(new Song(element.getAsJsonObject().get("name").getAsString(),
                        new ArrayList<String>(artistList),
                        externalUrls.get("spotify").getAsString()));
                artistList.clear();
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred while sending request");
        }
        return songList;
    }
    public List<Playlist> getPlaylists(String path) {//sending request for playlists and processing data
        List<Playlist> playlistList = new ArrayList<>();
        HttpRequest request = createGetRequest(path);
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            try { //try/catch to check if desired playlist path exists, and we didn't get error as response
            JsonObject playlists = jo.getAsJsonObject("playlists");
            JsonArray items = playlists.getAsJsonArray("items");
            for (JsonElement element : items) {
                JsonObject externalUrls = element.getAsJsonObject().getAsJsonObject("external_urls");
                playlistList.add(new Playlist(element.getAsJsonObject().get("name").getAsString(),
                        externalUrls.get("spotify").getAsString()));
            } }
            catch(NullPointerException e) {
                System.out.println(jo.getAsJsonObject("error").get("message").getAsString());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred while sending request");
        }
        return playlistList;
    }

    public Map<String,String> getCategoriesNameIdMap() { //sending request for categories and processing data
        HttpRequest request = createGetRequest("/v1/browse/categories");
        Map<String,String> nameIdMap = new HashMap<>();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("categories");
            JsonArray items = categories.getAsJsonArray("items");
            for (JsonElement element : items) {
                nameIdMap.put(element.getAsJsonObject().get("name").getAsString(),
                        element.getAsJsonObject().get("id").getAsString());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred while sending request");
        }
        return nameIdMap;
    }

    public List<String> getCategories() { //sending request for categories and processing data
        HttpRequest request = createGetRequest("/v1/browse/categories");
        List<String> categoriesList = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("categories");
            JsonArray items = categories.getAsJsonArray("items");
            for (JsonElement element : items) {
                categoriesList.add(element.getAsJsonObject().get("name").getAsString());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred while sending request");
        }
        return categoriesList;
    }

}
