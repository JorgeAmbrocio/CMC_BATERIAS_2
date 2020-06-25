package com.example.baterias.conexion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baterias.R;

import java.util.List;

public class RVAdapater extends RecyclerView.Adapter<RVAdapater.ViewHolder> {

    public class ViewHolder extends  RecyclerView.ViewHolder {
        private TextView tvMarca, tvModeloSKU , tvPosicion;
        private ImageView imgBateria;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvMarca = (TextView) itemView.findViewById(R.id.tvMarca);
            this.tvModeloSKU = (TextView) itemView.findViewById(R.id.tvModeloSKU);
            this.tvPosicion = (TextView) itemView.findViewById(R.id.tvPosicion);
            this.imgBateria = (ImageView) itemView.findViewById(R.id.imgBateria);
        }
    }


    public List<APIbaterias.Bateria> listaBateria;
    public RVAdapater(List<APIbaterias.Bateria> listaBateria) {this.listaBateria = listaBateria;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bateria, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMarca.setText(this.listaBateria.get(position).marcaBateria);
        holder.tvModeloSKU.setText(this.listaBateria.get(position).modeloBateria);
        holder.tvPosicion.setText(this.listaBateria.get(position).posicion);

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(this.listaBateria.get(position).fotoByte, 0, this.listaBateria.get(position).fotoByte.length);
            holder.imgBateria.setImageBitmap(bitmap);
        }catch (Exception e) {
            Toast.makeText(null, "No se ha podido cargar la imagen\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return this.listaBateria.size();
    }


}
