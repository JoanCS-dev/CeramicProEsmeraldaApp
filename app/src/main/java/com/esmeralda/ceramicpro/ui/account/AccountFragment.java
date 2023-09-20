package com.esmeralda.ceramicpro.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.esmeralda.ceramicpro.AppointmentHistoryFragment;
import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.QRFragment;
import com.esmeralda.ceramicpro.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class AccountFragment extends Fragment {
    private MediaType mediaType = MediaType.parse("application/json");
    private SharedPreferences cookies;
    private View view;
    private TextView txt_fullName, close_sesion;
    private String token, URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/";
    private CardView btn_logout, btn_history, card_view_alert, card_view_name_account;
    private String strCode;
    private OkHttpClient client;
    private ImageButton gotoqrcode;
    private Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        card_view_alert = view.findViewById(R.id.card_view_alert);
        card_view_name_account = view.findViewById(R.id.card_view_name_account);
        txt_fullName = view.findViewById(R.id.txt_fullName);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_history = view.findViewById(R.id.btn_history);
        gotoqrcode = view.findViewById(R.id.btn_gotoqrcode);
        close_sesion = view.findViewById(R.id.close_session);
        client = new OkHttpClient();
        gson = new Gson();
        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");

        btn_logout.setOnClickListener(view -> {
            if(token.equals("") || token.equals("0000000000")){
                RegisterToken("0000000000", "0000000000", "0000000000","0000000000", "0000000000");
                startActivity(new Intent(view.getContext(), HomeActivity.class));
            }else{
                MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(view.getContext());
                Builder.setTitle("Cerrar Sesión")
                        .setMessage("¿Seguro que desea cerrar sesión?")
                        .setCancelable(false);

                Builder.setPositiveButton("Ok", (dialog, which) -> {
                    RegisterToken("0000000000", "0000000000", "0000000000","0000000000", "0000000000");
                    startActivity(new Intent(view.getContext(), HomeActivity.class));
                });
                Builder.setNegativeButton("Cancelar", null);
                Builder.show();
            }

        });

        btn_history.setOnClickListener(view -> {
            Fragment fragment = new AppointmentHistoryFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        gotoqrcode.setOnClickListener(view -> {
            Fragment fragment = new QRFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            //startActivity(new Intent(view.getContext(), QRActivity.class));
        });
        ShowFullName();

        return view;
    }

    private void ShowFullName(){
        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        String fullName = cookies.getString("fullName", "Inicia tu sesión");
        strCode = cookies.getString("strCode", "0");
        if(fullName.equals("") || fullName.equals("0000000000")){
            card_view_name_account.setVisibility(View.GONE);
            btn_history.setVisibility(View.GONE);
            card_view_alert.setVisibility(View.VISIBLE);
            close_sesion.setText("Salir");
        }else{
            card_view_alert.setVisibility(View.GONE);
            card_view_name_account.setVisibility(View.VISIBLE);
            try {
                txt_fullName.setText(fullName.substring(0, 20) + "...");
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

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


}