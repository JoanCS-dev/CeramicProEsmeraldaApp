package com.esmeralda.ceramicpro;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esmeralda.ceramicpro.model.AuthRequestVM;
import com.esmeralda.ceramicpro.model.AuthResponseVM;
import com.esmeralda.ceramicpro.model.LastAppointmentResponseVM;
import com.esmeralda.ceramicpro.model.LastAppointmentVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private MediaType mediaType = MediaType.parse("application/json");
    private OkHttpClient client;
    private Gson gson;
    private AlertDialog loading;
    private ImageView Back;
    private Button Ingresar;
    private TextInputEditText txt_Email, txt_Pass;
    private String URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/";
    private SharedPreferences cookies;
    private Handler mHandler;

    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        cookies = getSharedPreferences("SHA_CST_DB", MODE_PRIVATE);

        //URL = cookies.getString("url", "https://ceramicproesmeralda.azurewebsites.net");

        if(URL.equals("")){
            Toast.makeText(this, "Por favor ingresa la url del servidor", Toast.LENGTH_SHORT).show();
        }

        gson = new Gson();

        Back = findViewById(R.id.btn_back_login);
        Ingresar = findViewById(R.id.Btn_Login);
        txt_Email = findViewById(R.id.txt_Email);
        txt_Pass = findViewById(R.id.txt_Pass);

        Back.setOnClickListener(view -> {
            RegisterToken("0000000000", "0000000000", "0000000000", "0000000000", "0000000000");
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        });

        Ingresar.setOnClickListener(view -> {
            String _email_ = txt_Email.getText().toString();
            String _pass_ = txt_Pass.getText().toString();
            if(_email_.equals("") || _pass_.equals("")){
                if(_email_.equals("")){
                    Message("Información", "Por favor escribe tu correo electrónico");
                }else if(!ValidEmail(_email_)){
                    Message("Información", "Por favor escribe un correo electrónico valido");
                }else if(_pass_.equals("")){
                    Message("Información", "Por favor escribe tu contraseña");
                }
            }else{
                Show();
                LogIn();
            }
        });
    }
    private void LogIn() {
        if(!URL.equals("")){
            AuthRequestVM req = new AuthRequestVM();
            req.acUser = txt_Email.getText().toString();
            req.acPassword = txt_Pass.getText().toString();
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
                            loading.hide();
                            Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
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
                                    RegisterToken(res.data.strToken, res.data.fullName, res.data.strCode, txt_Email.getText().toString(), txt_Pass.getText().toString());
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();

                                } else{
                                    loading.hide();
                                    Message("Información", res.message);
                                }
                            }
                        });
                    }else{
                        loading.hide();
                        Message("Error", response.message() + " - " + response.code());
                    }
                }
            });

        }else{
            loading.hide();
            Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
        }
    }
    private void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.design_dialog_progress);
        loading = builder.create();
        loading.show();
    }
    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(LoginActivity.this);
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }
    private boolean ValidEmail(String email){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void RegisterToken(String strToken, String fullName, String strCode, String user, String pass){
        SharedPreferences sharedPreferences = getSharedPreferences("SHA_CST_DB", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("strToken", strToken);
        editor.putString("fullName", fullName);
        editor.putString("strCode", strCode);
        editor.putString("user", user);
        editor.putString("pass", pass);
        editor.apply();
    }

}