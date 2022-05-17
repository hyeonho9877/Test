package com.kyonggi.eku;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class SendTool {
    private static final String TAG = "SendTool";
    private static final String baseUrl = "https://eku.kro.kr";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();


    public static void request(int bodyType, String url, HashMap<String, Object> params, Handler handler) throws IOException, NullPointerException {

        if (bodyType == APPLICATION_JSON) {
            requestForJson(url, params, handler);
            return;
        }

        FormBody.Builder builder = new FormBody.Builder();

        if (params != null) {
            for (String key : params.keySet()) {
                String value = (String) params.get(key);
                builder.add(key, value);
            }
        }

        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(baseUrl+url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: ");
                handler.sendMessage(Message.obtain(handler, CONNECTION_FAILED));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: "+response.code());
                handler.sendMessage(Message.obtain(handler, response.code(), response.body().string()));
            }
        });
    }

    public static void requestForJson(String url, HashMap<String, Object> params, Handler handler) throws NullPointerException {

        String jsonObject = gson.toJson(params);

        MediaType JSON = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(jsonObject, JSON);

        Log.d(TAG, "requestForJson: " + jsonObject);
        Request request = new Request.Builder()
                .url(baseUrl + url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: ");
                handler.sendMessage(Message.obtain(handler, CONNECTION_FAILED));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.code());
                handler.sendMessage(Message.obtain(handler, response.code(), response.body().string()));
            }
        });
    }

    public static void requestForJson2(String url, Object obj, Handler handler) throws NullPointerException {

        String jsonObject = gson.toJson(obj);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonObject, JSON);

        Request request = new Request.Builder()
                .url(baseUrl+url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: ");
                handler.sendMessage(Message.obtain(handler, CONNECTION_FAILED));

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: "+response.code());
                handler.sendMessage(Message.obtain(handler, response.code(), response.body().string()));
            }
        });
    }

    public static <T> T parseToSingleEntity(String targetText, Class<T> t) {
        return gson.fromJson(targetText, t);
    }

    public static <T> List<T> parseToList(String targetText, Class<T[]> t) {
        return Arrays.asList(gson.fromJson(targetText, t));
    }

    public static final int CONNECTION_FAILED = -1;
    public static final int HTTP_OK = 200;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int EMPTY_BODY = 0x0;
    public static final int APPLICATION_JSON = 0x1;
    public static final int POST_PARAM = 0x2;
}
