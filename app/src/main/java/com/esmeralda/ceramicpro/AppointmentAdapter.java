package com.esmeralda.ceramicpro;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esmeralda.ceramicpro.model.LastAppointmentVM;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    private Context context;
    private static List<LastAppointmentVM> data;
    buttonClickListener buttonClickListener;

    public AppointmentAdapter(Context context, List<LastAppointmentVM> data, buttonClickListener buttonClickListener){
        this.context = context;
        this.data = data;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.appointment_item, parent, false);

        return new MyViewHolder(view, buttonClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LastAppointmentVM appointmentVM = data.get(position);
        if(position == 0){
            if (holder.color_theme.getText().equals("White")){
                holder.ly_background.setBackgroundColor(0xFFE5E5E5);
            }else{
                holder.ly_background.setBackgroundColor(0xFF525252);
            }
            holder.lastAppointment.setBackgroundColor(0xFF3A3A3A);
        }else{
            if (holder.color_theme.getText().equals("White")){
                holder.ly_background.setBackgroundColor(0xFFFFFFFF);
            }else{
                holder.ly_background.setBackgroundColor(0xFF121212);
            }
            holder.lastAppointment.setBackgroundColor(0xFF6D6D6D);
        }
        holder.title_date.setText(data.get(position).getQuotesDate());
        holder.title_time.setText(data.get(position).getQuotesHour());
        holder.title_service.setText(data.get(position).getServiceDesc());
        holder.title_brand.setText(data.get(position).getVehicleBrandName());
        holder.title_model.setText(data.get(position).getVehicleModelName());
        holder.title_color.setText(data.get(position).getColorName());
        holder.title_sts.setText(data.get(position).getQuotesSTS());
        if(data.get(position).getQuotesSTS().equals("PENDIENTE")){
            holder.date_Cancel.setVisibility(View.VISIBLE);
        }else if(data.get(position).getQuotesSTS().equals("CANCELADA")){
            holder.title_sts.setTextColor(0xFFD30E00);
            holder.date_Cancel.setVisibility(View.GONE);
        }
        holder.txv_id.setText("" + data.get(position).getQuotesID());
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button date_Cancel;
        TextView title_date, title_time, title_service, title_brand, title_model, title_color, title_sts, color_theme, txv_id;
        LinearLayout lastAppointment, ly_background;
        buttonClickListener buttonClickListener;

        public MyViewHolder(@NonNull View itemView, buttonClickListener buttonClickListener) {
            super(itemView);
            lastAppointment = itemView.findViewById(R.id.LastAppointment);
            ly_background = itemView.findViewById(R.id.LY_Background);
            color_theme = itemView.findViewById(R.id.Color_Theme);
            title_date = itemView.findViewById(R.id.title_date_appointment);
            title_time = itemView.findViewById(R.id.title_time_appointment);
            title_service = itemView.findViewById(R.id.msg_service);
            title_brand = itemView.findViewById(R.id.msg_brand);
            title_model = itemView.findViewById(R.id.msg_model);
            title_color = itemView.findViewById(R.id.msg_color);
            title_sts = itemView.findViewById(R.id.msg_validation);
            txv_id = itemView.findViewById(R.id.id);
            date_Cancel = itemView.findViewById(R.id.Date_Cancel);


            this.buttonClickListener = buttonClickListener;
            date_Cancel.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            buttonClickListener.onButtonClick(getBindingAdapterPosition());
        }
    }

    public interface buttonClickListener{
        void onButtonClick(int position);
    }


}
