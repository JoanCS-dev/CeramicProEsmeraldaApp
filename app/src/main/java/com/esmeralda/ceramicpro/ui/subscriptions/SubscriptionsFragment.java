package com.esmeralda.ceramicpro.ui.subscriptions;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esmeralda.ceramicpro.AppointmentAdapter;
import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.SubscriptionAdapter;
import com.esmeralda.ceramicpro.model.LastAppointmentRequestVM;
import com.esmeralda.ceramicpro.model.LastAppointmentResponseVM;
import com.esmeralda.ceramicpro.model.LastAppointmentVM;
import com.esmeralda.ceramicpro.model.SubscriptionVM;
import com.esmeralda.ceramicpro.model.SubscriptionsResponseVM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class SubscriptionsFragment extends Fragment{
    private View view;
    private Dialog loading;
    private Gson gson;
    private OkHttpClient client;
    private String token, URL = "https://ceramicproesmeraldaapi.azurewebsites.net/Api/";
    private SwipeRefreshLayout swipeLayout;
    private SharedPreferences cookies;
    private MediaType mediaType = MediaType.parse("application/json");

    RecyclerView recycleSubscriptions;
    SubscriptionAdapter rvSubscriptionsAdapter;
    ArrayList<SubscriptionVM> subscriptionsArray;
    private List<SubscriptionVM> lst;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        gson = new Gson();
        cookies = view.getContext().getSharedPreferences("SHA_CST_DB", Context.MODE_PRIVATE);
        token = cookies.getString("strToken", "");


        swipeLayout = view.findViewById(R.id.swap);
        swipeLayout.setOnRefreshListener(() -> {
            swipeLayout.setRefreshing(false);
            Clear();
        });

        subscriptionsArray = new ArrayList<>();
        recycleSubscriptions = view.findViewById(R.id.RecycleSubscription);
        recycleSubscriptions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvSubscriptionsAdapter = new SubscriptionAdapter(view.getContext(), subscriptionsArray);
        recycleSubscriptions.setAdapter(rvSubscriptionsAdapter);
        SearchData();


        return view;
    }

    private void SearchData() {
        if(!URL.equals("") && !token.equals("")){
            Show();
            RequestBody body = RequestBody.create("", mediaType);
            Request request = new Request.Builder()
                    .url(URL + "AppPackages/Lst")
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
                            loading.hide();
                            Message("Respuesta fallida!", "Ocurrió un error en el servidor. Verifica tu conexión a internet o por favor contactarse con Sistemas.");
                        }
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String string_json = response.body().string();
                    if(response.isSuccessful()){
                        SubscriptionsResponseVM res = gson.fromJson(string_json, SubscriptionsResponseVM.class);
                        if(getActivity() == null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.ok) {
                                    loading.hide();
                                    lst = res.data;

                                    for (SubscriptionVM item:lst) {
                                        String elementos = "";
                                        SubscriptionVM model = new SubscriptionVM();
                                        model.setNombre(item.nombre);
                                        model.setGarantia(item.garantia);
                                        model.setDescripcion(item.descripcion);
                                        model.setIncluye(item.incluye);
                                        //model.setElementos(item.elementos);
                                        for (int i = 0; i < item.elementos.size(); i++){
                                            if(i + 1 < item.elementos.size()) {
                                                elementos += item.elementos.get(i) + " \n ";
                                            } else{
                                                elementos += item.elementos.get(i);
                                            }
                                        }
                                        model.setElements(elementos);

                                        subscriptionsArray.add(model);

                                    }

                                    PutDataIntoRecyclerView(subscriptionsArray);

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

    private void PutDataIntoRecyclerView(List<SubscriptionVM> subscriptionsArray){
        SubscriptionAdapter subscriptionAdapter = new SubscriptionAdapter(view.getContext(), subscriptionsArray);
        recycleSubscriptions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recycleSubscriptions.setAdapter(subscriptionAdapter);
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
    private void Clear(){
        recycleSubscriptions.setAdapter(null);
        lst.clear();
        subscriptionsArray.clear();
        SearchData();
    }
}