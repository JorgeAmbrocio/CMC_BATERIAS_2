package com.example.baterias.Services;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Utiles  {
    public static Context contexto;
    public static String strIP;
    public static String strId;
    public static String strRelativePath;
    private static String strNombreArchivo = "token.txt";

    public static String getBateriasToken () {
        String archivos [] = contexto.fileList();
        if (fileExist(archivos)) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(contexto.openFileInput(strNombreArchivo));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String linea = null, contenido = "";
                while ( (linea = bufferedReader.readLine()) != null){
                    contenido += linea;
                }
                bufferedReader.close();
                return  contenido;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setBateriasToken (String token) {
        if (token.equals("Error autenticación") || token.equals("Error al generar token")) {
            return;
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(contexto.openFileOutput(strNombreArchivo, Activity.MODE_PRIVATE));
            outputStreamWriter.write(token);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean fileExist (String[] archivos) {
        for (int i = 0 ; i < archivos.length; i++){
            if (archivos[i].equals(strNombreArchivo))
                return true;
        }
        return false;
    }
}
