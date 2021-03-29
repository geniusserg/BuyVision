package com.hse.buyvision;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class Translater extends Thread{
    private final String requestURL =
            "https://translation.googleapis.com/language/translate/v2";
    private final String requestParams =
            "&target=ru&source=en&key=AIzaSyBsRLb497BBczXgvJUOEmRsSABIx5VX7AU";
    private String requestText = "";
    public MutableLiveData<String> textResult = new MutableLiveData<>();


    private String getRequestUrl(String requestText){
        requestText = requestText.replaceAll("\n", " ");
        return requestURL + "?q=[\"" + requestText + "\"]" + requestParams;
    }

    public void setTranslateString(String requestText){
        this.requestText = requestText;
    }

    public void run() {
        String result = "";
        URL requestUrl = null;
        try {
            requestUrl = new URL(getRequestUrl(requestText));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(getRequestUrl(requestText));
        HttpURLConnection requestConnection = null;
        try {
            assert requestUrl != null;
            requestConnection = (HttpURLConnection) requestUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert requestConnection != null;
            requestConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        requestConnection.setConnectTimeout(5000);
        try {
            requestConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int requestStatus = 0;
        try {
            requestStatus = requestConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (requestStatus != 200){
            throw new RuntimeException("HttpResponseCode: "+requestStatus);
        }
        Scanner requestScanner = null;
        try {
            requestScanner = new Scanner(requestUrl.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String requestOutput = " ";
        while(true){
            assert requestScanner != null;
            if (!requestScanner.hasNext()) break;
            requestOutput += requestScanner.nextLine();
        }
        requestScanner.close();
        try {
            JSONObject requestJSONObject = new JSONObject(requestOutput);
            result = requestJSONObject.getJSONObject("data").
                     getJSONArray("translations").
                     getJSONObject(0).
                     getString("translatedText");
            result = result.replace("&quot", " ").
                    replace("[", "").
                    replace("]", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textResult.postValue(result);
        System.out.println(textResult.getValue());
    }
}

