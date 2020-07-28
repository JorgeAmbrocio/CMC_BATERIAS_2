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


public class MainActivity extends AppCompatActivity {

    private Button btnBaterias;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToBaterias(View view) {
        try {
            Intent intent = new Intent(MainActivity.this, BateriasActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(), "No se ha podido cargar la pantalla de baterías.", Toast.LENGTH_SHORT).show();
        }
    }

}