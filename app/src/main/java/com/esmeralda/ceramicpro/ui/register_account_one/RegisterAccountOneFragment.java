package com.esmeralda.ceramicpro.ui.register_account_one;

import static java.net.HttpURLConnection.HTTP_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.LoginActivity;
import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.RegisterActivity;
import com.esmeralda.ceramicpro.model.AccountRequestVM;
import com.esmeralda.ceramicpro.model.PeopleRequestVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.esmeralda.ceramicpro.ui.register_account_two.RegisterAccountTwoFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterAccountOneFragment extends Fragment {

    private Button B_Continuar;
    private Dialog loading;
    private Gson gson;
    private OkHttpClient client;
    private MediaType mediaType = MediaType.parse("application/json");
    private String URL = "https://ceramicproesmeralda.azurewebsites.net";
    private ImageButton Back;
    private TextInputEditText txt_Name, txt_Lastname, txt_Phone, txt_Email, txt_Pass, txt_Confirm_Pass, txt_CodigoPostal;
    private String txt_NameParse, txt_LastnameParse, txt_PhoneParse, txt_EmailParse, txt_PassParse;
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_account_one, container, false);
        B_Continuar = view.findViewById(R.id.Btn_Continuar);
        Back = view.findViewById(R.id.Btn_Back_F);
        txt_Name = view.findViewById(R.id.txt_Name);
        txt_Lastname = view.findViewById(R.id.txt_Lastname);
        txt_Phone = view.findViewById(R.id.txt_Phone);
        txt_Email = view.findViewById(R.id.txt_Email);
        txt_Pass = view.findViewById(R.id.txt_Pass);
        txt_Confirm_Pass = view.findViewById(R.id.txt_Confirm_Pass);
        txt_CodigoPostal = view.findViewById(R.id.txt_CodigoPostal);
        validateBack();

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
        client = new OkHttpClient();
        gson = new Gson();

        B_Continuar.setOnClickListener(view -> {
            String __txt_Name = txt_Name.getText().toString();
            String __txt_Lastname = txt_Lastname.getText().toString();
            String __txt_Phone = txt_Phone.getText().toString();
            String __txt_Email = txt_Email.getText().toString();
            String __txt_Pass = txt_Pass.getText().toString();
            String __txt_Confirm_Pass = txt_Confirm_Pass.getText().toString();
            if(__txt_Name.equals("") || __txt_Lastname.equals("") || __txt_Phone.equals("") || __txt_Email.equals("") || __txt_Pass.equals("") || __txt_Confirm_Pass.equals("")){
                if(__txt_Name.equals("")){
                    Message("Información", "Por favor escribe tu(s) nombre(s)");
                }else if(__txt_Lastname.equals("")){
                    Message("Información", "Por favor escribe tu(s) apellido(s)");
                }else if(__txt_Phone.equals("")){
                    Message("Información", "Por favor escribe tu télefono");
                }else if(__txt_Email.equals("")){
                    Message("Información", "Por favor escribe el correo electrónico");
                }else if(!ValidEmail(txt_Email.getText().toString())){
                    Message("Información", "Por favor escribe un correo electrónico valido");
                }else if(__txt_Pass.equals("")){
                    Message("Información", "Por favor escribe la contraseña");
                }
            }else {
                if(!__txt_Pass.equals(__txt_Confirm_Pass)){
                    Message("Información", "Las contraseñas no coinciden");
                }else{
                    Show();
                    SaveAs();
                    /*Bundle result = new Bundle();
                    result.putString("txt_Name", txt_Name.getText().toString());
                    result.putString("txt_Lastname", txt_Lastname.getText().toString());
                    result.putString("txt_Phone", txt_Phone.getText().toString());
                    result.putString("txt_Email", txt_Email.getText().toString());
                    result.putString("txt_Pass", txt_Pass.getText().toString());
                    getParentFragmentManager().setFragmentResult("requestKey", result);
                    Fragment fragment = new RegisterAccountTwoFragment();
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    fm.replace(R.id.container, fragment).commit();*/
                }
            }
        });

        return view;
    }
    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(view.getContext());
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }
    private boolean ValidEmail(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);
        return mather.find();
    }

    private void validateBack() {
        getParentFragmentManager().setFragmentResultListener("databack", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                txt_NameParse = bundle.getString("txt_NameParse");
                txt_LastnameParse = bundle.getString("txt_LastnameParse");
                txt_PhoneParse = bundle.getString("txt_PhoneParse");
                txt_EmailParse = bundle.getString("txt_EmailParse");
                txt_PassParse = bundle.getString("txt_PassParse");
                // Do something with the result...

                txt_Name.setText(txt_NameParse);
                txt_Lastname.setText(txt_LastnameParse);
                txt_Phone.setText(txt_PhoneParse);
                txt_Email.setText(txt_EmailParse);
                txt_Pass.setText(txt_PassParse);
                txt_Confirm_Pass.setText("");
            }
        });
    }

    private void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.design_dialog_progress);
        loading = builder.create();
        loading.show();
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void SaveAs() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(!URL.equals("")){
            PeopleRequestVM peopleRequestVM = new PeopleRequestVM();
            peopleRequestVM.peopleID = 0;
            peopleRequestVM.peFirstName = txt_Name.getText().toString();
            peopleRequestVM.peLastName = txt_Lastname.getText().toString();
            peopleRequestVM.peDateOfBirth = "2023-01-01";
            peopleRequestVM.peStatus = false;
            peopleRequestVM.peRDate = date;
            //peopleRequestVM.peStreet = txt_Calle.getText().toString();
            peopleRequestVM.peStreet = "SD";
            peopleRequestVM.peOutsideCode = "SD";
            peopleRequestVM.peInsideCode = "SD";
            peopleRequestVM.PeCP = txt_CodigoPostal.getText().toString();
            peopleRequestVM.settlementID = 0;

            AccountRequestVM acc = new AccountRequestVM();
            acc.accountID = 0;
            acc.acUser = txt_Email.getText().toString();
            acc.acPassword = txt_Pass.getText().toString();
            acc.acEmailAddress = txt_Email.getText().toString();
            acc.acPhoneNumber = txt_Phone.getText().toString();
            acc.acVerifyEmail = false;
            acc.acStatus = "NA";
            acc.acRDate = date;
            acc.peopleID = 0;
            acc.profileID = 0;
            acc.peopleVM = peopleRequestVM;

            RequestBody body = RequestBody.create(gson.toJson(acc), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/Api/Account/Add")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    loading.hide();
                    Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
                    SaveAs();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        ResponseVM res = gson.fromJson(string_json, ResponseVM.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    loading.hide();
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }else{
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
            Message("Error", "Por favor ingresa la url del servidor");
            SaveAs();
        }
    }
}