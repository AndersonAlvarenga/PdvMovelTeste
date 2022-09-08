package com.verdemar.pdvmovel.Classes;

import android.content.Context;
import android.widget.Toast;

//IMPORT CLASSES LIB GEDI
import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IAUDIO;


public class Beep {
    //Variaveis Beep
    private IAUDIO iAudio;
    private Context context;

    public Beep(Context c){
        this.context = c;
        //GEDI.init(this.context);
        try {
            iAudio = GEDI.getInstance().getAUDIO();
        }catch (Exception e){
            Toast.makeText(this.context,e.getMessage(),Toast.LENGTH_LONG);
        }

    }

    public void BeepOn(){
        try {
            this.iAudio.Beep();
        }catch (Exception e){
            Toast.makeText(this.context,e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
