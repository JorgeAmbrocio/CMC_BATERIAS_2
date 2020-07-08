package com.example.baterias.Services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogNoModelo  extends AppCompatDialogFragment {
    String rodado  = "";

    public DialogNoModelo(String rodado) {
        if (rodado.equals("Rodado")) {
            this.rodado = rodado;
        }else {
            this.rodado = "de " + rodado;
        }
    }

    @Override
    public Dialog onCreateDialog (Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Baterías")
                .setMessage("Tu batería es especial porque tu auto en ésta línea es " + this.rodado +  ".\nAcércate a un asesor para que te pueda apoyar.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        return  builder.create();
    }
}
