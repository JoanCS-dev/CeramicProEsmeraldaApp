package com.esmeralda.ceramicpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.esmeralda.ceramicpro.model.AuthRequestVM;
import com.esmeralda.ceramicpro.model.AuthResponseVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashScreenActivity extends AppCompatActivity {
    private SharedPreferences cookies;
    private String URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/", User, Pass;
    private MediaType mediaType = MediaType.parse("application/json");
    private OkHttpClient client;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        cookies = getSharedPreferences("SHA_CST_DB", MODE_PRIVATE);
        User = cookies.getString("user", "");
        Pass = cookies.getString("pass", "");
        gson = new Gson();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                validateSession();
            }
        }, 3000);
    }

    private void validateSession(){
        if((User.equals("") || User.equals("0000000000")) && (Pass.equals("") || Pass.equals("0000000000"))){
            startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            finish();
        }else{
            LogIn();
        }
    }

    private void LogIn() {
        if(!URL.equals("")){
            AuthRequestVM req = new AuthRequestVM();
            req.acUser = User;
            req.acPassword = Pass;
            RequestBody body = RequestBody.create(gson.toJson(req), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "AppAccount/Auth")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
                            startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                            finish();
                        }
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        AuthResponseVM res = gson.fromJson(string_json, AuthResponseVM.class);
                        if(getApplicationContext() == null){
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    RegisterTokenHome(res.data.strToken, res.data.fullName, res.data.strCode, User, Pass);
                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();

                                } else{
                                    Message("Información", res.message);
                                }
                            }
                        });
                    }else{
                        Message("Error", response.message() + " - " + response.code());
                    }
                }
            });

        }else{
            Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
        }
    }

    private void RegisterTokenHome(String strToken, String fullName, String strCode, String user, String pass){
        SharedPreferences sharedPreferences = getSharedPreferences("SHA_CST_DB", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("strToken", strToken);
        editor.putString("fullName", fullName);
        editor.putString("strCode", strCode);
        editor.putString("user", user);
        editor.putString("pass", pass);
        editor.apply();
    }

    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(SplashScreenActivity.this);
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}