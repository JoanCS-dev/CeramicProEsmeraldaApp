package com.esmeralda.ceramicpro.ui.account;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.esmeralda.ceramicpro.AppointmentHistoryActivity;
import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.LoginActivity;
import com.esmeralda.ceramicpro.MainActivity;
import com.esmeralda.ceramicpro.QRActivity;
import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.model.AppointmentResponseVM;
import com.esmeralda.ceramicpro.model.BrandVM;
import com.esmeralda.ceramicpro.model.ColorVM;
import com.esmeralda.ceramicpro.model.HoursRequestVM;
import com.esmeralda.ceramicpro.model.LastAppointmentRequestVM;
import com.esmeralda.ceramicpro.model.LastAppointmentResponseVM;
import com.esmeralda.ceramicpro.model.LastAppointmentVM;
import com.esmeralda.ceramicpro.model.QuoteDatesVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.esmeralda.ceramicpro.model.ServiceVM;
import com.esmeralda.ceramicpro.model.TypeVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountFragment extends Fragment {
    private MediaType mediaType = MediaType.parse("application/json");
    private SharedPreferences cookies;
    private Dialog loading;
    private View view;
    private TextView txt_fullName;
    private ImageView ivQrCodeA;
    private LottieAnimationView ivaQrCodeA;
    private List<LastAppointmentVM> lst;
    private String token, URL = "https://ceramicproesmeralda.azurewebsites.net";
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
        ivQrCodeA = view.findViewById(R.id.QR_contentA);
        ivaQrCodeA = view.findViewById(R.id.QR_content_AnimA);



        client = new OkHttpClient();
        gson = new Gson();
        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");

        btn_logout.setOnClickListener(view -> {
            RegisterToken("0000000000", "0000000000", "0000000000");
            startActivity(new Intent(view.getContext(), HomeActivity.class));
        });

        btn_history.setOnClickListener(view -> {
            startActivity(new Intent(view.getContext(), AppointmentHistoryActivity.class));
        });

        gotoqrcode.setOnClickListener(view -> {
            startActivity(new Intent(view.getContext(), QRActivity.class));
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
            card_view_alert.setVisibility(View.VISIBLE);
            //ivaQrCodeA.setVisibility(View.VISIBLE);
        }else{
            card_view_alert.setVisibility(View.GONE);
            card_view_name_account.setVisibility(View.VISIBLE);
            try {
                //cvLast_Date.setVisibility(View.VISIBLE);

                txt_fullName.setText(fullName.substring(0, 20) + "...");
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void RegisterToken(String strToken, String fullName, String strCode){
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("SHA_CST_DB", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("strToken", strToken);
        editor.putString("fullName", fullName);
        editor.putString("strCode", strCode);
        editor.apply();
    }



    private void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.design_dialog_progress);
        loading = builder.create();
        loading.show();
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(view.getContext());
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }

}