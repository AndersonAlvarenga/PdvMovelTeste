package com.verdemar.pdvmovel.Classes;


import static android.widget.Toast.makeText;
import static br.com.softwareexpress.sitef.android.CliSiTef.CMD_SHOW_QRCODE_FIELD;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import br.com.softwareexpress.sitef.android.CliSiTef;
import br.com.softwareexpress.sitef.android.ICliSiTefListener;
import br.com.softwareexpress.sitef.JCliSiTefI;

public class Sitef extends Activity implements ICliSiTefListener  {

    private Context context;
    private Activity activity;
    private static final int CAMPO_COMPROVANTE_CLIENTE = 121;
    private static final int CAMPO_COMPROVANTE_ESTAB = 122;
    private CliSiTef cliSiTef;
    private static Handler hndMessage = new Handler();


    public Sitef(Context context,Activity activity){
        this.activity = activity;
        this.context = context;
        this.cliSiTef = CliSiTef.getInstance();

    }
    public void init(){
        try{
            this.cliSiTef.setActivity(this.activity);
        }catch (Exception e){
            Log.e("Erro setActivity: ",e.getMessage());
        }
        try{
            int i = this.cliSiTef.startTransaction(this,110,"12","123456","20120514","120000","Teste","");
        }catch (Exception e){
            Log.e("Erro startTransaction: ",e.getMessage());
        }
        try{
            JCliSiTefI t = new JCliSiTefI();
            t.iniciaFuncaoSiTefInterativo();
            t.abrePinPad();

        }catch (Exception e){
            Log.e("Erro ",e.getMessage());
        }
    }

    private void setStatus(String s) {
        //Setar retornar status igual a s
    }


    private void alert(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onData(int stage, int command, int fieldId, int minLength, int maxLength, byte[] input) {
        String data = "";

        if (stage == 1) {
            // Evento onData recebido em uma startTransaction
        } else if (stage == 2) {
            // Evento onData recebido em uma finishTransaction
        }

        switch (command) {
            case CliSiTef.CMD_RESULT_DATA:
                switch (fieldId) {
                    case CAMPO_COMPROVANTE_CLIENTE:
                    case CAMPO_COMPROVANTE_ESTAB:
                        alert(clisitef.getBuffer());
                }
                break;
            case CliSiTef.CMD_SHOW_MSG_CASHIER:
            case CliSiTef.CMD_SHOW_MSG_CUSTOMER:
            case CliSiTef.CMD_SHOW_MSG_CASHIER_CUSTOMER:
                setStatus(clisitef.getBuffer());
                break;
            case CliSiTef.CMD_SHOW_MENU_TITLE:
            case CliSiTef.CMD_SHOW_HEADER:
                title = clisitef.getBuffer();
                break;
            case CliSiTef.CMD_CLEAR_MSG_CASHIER:
            case CliSiTef.CMD_CLEAR_MSG_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MSG_CASHIER_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MENU_TITLE:
            case CliSiTef.CMD_CLEAR_HEADER:
                title = "";
                setStatus("");
                break;
            case CliSiTef.CMD_CONFIRM_GO_BACK:
            case CliSiTef.CMD_CONFIRMATION: {

                return;
            }
            case CliSiTef.CMD_GET_FIELD_CURRENCY:
            case CliSiTef.CMD_GET_FIELD_BARCODE:
            case CliSiTef.CMD_GET_FIELD: {

                return;
            }
            case CliSiTef.CMD_GET_MENU_OPTION: {

                return;
            }
            case CliSiTef.CMD_PRESS_ANY_KEY: {

                return;
            }
            case CliSiTef.CMD_ABORT_REQUEST:
                break;
            default:
                break;
        }


        clisitef.continueTransaction(data);
    }

    @Override
    public void onTransactionResult(int stage, int resultCode) {

        trnResultCode = resultCode;
        //alert ("Fim do estágio " + stage + ", retorno " + resultCode);
        if (stage == 1 && resultCode == 0) { // Confirm the transaction
            try {
                clisitef.finishTransaction(1);
            } catch (Exception e) {
                //alert(e.getMessage());
            }
        } else {

            if (resultCode == 0) {
                finish();
            } else {

            }
        }
    }

    private class RequestCode {
        private static final int GET_DATA = 1;
        private static final int END_STAGE_1_MSG = 2;
        private static final int END_STAGE_2_MSG = 3;
    }

    // Variaveis estaticas para nao serem reinicializadas ao rodar o display
    // Para tanto, vamos assumir que esta atividade nunca será executada em
    // paralelo com outra igual (singleton)

    private int trnResultCode;
    private static String title;
    private static CliSiTef clisitef;



}
