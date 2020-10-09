package com.example.baterias.Services;

import android.os.StrictMode;
import android.util.Base64;
import android.util.JsonReader;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;

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
        public int idFoto;

        public byte[] fotoByte;
        public Bateria(String skuBateria, String marcaBateria, String modeloBateria, String posicion, int idFoto) {
            this.skuBateria = skuBateria;
            this.marcaBateria = marcaBateria;
            this.modeloBateria = modeloBateria;
            this.posicion = posicion;
            this.idFoto = idFoto;
        }
    }

    public class Foto {
        public byte[] fotoByte;
        public int idFoto;

        public Foto(byte[] fotoByte, int idFoto) {
            this.fotoByte = fotoByte;
            this.idFoto = idFoto;
        }
    }

    private class Tipo {
        public String tipo;
        public ArrayList<String> marcas;
    }

    public static class Dato {
        public int id;
        public String dato;

        @Override
        public String toString() {
            return this.dato;
        }
    }
    // --------------------------------------------------------------
    private String rutaBase;
    private ArrayList<Foto> fotos = new ArrayList<>();
    public APIbaterias (){
        this.rutaBase = "http://172.16.17.101/API.BATERIAS/Api/";
    }

    public ArrayList<Dato> getTipos () {
        return this.getArrayDato(this.rutaBase + "GetTipos");
    }

    public ArrayList<Dato> getMarcas (int tipo) {
        return this.getArrayDato(this.rutaBase + "GetMarcas?idTipo=" + tipo);
    }

    public ArrayList<Dato> getLineas (int tipo, int marca) {
        return this.getArrayDato(this.rutaBase + "GetLineas?idTipo=" + tipo + "&idMarca=" + marca);
    }

    public ArrayList<Dato> getModelos (int tipo, int linea) {
        return this.getArrayDato(this.rutaBase + "GetModelos?idTipo=" + tipo + "&idLinea=" + linea);
    }

    public ArrayList<Bateria> getBaterias (int tipo, int linea, String modelo, String procedencia) {
        ArrayList<Bateria> retorno = new ArrayList<>();
        try {
            JSONArray jsonArray = this.getFromAPI("GET", this.rutaBase + "GetBaterias?idTipo=" + tipo + "&idLinea=" + linea + "&modelo=" + modelo + "&procedencia=" + procedencia);
            for (int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String skub = jsonObject.optString("skuBateria"),
                        marcab = jsonObject.optString("marcaBateria"),
                        modelob= jsonObject.optString("modeloBateria"),
                        posicionb= jsonObject.optString("posicion");
                int idfotob = jsonObject.optInt("idFoto");
                Foto f = this.getFoto(idfotob);
                Bateria bateria_ = new Bateria(skub,marcab,modelob, posicionb, idfotob);
                bateria_.fotoByte = f.fotoByte;
                retorno.add(bateria_);
            }
        }catch (Exception e) {

        }

        return retorno;
    }

    private Foto getFoto (int id) {
        Foto foto = null;

        for (Foto foto_: this.fotos) {
            if (foto_.idFoto == id) {
                foto = foto_;
                break;
            }
        }

        if (foto == null) {
            try {
                JSONArray jsonArray = this.getFromAPI("GET", rutaBase + "GetFotos?id=" + id);
                for (int i =  0 ; i < jsonArray.length(); i ++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String strFoto = jsonObject.optString("foto"); // viene como un Byte[].ToString()
                    byte[] mapa = Base64.decode(strFoto, Base64.DEFAULT);
                    foto = new Foto(mapa ,jsonObject.optInt("idFoto"));
                    this.fotos.add(foto);
                }
            }catch (Exception e){

            }
        }

        return foto;
    }

    // OBTIENE UN ARRAY DE OBJETOS DE TIPO DATO HACIENDO USO DE getFromAPI
    private ArrayList<Dato> getArrayDato(String ruta) {
        ArrayList<Dato> retorno = new ArrayList<>();

        try {
            JSONArray jsonArray = this.getFromAPI("GET", ruta);
            for (int i = 0 ; i < jsonArray.length() ; i++) {
                Dato dato = new Dato();
                JSONObject jsonObject =  jsonArray.getJSONObject(i);
                dato.id  = jsonObject.optInt("id");
                dato.dato = jsonObject.optString("dato");

                retorno.add(dato);
            }
        }catch ( Exception e){}

        return retorno;
    }

    // OBTIENE UN ARRAYLIST DE LA APPI
    private JSONArray getFromAPI (String verbo, String ruta) {
        JSONArray retorno = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ruta = ruta.replace(" " , "%20");
        ruta = ruta.replace("Ñ", "%C3%91");

        try{
            URL url = new URL (ruta);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-token","718y4C5Z3dfWQeU/uwU1SxM0+l2E9sDtXKaMsBXpL4U=");
            connection.setRequestMethod(verbo);
            connection.connect();

            int response = connection.getResponseCode(); // verificar que la respuesta sea buena
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader bf = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String inputLine;
                while ((inputLine = bf.readLine()) != null){
                    stringBuffer.append(inputLine);
                }

                JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                int estado = jsonObject.optInt("estado"); // verificar que el retorno sea correcto
                if (estado == 1) {
                    // todo bien
                    retorno = (JSONArray) jsonObject.opt("datos");
                }else {
                    // todo mal
                }
            }else {

            }
            connection.disconnect();
        }catch (Exception e){

        }

        return  retorno;
    }


}
