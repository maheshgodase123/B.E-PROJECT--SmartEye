package com.example.imageclassifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<Model> list;
    Context context;

    public MyAdapter(Context context, ArrayList<Model> list)
    {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = list.get(position);
        holder.Driver.setText(model.getDriver());
        holder.NoPlate.setText(model.getNoPlate());
        holder.Status.setText(model.getStatus());
        holder.DateTime.setText(model.getDateTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Driver,NoPlate,Status,DateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Driver = itemView.findViewById(R.id.PersonName);
            NoPlate = itemView.findViewById(R.id.vehicleNo);
            Status = itemView.findViewById(R.id.VehicleStatus);
            DateTime = itemView.findViewById(R.id.DateTime);

        }
    }
}
