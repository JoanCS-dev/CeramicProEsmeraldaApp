package com.esmeralda.ceramicpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esmeralda.ceramicpro.model.SubscriptionVM;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {

    private Context context;
    private static List<SubscriptionVM> data;

    public SubscriptionAdapter(Context context, List<SubscriptionVM> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.subscriptions_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SubscriptionVM subscriptionVM = data.get(position);
        holder.title_pack.setText(data.get(position).getNombre());
        holder.title_garantia.setText(data.get(position).getGarantia());
        holder.title_description.setText(data.get(position).getDescripcion());
        holder.title_incluye.setText(data.get(position).getIncluye());
        holder.title_elements.setText(data.get(position).getElements());
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title_pack, title_garantia, title_description, title_incluye, title_elements;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_pack = itemView.findViewById(R.id.textPack);
            title_garantia = itemView.findViewById(R.id.textGarantia);
            title_description = itemView.findViewById(R.id.txt_description);
            title_incluye = itemView.findViewById(R.id.txt_incluye);
            title_elements = itemView.findViewById(R.id.txt_elements);
            //itemView.setOnClickListener(this);
        }

    }



}
