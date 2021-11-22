package com.example.restaurante.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileHelper {

    public byte[] getFileFromURL(Context ctx, String url) {

        try {

            URL pathUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) pathUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            byte[] byteBuffer = new byte[2048];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int bytesReaded;
            while((bytesReaded = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                os.write(byteBuffer, 0, bytesReaded);
            }
            return os.toByteArray();

        } catch (IOException e) {
            Toast.makeText(ctx, "Não foi possível baixar a imagem", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

}
