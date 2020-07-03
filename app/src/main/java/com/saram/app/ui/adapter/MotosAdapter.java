package com.saram.app.ui.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.saram.app.R;

public class MotosAdapter extends RecyclerView.Adapter<MotosAdapter.ViewHolder> {
    private String[] Modelos;
    private String[] Marcas;
    private String[] Placas;
    private String[] SARAM;
    private OnItemClickListener mListener;
    private int[] IDs;
    public  interface OnItemClickListener{
        void onEditClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener Listener){
        mListener=Listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
       public TextView Modelo;
       public TextView Marca;
       public TextView Placa;
       public TextView ID_SARAM;
       public ImageView btn_Edit;
       public ViewHolder(View moto , final OnItemClickListener Listener){
            super(moto);
            Modelo = moto.findViewById(R.id.tvModelo);
            Marca = moto.findViewById(R.id.tvMarca);
            Placa = moto.findViewById(R.id.tvPlaca);
            ID_SARAM = moto.findViewById(R.id.tvSaram);
            btn_Edit = moto.findViewById(R.id.imgEditar);
            btn_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listener!=null){
                        int position =getBindingAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            Listener.onEditClick(position);
                        }
                    }
                }
            });
        }

    }

    public MotosAdapter(String[] Modelos, String[] Marcas, String[] Placas, String[] SARAM, int[] IDs, Application app){
        this.Modelos=Modelos;
        this.Marcas=Marcas;
        this.Placas=Placas;
        this.SARAM=SARAM;
        this.IDs=IDs;
    }
    public void setData(String[] Modelos, String[] Marcas, String[] Placas, String[] SARAM, int[] IDs){
        this.Modelos=Modelos;
        this.Marcas=Marcas;
        this.Placas=Placas;
        this.SARAM=SARAM;
        this.IDs=IDs;
        notifyDataSetChanged();
    }

    @Override
    public MotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //Creacón de nueva vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_info_moto, parent, false);


        return  new ViewHolder(v, mListener);
    }

    @Override
    public  void  onBindViewHolder(final ViewHolder holder, final int position){
        holder.Modelo.setText("Modelo: "+Modelos[position].toString());
        holder.Marca.setText("Marca: "+Marcas[position].toString());
        holder.Placa.setText("Placa: "+Placas[position].toString());
        holder.ID_SARAM.setText("ID SARAM: "+SARAM[position].toString());
        holder.btn_Edit.setId(IDs[position]);
    }

    @Override
    public int getItemCount(){
        return IDs.length;
    }

}
