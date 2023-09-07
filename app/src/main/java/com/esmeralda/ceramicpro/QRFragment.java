package com.esmeralda.ceramicpro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esmeralda.ceramicpro.ui.account.AccountFragment;
import com.esmeralda.ceramicpro.ui.register_account_one.RegisterAccountOneFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRFragment extends Fragment {
    private View view;
    private ImageView ivQrCodeA;
    private TextView tv_NameQR;
    private ImageView back_home;
    private String strCode;
    private SharedPreferences cookies;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_q_r, container, false);
        back_home = view.findViewById(R.id.btn_back_home);
        ivQrCodeA = view.findViewById(R.id.QR_content);
        tv_NameQR = view.findViewById(R.id.NameQR);

        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        String fullName = cookies.getString("fullName", "Inicia tu sesión");
        strCode = cookies.getString("strCode", "0");

        back_home.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_navigation_qr_to_navigation_account);
        });

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
            tv_NameQR.setText(fullName);
        }catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }


}