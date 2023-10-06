package com.esmeralda.ceramicpro.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
*/

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.LoginActivity;
import com.esmeralda.ceramicpro.MainActivity;
import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.model.AccountRequestVM;
import com.esmeralda.ceramicpro.model.InterestedInOurServiceVM;
import com.esmeralda.ceramicpro.model.JoinOurTeamRequestVM;
import com.esmeralda.ceramicpro.model.PeopleRequestVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class HomeFragment extends Fragment {
    private View view;
    private BottomSheetDialog dialog;
    private ImageView teamwork, img_inversion, img_new, img_maintenance;
    private String URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/";
    private MediaType mediaType = MediaType.parse("application/json");
    private OkHttpClient client;
    private Gson gson;
    private Dialog loading;
    private Integer x = 0;
    private MaterialCardView cw_inversion, cw_new, cw_maintenance;

    private SharedPreferences cookies;
    private TextView themename;
    private String NameTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(x == 1){
            x=0;
            loading.hide();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_home, container, false);
        dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        teamwork = view.findViewById(R.id.TeamWork);

        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>(); // Create image list


        imageList.add(new SlideModel(R.drawable.img_1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_3, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_4, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_5, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_6, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_7, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_8, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_9, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_10, ScaleTypes.FIT));

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);

        gson = new Gson();

        themename = view.findViewById(R.id.themeNameHome);
        NameTheme = themename.getText().toString();

        img_inversion = view.findViewById(R.id.img_Inversion);
        img_new = view.findViewById(R.id.img_New);
        img_maintenance = view.findViewById(R.id.img_Maintenance);

        cw_inversion = view.findViewById(R.id.Inversion);
        cw_new = view.findViewById(R.id.New);
        cw_maintenance = view.findViewById(R.id.Maintenance);


        cw_inversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(NameTheme.equals("White")){
                    img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_Inversion), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                }else{
                    img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_Inversion), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }*/

                Show();
                SaveInterested("OPTION1");

            }
        });
        cw_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(NameTheme.equals("White")) {
                    img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_New), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                }else{
                    img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_New), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }*/

                Show();
                SaveInterested("OPTION2");
            }
        });
        cw_maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(NameTheme.equals("White")) {
                    img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_Maintenance), android.graphics.PorterDuff.Mode.MULTIPLY);
                }else{
                    img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_Maintenance), android.graphics.PorterDuff.Mode.MULTIPLY);
                }*/

                Show();
                SaveInterested("OPTION3");
            }
        });
        /*

        imageList.add(new SlideModel(R.drawable.img_porsche_suv, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_ford_focus_rs, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_infiniti, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_lamborghini_huracan, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_nissan_gtr, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_skyline_r34, ScaleTypes.FIT));

        */
        /*
        imageList.add(new SlideModel(R.drawable.img_1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_3, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_4, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_5, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_6, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_7, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_8, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_9, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_10, ScaleTypes.FIT));

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        */

        createDialog();

        teamwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        return view;

    }


    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.request_dialog,null,false);

        Button btn_send = view.findViewById(R.id.Btn_Send);
        Button btn_no = view.findViewById(R.id.Btn_No);
        TextInputEditText txt_NameFullRD = view.findViewById(R.id.txt_NameFull);
        TextInputEditText txt_EmailRD = view.findViewById(R.id.txt_Email);
        TextInputEditText txt_PhoneNumberRD = view.findViewById(R.id.txt_PhoneNumber);

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        btn_send.setOnClickListener(view1 -> {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if(txt_NameFullRD.getText().toString().equals("")){
                Message("Información", "Por favor escribe tu nombre completo");
            } else if (txt_EmailRD.getText().toString().equals("")) {
                Message("Información", "Por favor escribe tu correo electrónico");
            } else if (!ValidEmail(txt_EmailRD.getText().toString())) {
                Message("Información", "Por favor escribe un correo electrónico valido");
            } else if (txt_PhoneNumberRD.getText().toString().equals("")) {
                Message("Información", "Por favor escribe tu número teléfonico");
            }else{
                if(!URL.equals("")){
                    Show();
                    JoinOurTeamRequestVM joinOurTeamRequestVM = new JoinOurTeamRequestVM();
                    joinOurTeamRequestVM.joinOurTeamID = 0;
                    joinOurTeamRequestVM.joinStatus = "ACTIVO";
                    joinOurTeamRequestVM.joinRDate = date;
                    joinOurTeamRequestVM.joinFullName = txt_NameFullRD.getText().toString();
                    joinOurTeamRequestVM.joinEmail = txt_EmailRD.getText().toString();
                    joinOurTeamRequestVM.joinPhoneNumber = txt_PhoneNumberRD.getText().toString();
                    joinOurTeamRequestVM.accountID = 0;

                    RequestBody body = RequestBody.create(gson.toJson(joinOurTeamRequestVM), mediaType);
                    Request request = new Request.Builder()
                            .url(URL + "/AppJoinOurTeam/Add")
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    x=0;
                                    loading.hide();
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
                                            x=0;
                                            loading.hide();
                                            Message("Correcto", res.message);
                                            dialog.dismiss();
                                        }else{
                                            x=0;
                                            loading.hide();
                                            Message("Información", res.message);
                                        }
                                    }
                                });
                            }else{
                                x=0;
                                loading.hide();
                                Message("Error", response.message() + " - " + response.code());
                            }
                        }
                    });

                }else{
                    x=0;
                    loading.hide();
                    Message("Error", "Por favor ingresa la url del servidor");
                }
            }


        });

        dialog.setContentView(view);
    }
    private boolean ValidEmail(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);
        return mather.find();
    }
    public void SaveInterested(String Option) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(!URL.equals("")){
            InterestedInOurServiceVM interestedInOurServiceVM = new InterestedInOurServiceVM();
            interestedInOurServiceVM.interestedInOurServiceID = 0;
            interestedInOurServiceVM.interestedStatus = "ACTIVE";
            interestedInOurServiceVM.interestedRDate = date;
            interestedInOurServiceVM.interestedOption = Option;
            interestedInOurServiceVM.accountID = 0;

            RequestBody body = RequestBody.create(gson.toJson(interestedInOurServiceVM), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/AppInterestedInOurService/Add")
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            x=0;
                            loading.hide();
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
                                Message("Información", res.message);
                            }
                        });
                    }else{
                        Message("Error", response.message() + " - " + response.code());
                    }
                }
            });

        }else{
            loading.hide();
            x=0;
            Message("Error", "Por favor ingresa la url del servidor");
        }
    }
    private void Show() {
        x = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.design_dialog_progress);
        loading = builder.create();
        loading.show();
    }
    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(view.getContext());
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }

}