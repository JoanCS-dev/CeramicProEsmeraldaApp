package com.esmeralda.ceramicpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esmeralda.ceramicpro.model.LastAppointmentVM;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    private Context context;
    private List<LastAppointmentVM> data;

    public AppointmentAdapter(Context context, List<LastAppointmentVM> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.appointment_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title_date.setText(data.get(position).getQuotesDate());
        holder.title_time.setText(data.get(position).getQuotesHour());
        holder.title_service.setText(data.get(position).getServiceDesc());
        holder.title_brand.setText(data.get(position).getVehicleBrandName());
        holder.title_model.setText(data.get(position).getVehicleModelName());
        holder.title_color.setText(data.get(position).getColorName());
        holder.title_sts.setText(data.get(position).getQuotesSTS());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title_date, title_time, title_service, title_brand, title_model, title_color, title_sts;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_date = itemView.findViewById(R.id.title_date_appointment);
            title_time = itemView.findViewById(R.id.title_time_appointment);
            title_service = itemView.findViewById(R.id.msg_service);
            title_brand = itemView.findViewById(R.id.msg_brand);
            title_model = itemView.findViewById(R.id.msg_model);
            title_color = itemView.findViewById(R.id.msg_color);
            title_sts = itemView.findViewById(R.id.msg_validation);
        }



    }
}
