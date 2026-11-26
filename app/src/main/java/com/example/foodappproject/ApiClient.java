package com.example.foodappproject;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * REST API Client kết nối từ máy ảo Android tới Backend Server (http://10.0.2.2:3000/api).
 * Hỗ trợ đầy đủ các phương thức chuẩn RESTful: GET, POST, PUT, DELETE
 * và tự động fallback về bộ nhớ máy nếu Server chưa bật.
 */
public class ApiClient {

    private static final String TAG = "FreshCutAPI";
    // 10.0.2.2 là địa chỉ IP đặc biệt của máy ảo Android trỏ về localhost (127.0.0.1) của máy tính
    public static final String BASE_URL = "http://10.0.2.2:3000/api";

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static JSONObject requestSync(String method, String endpoint, JSONObject body) {
        Future<JSONObject> future = executor.submit(() -> executeHttp(method, endpoint, body));
        try {
            return future.get(1500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    public static void requestAsync(String method, String endpoint, JSONObject body) {
        executor.execute(() -> executeHttp(method, endpoint, body));
    }

    private static JSONObject executeHttp(String method, String endpoint, JSONObject body) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(1200);
            conn.setReadTimeout(1200);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            if (body != null && ("POST".equals(method) || "PUT".equals(method))) {
                conn.setDoOutput(true);
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(input, 0, input.length);
                }
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();
            if (is == null) return null;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            Log.d(TAG, method + " " + endpoint + " -> HTTP " + code);
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            Log.d(TAG, "Offline fallback (" + method + " " + endpoint + "): " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String encodePath(String segment) {
        try {
            return URLEncoder.encode(segment, "UTF-8");
        } catch (Exception e) {
            return segment;
        }
    }
}
