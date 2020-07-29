package com.example.baterias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.baterias.Services.APIbaterias;
import com.example.baterias.Services.DialogNoModelo;
import com.example.baterias.Services.RVAdapater;

import java.util.ArrayList;

public class BateriasActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private APIbaterias apIbaterias = new APIbaterias();
    private Spinner spTipo, spMarca, spLinea, spModelo;
    private RadioButton rbAgencia, rbRodado;
    private RecyclerView rvBaterias;
    private String strProcedencia = "Agencia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // crear la pantalla
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activarPantallaCompleta();
        setContentView(R.layout.activity_baterias);

        // iniciar objetos de interacción
        this.spTipo = (Spinner) findViewById(R.id.spTipo);
        this.spMarca = (Spinner) findViewById(R.id.spMarca);
        this.spLinea = (Spinner) findViewById(R.id.spLinea);
        this.spModelo = (Spinner) findViewById(R.id.spModelo);

        this.rbAgencia = (RadioButton) findViewById(R.id.rbAgencia);
        this.rbRodado = (RadioButton) findViewById(R.id.rbRodado);

        this.rvBaterias = (RecyclerView) findViewById(R.id.rvBaterias);

        // indicar los acction listener
        this.spTipo.setOnItemSelectedListener(this);
        this.spMarca.setOnItemSelectedListener(this);
        this.spLinea.setOnItemSelectedListener(this);
        this.spModelo.setOnItemSelectedListener(this);

        this.rbAgencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strProcedencia = "Agencia";
                ejecutarCambios(spLinea);
            }
        });

        this.rbRodado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strProcedencia = "Rodado";
                ejecutarCambios(spLinea);
            }
        });

        this.llenarTipos();
        this.rvBaterias.setLayoutManager(new LinearLayoutManager(this));
    }

    private void activarPantallaCompleta () {
        View vista = getWindow().getDecorView();
        vista.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        ejecutarCambios(adapterView);
    }

    public void ejecutarCambios (AdapterView<?> adapterView) {
        this.rvBaterias.setAdapter(null);
        switch (adapterView.getId()){
            case R.id.spTipo:
                // llenar marca
                this.setSpinnerData(
                        this.spMarca,
                        this.apIbaterias.getMarcas(
                                this.getItemSelected(this.spTipo).id
                        )
                );
                break;
            case R.id.spMarca:
                // llenar linea
                this.setSpinnerData(
                        this.spLinea,
                        this.apIbaterias.getLineas(
                                this.getItemSelected(this.spTipo).id,
                                this.getItemSelected(this.spMarca).id
                        )
                );
                break;
            case R.id.spLinea:
                // llenar modelo
                this.setSpinnerData(
                        this.spModelo,
                        this.apIbaterias.getModelos(
                            this.getItemSelected(this.spTipo).id,
                            this.getItemSelected(this.spLinea).id
                        )
                );
                break;
            case R.id.spModelo:
                // ejecutar búsqueda
                ArrayList<APIbaterias.Bateria> baterias_ = this.apIbaterias.getBaterias(
                        this.getItemSelected(this.spTipo ).id,
                        this.getItemSelected(this.spLinea).id,
                        this.getItemSelected(this.spModelo).dato,
                        this.strProcedencia);
                if (baterias_.isEmpty()){
                    MostrarMensajeNoModelos ();
                }
               RVAdapater rvAdapater = new RVAdapater(baterias_);
                this.rvBaterias.setAdapter(rvAdapater);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "No se ha seleccionado elemento", Toast.LENGTH_SHORT).show();
    }

    /// funciones para
    private void llenarTipos () {
        this.setSpinnerData(this.spTipo, apIbaterias.getTipos());
    }

    private APIbaterias.Dato getItemSelected(Spinner spinner){
        APIbaterias.Dato r = new APIbaterias.Dato();
        try {
            r = (APIbaterias.Dato) spinner.getSelectedItem();
        }catch (Exception e){}

        return r;
    }

    private void setSpinnerData (Spinner sp, ArrayList<APIbaterias.Dato> ar) {
        try {
            ArrayAdapter<APIbaterias.Dato> arrayAdapter = new ArrayAdapter<APIbaterias.Dato>(this, R.layout.text_spinner, ar);
            sp.setAdapter(arrayAdapter);
        }catch (Exception e){

        }
    }

    private void MostrarMensajeNoModelos () {
        DialogNoModelo dialogNoModelo = new DialogNoModelo(this.strProcedencia);
        dialogNoModelo.show(getSupportFragmentManager(), "Baterías");
    }

}