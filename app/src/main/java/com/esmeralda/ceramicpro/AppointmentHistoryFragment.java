package com.esmeralda.ceramicpro;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.esmeralda.ceramicpro.model.LastAppointmentRequestVM;
import com.esmeralda.ceramicpro.model.LastAppointmentResponseVM;
import com.esmeralda.ceramicpro.model.LastAppointmentVM;
import com.esmeralda.ceramicpro.ui.account.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class AppointmentHistoryFragment extends Fragment implements AppointmentAdapter.buttonClickListener{

    private int Ok = 0;
    private View view;
    RecyclerView recycleAppointment;
    AppointmentAdapter rvAppointmentAdapter;
    ArrayList<LastAppointmentVM> lastAppointmentArray;
    private MediaType mediaType = MediaType.parse("application/json");
    private SharedPreferences cookies;
    private ImageView back;
    private List<LastAppointmentVM> lst;
    private Dialog loading;
    private Gson gson;
    private OkHttpClient client;
    private String token, URL = "https://ceramicproesmeralda.azurewebsites.net/Api/";
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_appointment_history, container, false);
        client = new OkHttpClient();
        gson = new Gson();
        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");
        back = view.findViewById(R.id.btn_back_home_activity);

        swipeLayout = view.findViewById(R.id.swap);
        swipeLayout.setOnRefreshListener(() -> {
            swipeLayout.setRefreshing(false);
            Clear();
        });
        lastAppointmentArray = new ArrayList<>();
        recycleAppointment = view.findViewById(R.id.RecycleAppointment);
        recycleAppointment.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvAppointmentAdapter = new AppointmentAdapter(view.getContext(), lastAppointmentArray, this);
        recycleAppointment.setAdapter(rvAppointmentAdapter);
        SearchData();

        back.setOnClickListener(view -> {
            Fragment fragment = new AccountFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        return view;
    }



    private void Clear(){
        recycleAppointment.setAdapter(null);
        lst.clear();
        lastAppointmentArray.clear();
        SearchData();
    }

    private void DeleteAppointment(long Id) {
        if(!URL.equals("") && !token.equals("")){
            LastAppointmentRequestVM lastAppointmentRequestVM = new LastAppointmentRequestVM();
            lastAppointmentRequestVM.quotesID = Id;
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
                    .url(URL + "/AppQuotes/Cancel")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try{
                Response response = client.newCall(request).execute();

                if(response.isSuccessful()){
                    Ok = 1;

                    //Message("Información", "La cita ha sido cancelada correctamente.");
                    //Toast.makeText(view.getContext(), "La cita ha sido cancelada correctamente.", Toast.LENGTH_SHORT).show();
                }else{
                    Ok = 0;
                    //Toast.makeText(view.getContext(), "Ocurrio un error.", Toast.LENGTH_SHORT).show();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }else{
            Ok = 0;
        }
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
                    .url(URL + "/AppQuotes/List")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    loading.hide();
                                    lst = res.data;

                                    for (LastAppointmentVM item:lst) {
                                        LastAppointmentVM model = new LastAppointmentVM();
                                        model.setQuotesID(item.quotesID);
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
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(view.getContext(), lastAppointmentArray, this);
        recycleAppointment.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recycleAppointment.setAdapter(appointmentAdapter);
    }

    private void Show() {
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



    @Override
    public void onButtonClick(int position) {

        for (int i=0;i<lastAppointmentArray.size(); i++){
            if(i == position){
                long Id = lastAppointmentArray.get(position).getQuotesID();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DeleteAppointment(Id);
                        //Clear();
                    }
                }).start();


                //Toast.makeText(view.getContext(), "Position: " + position + " y su Id es: " + Id, Toast.LENGTH_SHORT).show();
            }
        }

        if(Ok == 0){
            Clear();
            Message("Información", "Cita cancelada con exito");
        }else{
            Clear();
            Message("Información", "Cita cancelada con exito");
        }

    }
}