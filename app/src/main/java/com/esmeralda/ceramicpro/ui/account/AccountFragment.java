package com.esmeralda.ceramicpro.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.LoginActivity;
import com.esmeralda.ceramicpro.MainActivity;
import com.esmeralda.ceramicpro.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class AccountFragment extends Fragment {
    private SharedPreferences cookies;
    private View view;
    private TextView txt_fullName;
    private ImageView ivQrCodeA;
    private LottieAnimationView ivaQrCodeA;
    private CardView btn_logout, card_view_alert, card_view_name_account;
    private String strCode;

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
        ivQrCodeA = view.findViewById(R.id.QR_contentA);
        ivaQrCodeA = view.findViewById(R.id.QR_content_AnimA);

        btn_logout.setOnClickListener(view -> {
            RegisterToken("0000000000", "0000000000", "0000000000");
            startActivity(new Intent(view.getContext(), HomeActivity.class));
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
            ivaQrCodeA.setVisibility(View.VISIBLE);
        }else{
            card_view_alert.setVisibility(View.GONE);
            card_view_name_account.setVisibility(View.VISIBLE);
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(
                        strCode,
                        BarcodeFormat.QR_CODE,
                        1000,
                        1000
                );
                ivQrCodeA.setImageBitmap(bitmap);
                ivQrCodeA.setVisibility(View.VISIBLE);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        txt_fullName.setText(fullName);
    }

    public void RegisterToken(String strToken, String fullName, String strCode){
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("SHA_CST_DB", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("strToken", strToken);
        editor.putString("fullName", fullName);
        editor.putString("strCode", strCode);
        editor.apply();
    }

}