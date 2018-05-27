package com.android.flappydragon.webservice;
import com.android.flappydragon.User;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;

/**
 * Created by Rodrigo on 26/05/2018.
 */

public class Request {


    public Request(){

    }


    public void sendRequest(User requestObject, String method) {

        final Json json = new Json();

        String requestJson = json.toJson(requestObject); // this is just an example

        Net.HttpRequest request = new Net.HttpRequest(method);
        final String url = "https://api-android-node.herokuapp.com/user";
        request.setUrl(url);

        request.setContent(requestJson);

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                try {

                    int statusCode = httpResponse.getStatus().getStatusCode();
                    if(statusCode != 200) {
                        System.out.println("Request Failed 1");
                        return;
                    }else{
                        String responseJson = httpResponse.getResultAsString();
                    }

                }
                catch(Exception exception) {
                    exception.printStackTrace();
                }
            }

            public void failed(Throwable t) {
                System.out.println("Request Failed Completely");
            }

            @Override
            public void cancelled() {
                System.out.println("request cancelled");

            }

        });
    }
}
