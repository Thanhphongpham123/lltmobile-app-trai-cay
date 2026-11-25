package com.example.foodappproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AvatarUpload extends ViewModel {
    enum State { READY, UPLOADING, FAILED, SAVED }
    final MutableLiveData<State> state = new MutableLiveData<>(State.READY);

    void save(Context context, Uri uri, SharedPreferences.Editor changes) {
        if (state.getValue() == State.UPLOADING) return;
        state.setValue(State.UPLOADING);
        Context app = context.getApplicationContext();
        new Thread(() -> {
            try {
                String url = upload(app, uri);
                if (!changes.putString(profile_activity.KEY_AVATAR, url).commit()) {
                    throw new IOException("Profile write failed");
                }
                state.postValue(State.SAVED);
            } catch (Exception e) {
                state.postValue(State.FAILED);
            }
        }, "avatar-upload").start();
    }

    private static String upload(Context app, Uri uri) throws Exception {
        String cloud = app.getString(R.string.avatar_cloud_name);
        String preset = app.getString(R.string.avatar_upload_preset);
        URL endpoint = new URL("https://api.cloudinary.com/v1_1/" + cloud + "/image/upload");
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        String boundary = UUID.randomUUID().toString();
        FutureTarget<Bitmap> picture = Glide.with(app).asBitmap().load(uri).centerInside().submit(768, 768);
        try {
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            Bitmap bitmap = picture.get();
            try (OutputStream out = connection.getOutputStream()) {
                String fields = "--" + boundary + "\r\nContent-Disposition: form-data; name=\"upload_preset\"\r\n\r\n"
                        + preset + "\r\n--" + boundary
                        + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"avatar.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n";
                out.write(fields.getBytes(StandardCharsets.UTF_8));
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)) throw new IOException("Invalid image");
                out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            }
            if (connection.getResponseCode() / 100 != 2) throw new IOException("Upload rejected");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
                String url = new JSONObject(json.toString()).getString("secure_url");
                if (!"https".equals(new URL(url).getProtocol())) throw new IOException("Invalid image URL");
                return url;
            }
        } finally {
            Glide.with(app).clear(picture);
            connection.disconnect();
        }
    }
}
