package com.saram.app.adapters;

import android.app.Activity;
import android.app.Application;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saram.app.R;

public class contactos_saramAdapter extends RecyclerView.Adapter<contactos_saramAdapter.ViewHolder> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    // DECALARAMOS LA VARIABLE DE LA ACTIVITY
    private Activity activity;

    private String[] Nombre;
    private String[] Numero;
    private OnItemClickListener mListener;
    private int[] IDs;

    // CREAMOS EL CONTEXT MENU, EL CUÁL SE MOSTRARÁ AL DEJAR PRESIONADO EL CONTACTO SELECCIONADO
    // SE INFLA EL LAYOUT DEL MENÚ DE OPCIONES
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        // INFLAMOS EL MENÚ
        MenuInflater menuInflater = activity.getMenuInflater();

        // AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
        menuInflater.inflate(R.menu.context_menu, contextMenu);
        contextMenu.setHeaderTitle("SE ELIMINARÁ RL CONTACTO SSELECCIONADO");

        // LE DAMOS SOLO A LA PRIMERA OPCIÓN UN MÉTODO NLISTENER YA QUE SOLO TENEMOS UNO EN CASO DE QUE FUERAN
        // MÁS HABRÍA QUE COLOCAR UN FOR OBTENIENDO EL TAMAÑO DEL ARRAY
        // EJEM for (int counter=0; counter<= contextMenu.size(); counter++){...}
        contextMenu.getItem(0).setOnMenuItemClickListener(this);
    }

    // MÉTODO ENCARGADO DE VERIFICAR QUE CONTACTO HA SIDO SELECCIONADO
    // SE MANEJA EL EVENTO DE CADA OPCIÓN
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // ADAPTER QUE TRAE INFORMACIÓN DEL ITEM SELECCIONADO
        // AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // CREAMOS EL SWITCH ENCARGADO DE LEER LAS OPCIONES
        switch(menuItem.getItemId()){
            case R.id.delContacto:
                return true;
            case R.id.editContacto:
                return true;
            default:
                return false;
        }
    }

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
            btn_delete = contactos.findViewById(R.id.imgEliminar);
            btn_Edit = contactos.findViewById(R.id.ivEdit);

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

           // HABILITAMOS EL CONTEXT MENU DENTRO DE CADA ITEM
           // contactos.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);/
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
