package com.esmeralda.ceramicpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {
    private ImageView ivQrCodeA;
    private TextView tv_NameQR;
    private ImageButton back_home;
    private String strCode;
    private SharedPreferences cookies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);
        back_home = findViewById(R.id.btn_back_home);
        ivQrCodeA = findViewById(R.id.QR_content);
        tv_NameQR = findViewById(R.id.NameQR);

        cookies = getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        String fullName = cookies.getString("fullName", "Inicia tu sesión");
        strCode = cookies.getString("strCode", "0");

        back_home.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
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
    }
}
