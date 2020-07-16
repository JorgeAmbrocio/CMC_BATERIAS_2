package com.example.baterias.Services;

import android.os.StrictMode;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class APIbaterias {
    // --------------------------------------------------------------
    public class Bateria {
        public String skuBateria;
        public String marcaBateria;
        public String modeloBateria;
        public String posicion;
        public String idFoto;

        public byte[] fotoByte;
        public Bateria(String skuBateria, String marcaBateria, String modeloBateria, String posicion, String idFoto) {
            this.skuBateria = skuBateria;
            this.marcaBateria = marcaBateria;
            this.modeloBateria = modeloBateria;
            this.posicion = posicion;
            this.idFoto = idFoto;
        }
    }

    public class Foto {
        public byte[] fotoByte;
        public String idFoto;

        public Foto(byte[] fotoByte, String idFoto) {
            this.fotoByte = fotoByte;
            this.idFoto = idFoto;
        }
    }

    private class Tipo {
        public String tipo;
        public ArrayList<String> marcas;
    }
    // --------------------------------------------------------------
    private String rutaBase;
    private String tokenAutorizacion = null;
    private ArrayList<Foto> fotos = new ArrayList<>();
    private ArrayList<Tipo> tipos = null;
    public APIbaterias (){
        //this.rutaBase = "http://192.168.1.3/API_BATERIAS/api/";
        this.rutaBase = "http://10.0.0.10:3636/api/";
        //this.rutaBase = "https://bateriascmc.azurewebsites.net/api/";

        this.tokenAutorizacion = Utiles.getBateriasToken();
        if (this.tokenAutorizacion == null) {
            this.getTokenFromAPI();
        }

    }

    public ArrayList<String> getTipos () {
        ArrayList<String> tipos_ = new ArrayList<>();

        // verifica si debe solicitar los tipos o si ya están almacenados en memoria
        if (this.tipos == null) {
            // no hay tipos, solicitar tipos
            this.tipos = new ArrayList<>();
            JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Vehiculos/getTipos");
            try{
                for (int i = 0 ; i < jsonArray.length(); i++) {
                    Tipo tipo_ = new Tipo ();
                    tipo_.tipo = jsonArray.getString(i);
                    this.tipos.add(tipo_);
                }
            } catch (Exception e) {
                //Toast.makeText(null, "No se han podido obtener los TIPOS DE VEHÍCULOS", Toast.LENGTH_SHORT).show();
            }
        }

        for (Tipo t : this.tipos) {
            tipos_.add(t.tipo);
        }

        return tipos_;
    }

    public ArrayList<String> getMarcas (String tipo) {
        ArrayList<String> retorno = new ArrayList<>();
        for (Tipo t: this.tipos) {
            if (t.tipo == tipo) {
                // aún no se han consultado las marcas, obtenerlas
                if (t.marcas == null) {
                    t.marcas = new ArrayList<>();
                    JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Marcas/" + tipo);
                    try{
                        for ( int i = 0 ; i < jsonArray.length(); i++) {
                            t.marcas.add(jsonArray.getString(i));
                        }
                    }catch (Exception e) {
                        Toast.makeText(null, "No se ha podido obtener las MARCA VEHÍCULO", Toast.LENGTH_SHORT).show();
                    }
                }
                retorno = t.marcas;
            }
        }

        return  retorno;
    }

    public ArrayList<String> getLineas (String marca) {
        ArrayList<String> retorno = new ArrayList<>();

        try{
            JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Lineas/" + marca);
            for (int i = 0 ; i < jsonArray.length(); i++){
                retorno.add(jsonArray.getString(i));
            }
        } catch (Exception e){
            Toast.makeText(null, "No se ha podido obtener las MARCA VEHÍCULO", Toast.LENGTH_SHORT).show();
        }

        return retorno;
    }

    public ArrayList<String> getModelos (String marca, String linea, String procedencia) {
        ArrayList<String> retorno  = new ArrayList<String>();
        try {
            JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Vehiculos/getModelos?marca=" + marca + "&linea=" + linea + "&procedencia="+procedencia);
            for (int i = 0 ; i<jsonArray.length(); i++) {
                retorno.add(jsonArray.getString(i));
            }
        } catch (Exception e) {
            Toast.makeText(null, "No se ha podido obtener las LÍNEA VEHÍCULO", Toast.LENGTH_SHORT).show();
        }
        return retorno;
    }

    public ArrayList<Bateria> getBaterias (String marca, String linea, String modelo, String procedencia) {
        ArrayList<Bateria> retorno = new ArrayList<>();
        try {
            JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Baterias/getBaterias?marca=" + marca + "&linea=" + linea + "&modelo=" + modelo + "&procedencia=" + procedencia);
            for (int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String skub = jsonObject.optString("skuBateria"),
                        marcab = jsonObject.optString("marcaBateria"),
                        modelob= jsonObject.optString("modeloBateria"),
                        posicionb= jsonObject.optString("posicion"),
                        idfotob = jsonObject.optString("idFoto");
                Foto f = this.getFoto(idfotob);
                Bateria bateria_ = new Bateria(skub,marcab,modelob, posicionb, idfotob);
                bateria_.fotoByte = f.fotoByte;
                retorno.add(bateria_);
            }
        }catch (Exception e) {
            Toast.makeText(null, "No se ha podido obtener las BATERÍAS VEHÍCULO", Toast.LENGTH_SHORT).show();
        }
        return retorno;
    }

    private Foto getFoto (String id) {
        Foto foto = null;

        for (Foto foto_: this.fotos) {
            if (foto_.idFoto == id) {
                foto = foto_;
                break;
            }
        }

        if (foto == null) {
            try {
                JSONArray jsonArray = this.getDesdeAPI("GET", rutaBase + "Fotos/" + id);
                for (int i =  0 ; i < jsonArray.length(); i ++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String strFoto = jsonObject.optString("foto"); // viene como un Byte[].ToString()
                    byte[] mapa = Base64.decode(strFoto, Base64.DEFAULT);
                    foto = new Foto(mapa ,jsonObject.optString("idFoto"));
                    this.fotos.add(foto);
                }
            }catch (Exception e){
                Toast.makeText(null, "No se ha podido obtener las FOTO BATERÍA", Toast.LENGTH_SHORT).show();
            }
        }

        return foto;
    }

    // OBTIENE UN ARRAYLIST DE LA APPI
    private JSONArray getDesdeAPI (String verbo, String ruta) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ruta = ruta.replace(" " , "%20");
        ruta = ruta.replace("Ñ", "%C3%91");

        try{
            URL url = new URL (ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(verbo);
            connection.setRequestProperty("Authorization","Bearer " + this.tokenAutorizacion);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader bf = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String inputLine;
                while ((inputLine = bf.readLine()) != null){
                    stringBuffer.append(inputLine);
                }
                connection.disconnect();
                return  new JSONArray(stringBuffer.toString());
            }else {
                connection.disconnect();
                this.getTokenFromAPI();
            }

        }catch (Exception e){
            String mensaje = e.getMessage();
            return null;
        }

        return  null;
    }

    private void getTokenFromAPI () {
        JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Acceso/" + Utiles.strIP);
        //JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Acceso/" + "192.168.1.2");
        //JSONArray jsonArray = this.getDesdeAPI("GET", this.rutaBase + "Acceso/" + "143.190.230.128");

        try {
            for (int i = 0 ; i < jsonArray.length(); i ++){
                String token = jsonArray.getString(i);
                this.tokenAutorizacion = token;
                Utiles.setBateriasToken(token);
                Utiles.getBateriasToken();
            }
        }catch (Exception e){
            String contenido = e.getMessage();
        }
    }

}
