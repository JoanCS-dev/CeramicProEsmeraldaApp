package com.esmeralda.ceramicpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.esmeralda.ceramicpro.model.LastAppointmentRequestVM;
import com.esmeralda.ceramicpro.model.LastAppointmentResponseVM;
import com.esmeralda.ceramicpro.model.LastAppointmentVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

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

public class AppointmentHistoryActivity extends AppCompatActivity {

    RecyclerView recycleAppointment;
    AppointmentAdapter rvAppointmentAdapter;
    ArrayList<LastAppointmentVM> lastAppointmentArray;
    private MediaType mediaType = MediaType.parse("application/json");
    private SharedPreferences cookies;
    private ImageButton back;
    private List<LastAppointmentVM> lst;
    private Dialog loading;
    private Gson gson;
    private OkHttpClient client;
    private String token, URL = "https://ceramicproesmeralda.azurewebsites.net";
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        client = new OkHttpClient();
        gson = new Gson();
        cookies = getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");

        back = findViewById(R.id.btn_back_home_activity);

        swipeLayout = findViewById(R.id.swap);
        swipeLayout.setOnRefreshListener(() -> {
            swipeLayout.setRefreshing(false);
            SearchData();
        });

        lastAppointmentArray = new ArrayList<>();
        recycleAppointment = findViewById(R.id.RecycleAppointment);
        recycleAppointment.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvAppointmentAdapter = new AppointmentAdapter(AppointmentHistoryActivity.this, lastAppointmentArray);
        recycleAppointment.setAdapter(rvAppointmentAdapter);
        SearchData();

        back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void SearchData() {
        if(!URL.equals("") && !token.equals("")){
            Show();
            LastAppointmentRequestVM lastAppointmentRequestVM = new LastAppointmentRequestVM();
            lastAppointmentRequestVM.quotesID = 0;
            lastAppointmentRequestVM.servicePriceID = 0;
            lastAppointmentRequestVM.colorID = 0;
            lastAppointmentRequestVM.serviceDesc = "";
            lastAppointmentRequestVM.colorName = "";
            lastAppointmentRequestVM.quotesSTS = "";
            lastAppointmentRequestVM.quoteHoursID = 0;
            lastAppointmentRequestVM.accountID = 0;
            lastAppointmentRequestVM.vehicleModelID = 0;
            RequestBody body = RequestBody.create(gson.toJson(lastAppointmentRequestVM), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/Api/Quotes/List")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    AppointmentHistoryActivity.this.runOnUiThread(new Runnable() {
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
                        LastAppointmentResponseVM res = gson.fromJson(string_json, LastAppointmentResponseVM.class);
                        AppointmentHistoryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    loading.hide();
                                    lst = res.data;

                                    for (LastAppointmentVM item:lst) {
                                        LastAppointmentVM model = new LastAppointmentVM();
                                        model.setQuotesDate(item.quotesDate);
                                        model.setQuotesHour(item.quotesHour);
                                        model.setServiceDesc(item.serviceDesc);
                                        model.setVehicleBrandName(item.vehicleBrandName);
                                        model.setVehicleModelName(item.vehicleModelName);
                                        model.setColorName(item.colorName);
                                        model.setQuotesSTS(item.quotesSTS);

                                        lastAppointmentArray.add(model);

                                    }

                                    PutDataIntoRecyclerView(lastAppointmentArray);

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

    private void PutDataIntoRecyclerView(List<LastAppointmentVM> lastAppointmentArray){
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this, lastAppointmentArray);
        recycleAppointment.setLayoutManager(new LinearLayoutManager(this));
        recycleAppointment.setAdapter(appointmentAdapter);
    }

    private void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.design_dialog_progress);
        loading = builder.create();
        loading.show();
    }
    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(this);
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }

}