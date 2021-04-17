package com.jeremydufeux.go4lunch.service;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FakeInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        String responseString = "";

        final URI uri = chain.request().url().uri();
        final String query = uri.getQuery();
        final String[] parsedQuery = query.split("=");

        if(uri.toString().contains("nearbysearch")
                && uri.toString().contains("location")){
            String path = "src/test/resources/MockFiles/nearbySearch/nearby_search_1.json";
            responseString = new String(Files.readAllBytes(Paths.get(path)));
        }
        else if(uri.toString().contains("nearbysearch")
                && uri.toString().contains("pagetoken")) {
            String path = "src/test/resources/MockFiles/nearbySearch/nearby_search_2.json";
            responseString = new String(Files.readAllBytes(Paths.get(path)));
        }
        else if(uri.toString().contains("details")){
            System.out.println(parsedQuery[6]);
            String path = "src/test/resources/MockFiles/placeDetails/" + parsedQuery[6] + ".json";
            responseString = new String(Files.readAllBytes(Paths.get(path)));
        }

        response = new Response.Builder()
                .code(200)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(responseString.getBytes(), MediaType.parse("application/json")))
                .addHeader("content-type", "application/json")
                .build();
        return response;
    }
}
