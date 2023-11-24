package com.esmeralda.ceramicpro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.esmeralda.ceramicpro.LoginActivity;
import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.model.AccountRequestVM;
import com.esmeralda.ceramicpro.model.PasswordsRequestVM;
import com.esmeralda.ceramicpro.model.PeopleRequestVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.esmeralda.ceramicpro.ui.account.AccountFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PasswordFragment extends Fragment {
    private View view;
    private ImageView back_home;
    private SharedPreferences cookies;
    private String token;
    private Dialog loading;
    private Gson gson;
    private OkHttpClient client;
    private MediaType mediaType = MediaType.parse("application/json");
    private String URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/";
    private TextInputEditText txt_NewPass, txt_Pass, txt_Confirm_Pass;
    private Button save_pass;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_password, container, false);
        back_home = view.findViewById(R.id.btn_back_home);
        gson = new Gson();
        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");
        txt_Pass = view.findViewById(R.id.txt_currentpass);
        txt_NewPass = view.findViewById(R.id.txt_newpass);
        txt_Confirm_Pass = view.findViewById(R.id.txt_confpass);
        save_pass = view.findViewById(R.id.Btn_SavePass);

        back_home.setOnClickListener(view -> {
            Fragment fragment = new AccountFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        save_pass.setOnClickListener(view -> {
            String __txt_Pass = txt_Pass.getText().toString();
            String __txt_NewPass = txt_NewPass.getText().toString();
            String __txt_Confirm_Pass = txt_Confirm_Pass.getText().toString();
            if(__txt_Pass.equals("") || __txt_NewPass.equals("") || __txt_Confirm_Pass.equals("")){
                if(__txt_Pass.equals("")){
                    Message("Información", "Por favor escribe tu contraseña anterior");
                }else if(__txt_NewPass.equals("")){
                    Message("Información", "Por favor escribe tu nueva contraseña");
                }else if(__txt_Confirm_Pass.equals("")){
                    Message("Información", "Por favor escribe tu nueva contraseña");
                }
            }else {
                if (!__txt_NewPass.equals(__txt_Confirm_Pass)) {
                    Message("Información", "Las contraseñas no coinciden");
                } else {
                    SaveAs();
                }
            }
        });

        return view;
    }

    public void SaveAs() {
        if(!URL.equals("")){
            PasswordsRequestVM pass = new PasswordsRequestVM();
            pass.acPassword = txt_Pass.getText().toString();
            pass.acPasswordNew = txt_NewPass.getText().toString();

            RequestBody body = RequestBody.create(gson.toJson(pass), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "AppAccount/ChangePwd")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");

                        }
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        ResponseVM res = gson.fromJson(string_json, ResponseVM.class);
                        if(getActivity() == null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    RegisterToken("0000000000", "0000000000", "0000000000","0000000000", "0000000000");
                                    startActivity(new Intent(view.getContext(), LoginActivity.class));
                                }else{
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
            Message("Error", "Por favor ingresa la url del servidor");
        }
    }

    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(view.getContext());
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }
    private void RegisterToken(String strToken, String fullName, String strCode, String user, String pass){
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("SHA_CST_DB", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("strToken", strToken);
        editor.putString("fullName", fullName);
        editor.putString("strCode", strCode);
        editor.putString("user", user);
        editor.putString("pass", pass);
        editor.apply();
    }
    private void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.design_dialog_progress);
        loading = builder.create();
        loading.show();
    }
}