package com.example.slack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText message;
    private Button send;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message=findViewById(R.id.message);
        send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = message.getText().toString();
                if(text.length()>0){
                    message.setText("");
                    try {
                        send(text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Enter a Valid Message!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void send(String data) throws IOException{

        OkHttpClient client = new OkHttpClient();

        JsonObject json = new JsonObject();
        json.addProperty("text", data);
        json.addProperty("username","incoming-webhook");
        json.addProperty("icon_emoji", ":smiley:");

        Request request = new Request.Builder()
                .url("https://hooks.slack.com/services/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")
                .post(RequestBody.create(MediaType.parse("application/javascript; charset=utf-8"), json.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Your Message has been sent!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}