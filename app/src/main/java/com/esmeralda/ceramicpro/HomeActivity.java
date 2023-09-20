package com.esmeralda.ceramicpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esmeralda.ceramicpro.model.AuthRequestVM;
import com.esmeralda.ceramicpro.model.AuthResponseVM;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private Button LoginRedirect, RegisterRedirect;
    private TextView Invitado;
    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        LoginRedirect = findViewById(R.id.Btn_Act_Login);
        RegisterRedirect = findViewById(R.id.Btn_Act_Register);
        Invitado = findViewById(R.id.Btn_Invitado);
        dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        createDialog();

        LoginRedirect.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        });

        Invitado.setOnClickListener(view -> dialog.show());

        RegisterRedirect.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
        });
    }


    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.design_bottom_dialog,null,false);

        Button btn_si = view.findViewById(R.id.Btn_Si);
        Button btn_no = view.findViewById(R.id.Btn_No);

        btn_si.setOnClickListener(view1 -> {
            dialog.dismiss();
            RegisterTokenHome("0000000000", "0000000000", "0000000000", "0000000000", "0000000000");
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        });

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.setContentView(view);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void ShowModalEditText(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //URL = cookies.getString("url", "https://");


        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText("");
        alert.setView(input);
        alert.setPositiveButton("Guardar",
                (dialog, which) -> {
                    String url = input.getText().toString();
                    SharedPreferences sharedPreferences = getSharedPreferences("SHA_CST_DB", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("url", url);
                    editor.apply();
                });

        alert.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        alert.show();
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

}