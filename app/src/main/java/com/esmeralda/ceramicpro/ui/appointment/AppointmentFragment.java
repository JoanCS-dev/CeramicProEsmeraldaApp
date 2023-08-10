package com.esmeralda.ceramicpro.ui.appointment;

import android.app.DatePickerDialog;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.model.AppointmentResponseVM;
import com.esmeralda.ceramicpro.model.BrandVM;
import com.esmeralda.ceramicpro.model.ColorVM;
import com.esmeralda.ceramicpro.model.ModelVM;
import com.esmeralda.ceramicpro.model.QuoteDatesResponseVM;
import com.esmeralda.ceramicpro.model.QuoteDatesVM;
import com.esmeralda.ceramicpro.model.QuotesRequestVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.esmeralda.ceramicpro.model.ServiceVM;
import com.esmeralda.ceramicpro.model.TypeVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private Dialog loading;
    private CardView card_view_alert;
    private TextInputLayout dropdown_type_layout, dropdown_service_layout, dropdown_brand_layout, dropdown_model_layout, dropdown_color_layout, edit_date_layout, dropdown_hour_layout;
    private AutoCompleteTextView dropdown_type, dropdown_service, dropdown_brand, dropdown_model, dropdown_color, dropdown_hour;
    private TextInputEditText edit_date;
    private Button btn_confirm;
    private String typeName, serviceName, colorName, token, URL = "https://ceramicproesmeralda.azurewebsites.net";
    private List<ServiceVM> lst_service;
    private List<BrandVM> lst_brand;
    private List<ModelVM> lst_model;
    private List<ColorVM> lst_color;
    private List<QuoteDatesVM> lst_dates;
    private List<TypeVM> lst_type;
    private long brandID, modelID, quoteHoursID;
    private int dia, mes, ano;
    private SharedPreferences cookies;
    private OkHttpClient client;
    private Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);

        card_view_alert = view.findViewById(R.id.card_view_alert);

        dropdown_type_layout = view.findViewById(R.id.dropdown_type_layout);
        dropdown_service_layout = view.findViewById(R.id.dropdown_service_layout);
        dropdown_brand_layout = view.findViewById(R.id.dropdown_brand_layout);
        dropdown_model_layout = view.findViewById(R.id.dropdown_model_layout);
        dropdown_color_layout = view.findViewById(R.id.dropdown_color_layout);
        dropdown_hour_layout = view.findViewById(R.id.dropdown_hour_layout);
        edit_date_layout = view.findViewById(R.id.edit_date_layout);

        dropdown_type = view.findViewById(R.id.dropdown_type);
        dropdown_service = view.findViewById(R.id.dropdown_service);
        dropdown_brand = view.findViewById(R.id.dropdown_brand);
        dropdown_model = view.findViewById(R.id.dropdown_model);
        dropdown_color = view.findViewById(R.id.dropdown_color);
        dropdown_hour = view.findViewById(R.id.dropdown_hour);
        edit_date = view.findViewById(R.id.edit_date);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        client = new OkHttpClient();

        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");


        gson = new Gson();
        SearchData();
        //InitLst();
        btn_confirm.setOnClickListener(view -> {
            Show();
            Confirm();
        });

        dropdown_type.setOnItemClickListener((adapterView, view, i, l) -> typeName = adapterView.getItemAtPosition(i).toString());

        dropdown_service.setOnItemClickListener((adapterView, view, i, l) -> serviceName = adapterView.getItemAtPosition(i).toString());

        dropdown_brand.setOnItemClickListener((adapterView, view, i, l) -> {
            String data = adapterView.getItemAtPosition(i).toString();
            brandID = 0;
            for (BrandVM item: lst_brand) {
                if(data.equals(item.name)){
                    brandID = item.id;
                    break;
                }
            }

            ArrayList<String> arr_model = new ArrayList<>();
            for (ModelVM item:lst_model) {
                if(item.Brand == brandID){
                    arr_model.add(item.Name);
                }
            }
            ArrayAdapter<String> arrayAdapterModel = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_model);
            dropdown_model.setAdapter(arrayAdapterModel);
        });

        dropdown_model.setOnItemClickListener((adapterView, view, i, l) -> {
            String data = adapterView.getItemAtPosition(i).toString();
            modelID = 0;
            for (ModelVM item: lst_model) {
                if(data.equals(item.Name)){
                    modelID = item.Id;
                    break;
                }
            }
        });

        dropdown_color.setOnItemClickListener((adapterView, view, i, l) -> colorName = adapterView.getItemAtPosition(i).toString());

        /*dropdown_hour.setOnItemClickListener((adapterView, view, i, l) -> {
            String hour = adapterView.getItemAtPosition(i).toString();
            for (QuoteDatesVM date_item: lst_dates) {
                if(date_item.quoteDates.equals(edit_date.getText().toString())){
                    for (QuoteHoursVM hour_item: date_item.quoteHours) {
                        if(hour.equals(hour_item.quoteHour)){
                            quoteHoursID = hour_item.quoteHoursID;
                            break;
                        }
                    }
                    break;
                }
            }
        });*/

        edit_date.setOnClickListener(view -> {
            if(!token.equals("") || !token.equals("0000000000")){

                Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                ano = c.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), (datePicker, i, i1, i2) -> {
                    String date = AddC(i) +"-"+AddC(i1+1)+"-"+AddC(i2);
                    edit_date.setText(date);

                    ArrayList<String> arr = new ArrayList<>();
                    for (QuoteDatesVM item : lst_dates) {
                        if(item.name.equals(date)){
                            //for (QuoteHoursVM hour : item.quoteHours){
                            //    arr.add(hour.quoteHour);
                            //}
                            break;
                        }
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr);
                    dropdown_hour.setAdapter(arrayAdapter);

                }, ano, mes, dia);

                dialog.show();
            }
        });

        if(token.equals("") || token.equals("0000000000")){
            card_view_alert.setVisibility(View.VISIBLE);
            enable(false);
        }else{
            card_view_alert.setVisibility(View.GONE);
            enable(true);
            SearchDates();
        }

        return view;
    }
    private void enable(Boolean sts){
        dropdown_type_layout.setEnabled(sts);
        dropdown_service_layout.setEnabled(sts);
        dropdown_brand_layout.setEnabled(sts);
        dropdown_model_layout.setEnabled(sts);
        dropdown_color_layout.setEnabled(sts);
        dropdown_hour_layout.setEnabled(sts);
        edit_date_layout.setEnabled(sts);
        btn_confirm.setEnabled(sts);
    }
    public void Confirm() {
        if(!URL.equals("")){
            QuotesRequestVM quotesRequestVM = new QuotesRequestVM();
            quotesRequestVM.quotesID = 0;
            quotesRequestVM.quoteHoursID = quoteHoursID;
            quotesRequestVM.quotesService = serviceName;
            quotesRequestVM.quotesColor = colorName;
            quotesRequestVM.quotesSTS = "ACTIVO";
            quotesRequestVM.accountID = 0;
            quotesRequestVM.vehicleModelID = modelID;

            RequestBody body = RequestBody.create(gson.toJson(quotesRequestVM), mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/Api/Quotes/Add")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    loading.hide();
                    Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    loading.hide();
                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        ResponseVM res = gson.fromJson(string_json, ResponseVM.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    Message("Correcto", res.message);
                                } else {
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

    private void SearchData() {
        if(!URL.equals("") && !token.equals("")){
            //Show();
            RequestBody body = RequestBody.create("", mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/Api/Quotes/ListDataQuote")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //loading.hide();
                    Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        AppointmentResponseVM res = gson.fromJson(string_json, AppointmentResponseVM.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    //loading.hide();
                                    lst_dates = res.data.quotes;
                                    lst_color = res.data.color;
                                    lst_brand = res.data.brand;
                                    lst_type = res.data.type;




                                    ArrayList<String> arr_type = new ArrayList<>();
                                    ArrayList<String> arr_color = new ArrayList<>();
                                    ArrayList<String> arr_brand = new ArrayList<>();
                                    for (TypeVM item:lst_type) {
                                        arr_type.add(item.name);
                                    }

                                    for (ColorVM item:lst_color) {
                                        arr_color.add(item.name);
                                    }

                                    for (BrandVM item:lst_brand) {
                                        arr_brand.add(item.name);
                                    }

                                    ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_type);
                                    ArrayAdapter<String> arrayAdapterColor = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_color);
                                    ArrayAdapter<String> arrayAdapterBrand = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_brand);
                                    dropdown_type.setAdapter(arrayAdapterType);
                                    dropdown_color.setAdapter(arrayAdapterColor);
                                    dropdown_brand.setAdapter(arrayAdapterBrand);
                                } else {
                                    //loading.hide();
                                    Message("Información", res.message);
                                }
                            }
                        });
                    }else{
                        //loading.hide();
                        Message("Error", response.message() + " - " + response.code());
                    }
                }
            });

        }else{
            //loading.hide();
            Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
        }
    }
    private void SearchDates() {
        if(!URL.equals("") && !token.equals("")){
            Show();
            RequestBody body = RequestBody.create("", mediaType);
            Request request = new Request.Builder()
                    .url(URL + "/Api/Quotes/ListQuotes")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    loading.hide();
                    Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        QuoteDatesResponseVM res = gson.fromJson(string_json, QuoteDatesResponseVM.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    loading.hide();
                                    lst_dates = res.data;
                                } else {
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
            Message("Error", "Por favor ingresa la url del servidor e inicia sessión");
        }
    }
    private String AddC(int No){
        String n = "";
        if(No < 10){
            n = "0" + No;
        }else{
            n = "" + No;
        }
        return n;
    }
    private void InitLst(){
        lst_type = new ArrayList<>();
        lst_brand = new ArrayList<>();
        lst_model = new ArrayList<>();
        lst_color = new ArrayList<>();


        ArrayList<String> arr_type = new ArrayList<>();
        ArrayList<String> arr_color = new ArrayList<>();
        ArrayList<String> arr_brand = new ArrayList<>();
        for (TypeVM item:lst_type) {
            arr_type.add(item.name);
        }

        for (ColorVM item:lst_color) {
            arr_color.add(item.name);
        }

        for (BrandVM item:lst_brand) {
            arr_brand.add(item.name);
        }

        ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_type);
        ArrayAdapter<String> arrayAdapterColor = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_color);
        ArrayAdapter<String> arrayAdapterBrand = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_brand);
        dropdown_type.setAdapter(arrayAdapterType);
        dropdown_color.setAdapter(arrayAdapterColor);
        dropdown_brand.setAdapter(arrayAdapterBrand);
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