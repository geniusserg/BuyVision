package com.hse.buyvision;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Translater {
    private static final String requestURL =
            "https://translation.googleapis.com/language/translate/v2";
    private static final String requestParams = "&target=ru&source=en&key=";

    private static String getRequestUrl(String requestText){
        return requestURL + "?q=[" + requestText + "]" + requestParams;
    }


    public static String translate(String requestText) throws IOException, JSONException {
        String result = "";
        URL requestUrl = new URL(getRequestUrl(requestText));
        HttpURLConnection requestConnection = (HttpURLConnection) requestUrl.openConnection();
        requestConnection.setRequestMethod("POST");
        requestConnection.setConnectTimeout(5000);
        requestConnection.connect();
        int requestStatus = requestConnection.getResponseCode();
        if (requestStatus != 200){
            throw new RuntimeException("HttpResponseCode: "+requestStatus);
        }
        Scanner requestScanner = new Scanner(requestUrl.openStream());
        String requestOutput = " ";
        while(requestScanner.hasNext()){
            requestOutput += requestScanner.nextLine();
        }
        requestScanner.close();
        JSONObject requestJSONObject = new JSONObject(requestOutput);

        return result;
    }
}

