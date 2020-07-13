package com.example.baterias;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.baterias.Services.Utiles;

public class MainActivity extends AppCompatActivity {

    private Button btnBaterias;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Utiles.contexto = this;
            // obtener la ip del dispositivo
            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            Utiles.strIP = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
        }catch ( Exception e) {
            Toast.makeText(this, "No se ha podio obtener IPV4", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToBaterias(View view) {
        try {
            Intent intent = new Intent(MainActivity.this, BateriasActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(), "No se ha podido cargar la pantalla de baterías.", Toast.LENGTH_SHORT).show();
        }
    }

    public Context getContext () {return getApplicationContext();}
}