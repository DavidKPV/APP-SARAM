package com.saram.app.ui.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saram.app.R;

public class contactos_saramAdapter extends RecyclerView.Adapter<contactos_saramAdapter.ViewHolder> {
    private String[] Nombre;
    private String[] Numero;
    private OnItemClickListener mListener;
    private int[] IDs;
    public  interface OnItemClickListener{
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener Listener){
        mListener=Listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
       public TextView Nombre;
       public TextView Numero;
       public ImageView btn_Edit, btn_delete;

       public ViewHolder(View contactos , final OnItemClickListener Listener){
            super(contactos);
            Nombre = contactos.findViewById(R.id.tvNombreContacto);
            Numero = contactos.findViewById(R.id.tvNumeroContacto);
            //btn_Edit = contactos.findViewById(R.id.imgEditar);
            btn_delete = contactos.findViewById(R.id.imgEliminar);

            /*
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
             */

            // BOTÓN PARA ELIMINAR REGISTRO DE LA MOTOCICLETA
           btn_delete.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(Listener!=null){
                       int position = getBindingAdapterPosition();
                       if(position != RecyclerView.NO_POSITION){
                           Listener.onDeleteClick(position);
                       }
                   }
               }
           });
        }

    }

    public contactos_saramAdapter(String[] Nombre, String[] Numero, int[] IDs, Application app){
        this.Nombre=Nombre;
        this.Numero=Numero;
        this.IDs=IDs;
    }
    public void setData(String[] Nombre, String[] Numero, int[] IDs){
        this.Nombre=Nombre;
        this.Numero=Numero;
        this.IDs=IDs;
        notifyDataSetChanged();
    }

    @Override
    public contactos_saramAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //Creacón de nueva vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contactos_saram, parent, false);
        return  new ViewHolder(v, mListener);
    }

    @Override
    public  void  onBindViewHolder(final ViewHolder holder, final int position){
        holder.Nombre.setText("Nombre: "+Nombre[position].toString());
        holder.Numero.setText("Número: "+Numero[position].toString());
        //holder.btn_Edit.setId(IDs[position]);
    }

    @Override
    public int getItemCount(){
        return IDs.length;
    }

}
