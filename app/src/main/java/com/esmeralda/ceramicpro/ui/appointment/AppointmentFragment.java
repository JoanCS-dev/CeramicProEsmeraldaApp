package com.esmeralda.ceramicpro.ui.appointment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.model.AppointmentResponseVM;
import com.esmeralda.ceramicpro.model.BrandVM;
import com.esmeralda.ceramicpro.model.ColorVM;
import com.esmeralda.ceramicpro.model.HoursRequestVM;
import com.esmeralda.ceramicpro.model.ModelRequestVM;
import com.esmeralda.ceramicpro.model.ModelVM;
import com.esmeralda.ceramicpro.model.QuoteDatesResponseVM;
import com.esmeralda.ceramicpro.model.QuoteDatesVM;
import com.esmeralda.ceramicpro.model.QuoteHoursVM;
import com.esmeralda.ceramicpro.model.QuotesRequestVM;
import com.esmeralda.ceramicpro.model.ResponseModelVM;
import com.esmeralda.ceramicpro.model.ResponseQuotesHoursVM;
import com.esmeralda.ceramicpro.model.ResponseServiceVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.esmeralda.ceramicpro.model.ServiceRequestVM;
import com.esmeralda.ceramicpro.model.ServiceVM;
import com.esmeralda.ceramicpro.model.TypeVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppointmentFragment extends Fragment {

    private MediaType mediaType = MediaType.parse("application/json");
    private View view;
    private Integer x = 0;
    private Dialog loading;
    private CardView card_view_alert;
    private TextInputLayout dropdown_type_layout, dropdown_service_layout, dropdown_brand_layout, dropdown_model_layout, dropdown_color_layout, dropdown_date_layout, dropdown_hour_layout;
    private AutoCompleteTextView dropdown_type, dropdown_service, dropdown_brand, dropdown_model, dropdown_color, dropdown_date, dropdown_hour;

    private Button btn_confirm;
    private String typeName, serviceName, brandName, modelName, dateName, colorName, hoursName, token, URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/";
    private List<ServiceVM> lst_service;
    private List<BrandVM> lst_brand;
    private List<ModelVM> lst_model;
    private List<ColorVM> lst_color;
    private List<QuoteHoursVM> lst_hours;
    private List<QuoteDatesVM> lst_dates;
    private List<TypeVM> lst_type;
    private long brandID, modelID, typeID, dateID, hoursID, serviceID, colorID;
    private int dia, mes, ano;
    private SharedPreferences cookies;
    private OkHttpClient client;
    private Gson gson;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(x == 1){
            x=0;
            loading.hide();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);

        card_view_alert = view.findViewById(R.id.card_view_alert);

        swipeLayout = view.findViewById(R.id.swap);
        swipeLayout.setOnRefreshListener(() -> {
            if(token.equals("") || token.equals("0000000000")){
                swipeLayout.setRefreshing(false);
            }else{
                swipeLayout.setRefreshing(false);
                SearchData();
                Show();
            }

        });
        dropdown_type_layout = view.findViewById(R.id.dropdown_type_layout);
        dropdown_service_layout = view.findViewById(R.id.dropdown_service_layout);
        dropdown_brand_layout = view.findViewById(R.id.dropdown_brand_layout);
        dropdown_model_layout = view.findViewById(R.id.dropdown_model_layout);
        dropdown_color_layout = view.findViewById(R.id.dropdown_color_layout);
        dropdown_date_layout = view.findViewById(R.id.dropdown_date_layout);
        dropdown_hour_layout = view.findViewById(R.id.dropdown_hour_layout);

        dropdown_type = view.findViewById(R.id.dropdown_type);
        dropdown_service = view.findViewById(R.id.dropdown_service);
        dropdown_brand = view.findViewById(R.id.dropdown_brand);
        dropdown_model = view.findViewById(R.id.dropdown_model);
        dropdown_color = view.findViewById(R.id.dropdown_color);
        dropdown_date = view.findViewById(R.id.dropdown_date);
        dropdown_hour = view.findViewById(R.id.dropdown_hour);
        btn_confirm = view.findViewById(R.id.btn_confirm);


        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");
        gson = new Gson();

        //InitLst();
        btn_confirm.setOnClickListener(view -> {
            if(dropdown_type.getText().toString().equals("Tipo de Vehículo") || dropdown_type.getText().toString().equals("")){
                Message("Información", "Por favor selecciona que tipo de vehículo tienes");

            }else if(dropdown_service.getText().toString().equals("Servicio") || dropdown_service.getText().toString().equals("")){
                Message("Información", "Por favor selecciona el servicio a agendar");

            }else if(dropdown_brand.getText().toString().equals("Marca del vehículo") || dropdown_brand.getText().toString().equals("")){
                Message("Información", "Por favor selecciona la marca de tu vehículo");

            }else if(dropdown_model.getText().toString().equals("Modelo del vehículo") || dropdown_model.getText().toString().equals("")){
                Message("Información", "Por favor selecciona el modelo de tu vehículo");

            }else if(dropdown_color.getText().toString().equals("Color del vehículo") || dropdown_color.getText().toString().equals("")){
                Message("Información", "Por favor selecciona el color de tu vehículo");

            }else if(dropdown_date.getText().toString().equals("Fecha de la cita") || dropdown_date.getText().toString().equals("")){
                Message("Información", "Por favor selecciona la fecha para tu cita");

            }else if(dropdown_hour.getText().toString().equals("Hora de la cita") || dropdown_hour.getText().toString().equals("")){
                Message("Información", "Por favor selecciona la hora para tu cita");
            }else{
                Show();
                Confirm();
            }

        });


        dropdown_type.setOnItemClickListener((adapterView, view, i, l) -> {
            typeName = adapterView.getItemAtPosition(i).toString();
            typeID = 0;
            dropdown_service.clearListSelection();
            dropdown_service.setText("Servicio");
            for (TypeVM item: lst_type) {
                if(typeName.equals(item.name)){
                    typeID = item.id;
                    if(!URL.equals("") && !token.equals("")){
                        Show();
                        ServiceRequestVM serviceRequestVM = new ServiceRequestVM();
                        serviceRequestVM.TypeServiceVehicleID = typeID;
                        RequestBody body = RequestBody.create(gson.toJson(serviceRequestVM), mediaType);
                        Request request = new Request.Builder()
                                .url(URL + "/AppService/GetShortByType")
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
                                    ResponseServiceVM res = gson.fromJson(string_json, ResponseServiceVM.class);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.ok) {
                                                x=0;
                                                loading.hide();
                                                lst_service = res.data;

                                                ArrayList<String> arr_service = new ArrayList<>();
                                                for (ServiceVM item:lst_service) {
                                                    arr_service.add(item.name);
                                                }

                                                ArrayAdapter<String> arrayAdapterService = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_service);

                                                dropdown_service.setAdapter(arrayAdapterService);
                                            } else {
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
                        Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
                    }
                    break;
                }
            }

        });

        dropdown_service.setOnItemClickListener((adapterView, view, i, l) -> {
            serviceName = adapterView.getItemAtPosition(i).toString();
            serviceID = 0;
            for (ServiceVM item: lst_service) {
                if (serviceName.equals(item.name)) {
                    serviceID = item.id;
                    break;
                }
            }
        });



        dropdown_brand.setOnItemClickListener((adapterView, view, i, l) -> {
            brandName = adapterView.getItemAtPosition(i).toString();
            brandID = 0;
            dropdown_model.clearListSelection();
            dropdown_model.setText("Modelo del vehículo");
            for (BrandVM item: lst_brand) {
                if(brandName.equals(item.name)){
                    brandID = item.id;
                    if(!URL.equals("") && !token.equals("")){
                        Show();
                        ModelRequestVM modelRequestVM = new ModelRequestVM();
                        modelRequestVM.vehicleBrandID = brandID;
                        RequestBody body = RequestBody.create(gson.toJson(modelRequestVM), mediaType);
                        Request request = new Request.Builder()
                                .url(URL + "/AppVehicleModel/DropList")
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
                                    ResponseModelVM res = gson.fromJson(string_json, ResponseModelVM.class);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.ok) {
                                                x=0;
                                                loading.hide();
                                                lst_model = res.data;

                                                ArrayList<String> arr_model = new ArrayList<>();
                                                for (ModelVM item:lst_model) {
                                                    arr_model.add(item.name);
                                                }

                                                ArrayAdapter<String> arrayAdapterService = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_model);

                                                dropdown_model.setAdapter(arrayAdapterService);
                                            } else {
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
                        Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
                    }
                    break;
                }
            }


        });

        dropdown_model.setOnItemClickListener((adapterView, view, i, l) -> {
            modelName = adapterView.getItemAtPosition(i).toString();
            modelID = 0;
            for (ModelVM item: lst_model) {
                if(modelName.equals(item.name)){
                    modelID = item.id;
                    break;
                }
            }
        });

        dropdown_color.setOnItemClickListener((adapterView, view, i, l) -> {
            colorName = adapterView.getItemAtPosition(i).toString();
            colorID = 0;
            for (ColorVM item: lst_color) {
                if(colorName.equals(item.name)){
                    colorID = item.id;
                    break;
                }
            }
        });

        dropdown_date.setOnItemClickListener((adapterView, view, i, l) -> {
            dateName = adapterView.getItemAtPosition(i).toString();
            dateID = 0;
            dropdown_hour.clearListSelection();
            dropdown_hour.setText("Hora de la cita");
            for (QuoteDatesVM item: lst_dates) {
                if(dateName.equals(item.name)){
                    dateID = item.id;
                    if(!URL.equals("") && !token.equals("")){
                        Show();
                        HoursRequestVM hoursRequestVM = new HoursRequestVM();
                        hoursRequestVM.QuoteDatesID = dateID;
                        RequestBody body = RequestBody.create(gson.toJson(hoursRequestVM), mediaType);
                        Request request = new Request.Builder()
                                .url(URL + "/AppQuoteHour/GetShortByDate")
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
                                    ResponseQuotesHoursVM res = gson.fromJson(string_json, ResponseQuotesHoursVM.class);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.ok) {
                                                x=0;
                                                loading.hide();
                                                lst_hours = res.data;

                                                ArrayList<String> arr_hours = new ArrayList<>();
                                                for (QuoteHoursVM item:lst_hours) {
                                                    arr_hours.add(item.name);
                                                }

                                                ArrayAdapter<String> arrayAdapterService = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_hours);

                                                dropdown_hour.setAdapter(arrayAdapterService);
                                            } else {
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
                        Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
                    }
                    break;
                }
            }


        });

        dropdown_hour.setOnItemClickListener((adapterView, view, i, l) -> {
            hoursName = adapterView.getItemAtPosition(i).toString();
            hoursID = 0;
            for (QuoteHoursVM item: lst_hours) {
                if (hoursName.equals(item.name)) {
                    hoursID = item.id;
                    break;
                }
            }
        });
        validate();
        return view;
    }


    private void validate(){
        if(token.equals("") || token.equals("0000000000")){
            card_view_alert.setVisibility(View.VISIBLE);
            enable(false);
        }else{
            card_view_alert.setVisibility(View.GONE);
            enable(true);
            SearchData();
            Show();
        }
    }

    private void cleanall()
    {
        dropdown_type.clearListSelection();
        dropdown_type.setText("Tipo de Vehículo");
        dropdown_service.clearListSelection();
        dropdown_service.setText("Servicio");
        dropdown_brand.clearListSelection();
        dropdown_brand.setText("Marca del vehículo");
        dropdown_model.clearListSelection();
        dropdown_model.setText("Modelo del vehículo");
        dropdown_color.clearListSelection();
        dropdown_color.setText("Color del vehículo");
        dropdown_date.clearListSelection();
        dropdown_date.setText("Fecha de la cita");
        dropdown_hour.clearListSelection();
        dropdown_hour.setText("Hora de la cita");
        SearchData();
        Show();
    }
    private void enable(Boolean sts){
        dropdown_type_layout.setEnabled(sts);
        dropdown_service_layout.setEnabled(sts);
        dropdown_brand_layout.setEnabled(sts);
        dropdown_model_layout.setEnabled(sts);
        dropdown_color_layout.setEnabled(sts);
        dropdown_hour_layout.setEnabled(sts);
        dropdown_date_layout.setEnabled(sts);
        if(!sts){
            btn_confirm.setVisibility(view.GONE);
        }

    }
    public void Confirm() {
        if(!URL.equals("")){
            QuotesRequestVM quotesRequestVM = new QuotesRequestVM();
            quotesRequestVM.quotesID = 0;
            quotesRequestVM.servicePriceID = serviceID;
            quotesRequestVM.colorID = colorID;
            quotesRequestVM.quotesSTS = "ACTIVO";
            quotesRequestVM.quoteHoursID = hoursID;
            quotesRequestVM.accountID = 0;
            quotesRequestVM.vehicleModelID = modelID;

            RequestBody body = RequestBody.create(gson.toJson(quotesRequestVM), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/AppQuotes/Add")
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    x=0;
                                    loading.hide();
                                    Message("Correcto", res.message);
                                    cleanall();
                                } else {
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
            Message("Error", "Por favor ingresa la url del servidor");
        }
    }

    private void SearchData() {
        if(!URL.equals("") && !token.equals("")){
            //Show();
            RequestBody body = RequestBody.create("", mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/AppQuotes/ListDataQuote")
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
                        AppointmentResponseVM res = gson.fromJson(string_json, AppointmentResponseVM.class);
                        if(getActivity() == null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    x=0;
                                    loading.hide();
                                    lst_dates = res.data.quotes;
                                    lst_color = res.data.color;
                                    lst_brand = res.data.brand;
                                    lst_type = res.data.type;


                                    ArrayList<String> arr_date = new ArrayList<>();
                                    ArrayList<String> arr_type = new ArrayList<>();
                                    ArrayList<String> arr_color = new ArrayList<>();
                                    ArrayList<String> arr_brand = new ArrayList<>();

                                    for (QuoteDatesVM item:lst_dates) {
                                        arr_date.add(item.name);
                                    }
                                    for (TypeVM item:lst_type) {
                                        arr_type.add(item.name);
                                    }

                                    for (ColorVM item:lst_color) {
                                        arr_color.add(item.name);
                                    }

                                    for (BrandVM item:lst_brand) {
                                        arr_brand.add(item.name);
                                    }

                                    ArrayAdapter<String> arrayAdapterDate = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_date);
                                    ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_type);
                                    ArrayAdapter<String> arrayAdapterColor = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_color);
                                    ArrayAdapter<String> arrayAdapterBrand = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_brand);
                                    dropdown_date.setAdapter(arrayAdapterDate);
                                    dropdown_type.setAdapter(arrayAdapterType);
                                    dropdown_color.setAdapter(arrayAdapterColor);
                                    dropdown_brand.setAdapter(arrayAdapterBrand);
                                } else {
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
            Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
        }
    }

    private void Show() {
        x=1;
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