package com.esmeralda.ceramicpro.ui.appointment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.model.BrandVM;
import com.esmeralda.ceramicpro.model.ColorVM;
import com.esmeralda.ceramicpro.model.ModelVM;
import com.esmeralda.ceramicpro.model.QuoteDatesResponseVM;
import com.esmeralda.ceramicpro.model.QuoteDatesVM;
import com.esmeralda.ceramicpro.model.QuoteHoursVM;
import com.esmeralda.ceramicpro.model.QuotesRequestVM;
import com.esmeralda.ceramicpro.model.ResponseVM;
import com.esmeralda.ceramicpro.model.ServiceVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
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
    private TextInputLayout dropdown_service_layout, dropdown_brand_layout, dropdown_model_layout, dropdown_color_layout, edit_date_layout, dropdown_hour_layout;
    private AutoCompleteTextView dropdown_service, dropdown_brand, dropdown_model, dropdown_color, dropdown_hour;
    private TextInputEditText edit_date;
    private Button btn_confirm;
    private String serviceName, colorName, token, URL = "https://ceramicproesmeralda.azurewebsites.net";
    private List<ServiceVM> lst_service;
    private List<BrandVM> lst_brand;
    private List<ModelVM> lst_model;
    private List<ColorVM> lst_color;
    private long brandID, modelID, quoteHoursID;
    private int dia, mes, ano;
    private SharedPreferences cookies;
    private OkHttpClient client;
    private Gson gson;
    private List<QuoteDatesVM> lst_dates;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);

        card_view_alert = view.findViewById(R.id.card_view_alert);

        dropdown_service_layout = view.findViewById(R.id.dropdown_service_layout);
        dropdown_brand_layout = view.findViewById(R.id.dropdown_brand_layout);
        dropdown_model_layout = view.findViewById(R.id.dropdown_model_layout);
        dropdown_color_layout = view.findViewById(R.id.dropdown_color_layout);
        dropdown_hour_layout = view.findViewById(R.id.dropdown_hour_layout);
        edit_date_layout = view.findViewById(R.id.edit_date_layout);

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

        InitLst();
        btn_confirm.setOnClickListener(view -> {
            Show();
            Confirm();
        });
        dropdown_service.setOnItemClickListener((adapterView, view, i, l) -> serviceName = adapterView.getItemAtPosition(i).toString());

        dropdown_brand.setOnItemClickListener((adapterView, view, i, l) -> {
            String data = adapterView.getItemAtPosition(i).toString();
            brandID = 0;
            for (BrandVM item: lst_brand) {
                if(data.equals(item.Name)){
                    brandID = item.Id;
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

        dropdown_hour.setOnItemClickListener((adapterView, view, i, l) -> {
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
        });

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
                        if(item.quoteDates.equals(date)){
                            for (QuoteHoursVM hour : item.quoteHours){
                                arr.add(hour.quoteHour);
                            }
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
        lst_service = new ArrayList<>();
        lst_brand = new ArrayList<>();
        lst_model = new ArrayList<>();
        lst_color = new ArrayList<>();

        lst_service.add(new ServiceVM("PELÍCULAS DE PROTECCIÓN DE PINTURA"));
        lst_service.add(new ServiceVM("VIDRIOS POLARIZADOS"));

        lst_brand.add(new BrandVM(6, "Audi"));
        lst_brand.add(new BrandVM(7, "Bentley"));
        lst_brand.add(new BrandVM(8, "BMW"));
        lst_brand.add(new BrandVM(9, "Bugatti"));
        lst_brand.add(new BrandVM(19, "Ferrari"));
        lst_brand.add(new BrandVM(34, "Lamborghini"));
        lst_brand.add(new BrandVM(42, "Maserati"));
        lst_brand.add(new BrandVM(44, "McLaren"));
        lst_brand.add(new BrandVM(45, "Mercedes"));
        lst_brand.add(new BrandVM(48, "Mini"));
        lst_brand.add(new BrandVM(50, "Mustang"));
        lst_brand.add(new BrandVM(58, "Porsche"));
        lst_brand.add(new BrandVM(62, "Rolls-Royce"));
        lst_brand.add(new BrandVM(71, "Tesla"));

        //lst_model.add(new ModelVM(0, "", 0));

        lst_model.add(new ModelVM(17,"A1",	6));
        lst_model.add(new ModelVM(18,"A3",	6));
        lst_model.add(new ModelVM(19,"A4",	6));
        lst_model.add(new ModelVM(20,"A5",	6));
        lst_model.add(new ModelVM(21,"A6",	6));
        lst_model.add(new ModelVM(22,"A7",	6));
        lst_model.add(new ModelVM(23,"A8",	6));
        lst_model.add(new ModelVM(24,"e-tron",	6));
        lst_model.add(new ModelVM(25,"e-tron GT",	6));
        lst_model.add(new ModelVM(26,"e-tron Sportback",	6));
        lst_model.add(new ModelVM(27,"Q2",	6));
        lst_model.add(new ModelVM(28,"Q3",	6));
        lst_model.add(new ModelVM(29,"Q4",	6));
        lst_model.add(new ModelVM(30,"Q5",	6));
        lst_model.add(new ModelVM(31,"Q7",	6));
        lst_model.add(new ModelVM(32,"Q8",	6));
        lst_model.add(new ModelVM(33,"Q8 e-tron",	6));
        lst_model.add(new ModelVM(34,"R8",	6));
        lst_model.add(new ModelVM(35,"TT",	6));
        lst_model.add(new ModelVM(36,"Bentayga",	7));
        lst_model.add(new ModelVM(37,"Continental GT",	7));
        lst_model.add(new ModelVM(38,"Flying Spur",	7));
        lst_model.add(new ModelVM(39,"Flying Spur Speed",	7));
        lst_model.add(new ModelVM(40,"i3",	8));
        lst_model.add(new ModelVM(41,"i5",	8));
        lst_model.add(new ModelVM(42,"i7",	8));
        lst_model.add(new ModelVM(43,"iX",	8));
        lst_model.add(new ModelVM(44,"ix1",	8));
        lst_model.add(new ModelVM(45,"iX3",	8));
        lst_model.add(new ModelVM(46,"Serie 1",	8));
        lst_model.add(new ModelVM(47,"Serie 2",	8));
        lst_model.add(new ModelVM(48,"Serie 3",	8));
        lst_model.add(new ModelVM(49,"Serie 4",	8));
        lst_model.add(new ModelVM(50,"Serie 5",	8));
        lst_model.add(new ModelVM(51,"Serie 6",	8));
        lst_model.add(new ModelVM(52,"Serie 7",	8));
        lst_model.add(new ModelVM(53,"Serie 8",	8));
        lst_model.add(new ModelVM(54,"X1",	8));
        lst_model.add(new ModelVM(55,"X2",	8));
        lst_model.add(new ModelVM(56,"X3",	8));
        lst_model.add(new ModelVM(57,"X4",	8));
        lst_model.add(new ModelVM(58,"X5",	8));
        lst_model.add(new ModelVM(59,"X6",	8));
        lst_model.add(new ModelVM(60,"X7",	8));
        lst_model.add(new ModelVM(61,"XM",	8));
        lst_model.add(new ModelVM(62,"Z4",	8));
        lst_model.add(new ModelVM(63,"Centodieci",	9));
        lst_model.add(new ModelVM(64,"Chiron",	9));
        lst_model.add(new ModelVM(65,"Divo",	9));
        lst_model.add(new ModelVM(105,"296 GTB	",19));
        lst_model.add(new ModelVM(106,"296 GTS	",19));
        lst_model.add(new ModelVM(107,"458	",19));
        lst_model.add(new ModelVM(108,"488 GTB	",19));
        lst_model.add(new ModelVM(109,"812 GTS	",19));
        lst_model.add(new ModelVM(110,"812 Superfast	",19));
        lst_model.add(new ModelVM(111,"California T	",19));
        lst_model.add(new ModelVM(112,"F12 Berlinetta	",19));
        lst_model.add(new ModelVM(113,"F8 Spider	",19));
        lst_model.add(new ModelVM(114,"F8 Tributo	",19));
        lst_model.add(new ModelVM(115,"GTC4 Lusso	",19));
        lst_model.add(new ModelVM(116,"LaFerrari	",19));
        lst_model.add(new ModelVM(117,"Purosangue	",19));
        lst_model.add(new ModelVM(118,"Roma	",19));
        lst_model.add(new ModelVM(119,"SF90 Spider	",19));
        lst_model.add(new ModelVM(120,"SF90 Stradale	",19));
        lst_model.add(new ModelVM(201,"Aventador	",34));
        lst_model.add(new ModelVM(202,"Countach	",34));
        lst_model.add(new ModelVM(203,"Hurac?n	",34));
        lst_model.add(new ModelVM(204,"Revuelto	",34));
        lst_model.add(new ModelVM(205,"Urus	",34));
        lst_model.add(new ModelVM(231,"Ghibli	",42));
        lst_model.add(new ModelVM(232,"Grecale	",42));
        lst_model.add(new ModelVM(233,"Levante	",42));
        lst_model.add(new ModelVM(234,"MC20	",42));
        lst_model.add(new ModelVM(235,"Quattroporte	",42));
        lst_model.add(new ModelVM(246,"540C / 570S	",44));
        lst_model.add(new ModelVM(247,"600LT	",44));
        lst_model.add(new ModelVM(248,"650S / 675LT	",44));
        lst_model.add(new ModelVM(249,"720S	",44));
        lst_model.add(new ModelVM(250,"765LT	",44));
        lst_model.add(new ModelVM(251,"Artura	",44));
        lst_model.add(new ModelVM(252,"Elva	",44));
        lst_model.add(new ModelVM(253,"GT	",44));
        lst_model.add(new ModelVM(254,"P1	",44));
        lst_model.add(new ModelVM(255,"Senna	",44));
        lst_model.add(new ModelVM(256,"Speedtail	",44));
        lst_model.add(new ModelVM(257,"AMG GT	",45));
        lst_model.add(new ModelVM(258,"AMG One	",45));
        lst_model.add(new ModelVM(259,"CLA	",45));
        lst_model.add(new ModelVM(260,"Clase A	",45));
        lst_model.add(new ModelVM(261,"Clase B	",45));
        lst_model.add(new ModelVM(262,"Clase C	",45));
        lst_model.add(new ModelVM(263,"Clase E	",45));
        lst_model.add(new ModelVM(264,"Clase G	",45));
        lst_model.add(new ModelVM(265,"Clase S	",45));
        lst_model.add(new ModelVM(266,"Clase T	",45));
        lst_model.add(new ModelVM(267,"Clase V	",45));
        lst_model.add(new ModelVM(268,"CLS	",45));
        lst_model.add(new ModelVM(269,"EQA	",45));
        lst_model.add(new ModelVM(270,"EQB	",45));
        lst_model.add(new ModelVM(271,"EQC	",45));
        lst_model.add(new ModelVM(272,"EQE	",45));
        lst_model.add(new ModelVM(273,"EQE SUV	",45));
        lst_model.add(new ModelVM(274,"EQS SUV	",45));
        lst_model.add(new ModelVM(275,"EQT	",45));
        lst_model.add(new ModelVM(276,"EQV	",45));
        lst_model.add(new ModelVM(277,"GLA	",45));
        lst_model.add(new ModelVM(278,"GLB	",45));
        lst_model.add(new ModelVM(279,"GLC	",45));
        lst_model.add(new ModelVM(280,"GLE	",45));
        lst_model.add(new ModelVM(281,"GLS	",45));
        lst_model.add(new ModelVM(282,"Marco Polo	",45));
        lst_model.add(new ModelVM(283,"Maybach Clase S 	",45));
        lst_model.add(new ModelVM(284,"Maybach GLS	",45));
        lst_model.add(new ModelVM(285,"Vito	",45));
        lst_model.add(new ModelVM(294,"3 puertas	",48));
        lst_model.add(new ModelVM(295,"5 Puertas	",48));
        lst_model.add(new ModelVM(296,"Cabrio	",48));
        lst_model.add(new ModelVM(297,"Clubman	",48));
        lst_model.add(new ModelVM(298,"Countryman	",48));
        lst_model.add(new ModelVM(305,"Mach-E	",50));

        lst_color.add(new ColorVM("AMARILLO"));
        lst_color.add(new ColorVM("AZUL"));
        lst_color.add(new ColorVM("BLANCO"));
        lst_color.add(new ColorVM("GRIS"));
        lst_color.add(new ColorVM("NEGRO"));
        lst_color.add(new ColorVM("ROJO"));

        ArrayList<String> arr_service = new ArrayList<>();
        ArrayList<String> arr_color = new ArrayList<>();
        ArrayList<String> arr_brand = new ArrayList<>();
        for (ServiceVM item:lst_service) {
            arr_service.add(item.Service);
        }

        for (ColorVM item:lst_color) {
            arr_color.add(item.Color);
        }

        for (BrandVM item:lst_brand) {
            arr_brand.add(item.Name);
        }

        ArrayAdapter<String> arrayAdapterService = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_service);
        ArrayAdapter<String> arrayAdapterColor = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_color);
        ArrayAdapter<String> arrayAdapterBrand = new ArrayAdapter<>(view.getContext(), R.layout.design_drop_down_item, arr_brand);
        dropdown_service.setAdapter(arrayAdapterService);
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