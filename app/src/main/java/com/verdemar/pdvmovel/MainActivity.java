package com.verdemar.pdvmovel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.lib.tsg810.module.contactless.ContactlessModule;
import com.verdemar.pdvmovel.Classes.Beep;
import com.verdemar.pdvmovel.Classes.ICLDev;
import com.verdemar.pdvmovel.Classes.Led;
import com.verdemar.pdvmovel.Classes.ISmart;
import com.verdemar.pdvmovel.Classes.Print;
import com.verdemar.pdvmovel.Pagamento.Dialog;
import com.verdemar.pdvmovel.Pagamento.Itens;
import com.verdemar.pdvmovel.Pagamento.mensagem;
import com.verdemar.pdvmovel.Pagamento.transacion;
import com.verdemar.pdvmovel.Pagamento.yesno;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.ISMART;
import br.com.softwareexpress.sitef.android.CliSiTef;
import br.com.softwareexpress.sitef.android.CliSiTefI;
import br.com.softwareexpress.sitef.android.ICliSiTefListener;


public class MainActivity extends AppCompatActivity implements ICliSiTefListener {
    private Beep beep;
    private Led led;
    private ISmart smart;
    private ISMART iSmartGedi;
    private ICLDev iclDev;
    private NfcAdapter nfcAdapter;
    private CliSiTef cliSiTef;
    private ContactlessModule contactlessModule;
    private byte[] key = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    private int trnResultCode;
    private static final int CAMPO_COMPROVANTE_CLIENTE = 121;
    private static final int CAMPO_COMPROVANTE_ESTAB = 122;
    private static int REQ_CODE = 4321;
    private static String title;
    private EditText inputText;
    private static MainActivity instance;
    private class RequestCode {
        private static final int GET_DATA = 1;
        private static final int END_STAGE_1_MSG = 2;
        private static final int END_STAGE_2_MSG = 3;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Inicio","On create");
        GEDI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        try{
            this.nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
            this.contactlessModule = new ContactlessModule(this,this, nfcAdapter);
            this.cliSiTef = new CliSiTef(getApplicationContext());
            this.cliSiTef.setMessageHandler(hndMessage);


            Intent i = new Intent();
            onNewIntent(i);

        }catch (Exception e){
            Log.i("Erro",e.getMessage());
        }
        TextView titulo = (TextView) findViewById(R.id.textStatusId);
        titulo.setText("Status Tef");
         this.inputText = (EditText) findViewById(R.id.editTextNumber);
        inputText.setText("110");
    }
    private static Handler hndMessage = new Handler() {
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case CliSiTefI.EVT_INICIA_ATIVACAO_BT:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setTitle("Ativando BT");
                    break;
                case CliSiTefI.EVT_FIM_ATIVACAO_BT:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setTitle("PinPad");
                    break;
                case CliSiTefI.EVT_INICIA_AGUARDA_CONEXAO_PP:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setTitle("Aguardando pinpad");
                    break;
                case CliSiTefI.EVT_FIM_AGUARDA_CONEXAO_PP:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setTitle("");
                    break;
                case CliSiTefI.EVT_PP_BT_CONFIGURANDO:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setTitle("Configurando pinpad");
                    break;
                case CliSiTefI.EVT_PP_BT_CONFIGURADO:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setTitle("Pinpad configurado");
                    break;
                case CliSiTefI.EVT_PP_BT_DESCONECTADO:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setTitle("Pinpad desconectado");
                    break;
            }
        }
    };



    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("onNewItent","Inicio onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        contactlessModule.intentReceived(intent);
    }

    public ArrayList<String> read(int blockRead, byte[] key) {
        return contactlessModule.readCard(4,this.key);
    }
    public void writeTag(int offset, String data, byte[] key){
        try {
            contactlessModule.writeCard(offset,data,this.key);
        }catch (Exception e){
            Log.i("Erro ao escrever",e.getMessage());
        }
    }

    //Metodo OK

    //Faz um beep
    public void onClickBeep(View v){
        beep = new Beep(this);
        beep.BeepOn();
    }

    //Acendo os led e apaga, acendo todos e apaga todos
    public void ledOn(View v){
        led = new Led(this);
        led.ledOn();
    }
    public void ledOff(View v){
        led = new Led(this);
        led.ledOff();
    }

    //ISmart exemplo so exibe o
    public void btnISMART(View view) {
        smart = new ISmart(this);
        String retorno = smart.getCard();
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(retorno);
    }

    //Metodo ICL
    public void btnICL(View view) {
        this.iclDev = new ICLDev(this);
        this.iclDev.AtivarContactless();
    }
    public void btICL2(View view){
        String texto = this.read(4,this.key).toString();
    }

    public void btPrint(View v){
        Print print = new Print(this);
        try {
            print.getStatusImpressora();
            print.imprimeTexto("Teste impressao");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void brSitef(View v){
        this.cliSiTef = CliSiTef.getInstance();

        this.cliSiTef.setDebug(true);
        int idConfig = this.cliSiTef.configure("10.0.213.92", "00000000", "pdvrd.90","TipoPinPad=Android_AUTO");
        this.cliSiTef.setActivity(this);
        int mod = Integer.valueOf(this.inputText.getText().toString());
        int i = this.cliSiTef.startTransaction(this,mod,"12","123456","20120514","120000","Teste","");

        Log.i("startTransaction",String.valueOf(i));

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
                        Log.i("CAMPO_COMPROVANTE_CLIENTE","CAMPO_COMPROVANTE_CLIENTE");
                    case CAMPO_COMPROVANTE_ESTAB:
                        Log.i("CAMPO_COMPROVANTE_ESTAB","CAMPO_COMPROVANTE_ESTAB");
                        alert(this.cliSiTef.getBuffer());
                }
                break;
            case CliSiTef.CMD_SHOW_MSG_CASHIER:
            case CliSiTef.CMD_SHOW_MSG_CUSTOMER:
            case CliSiTef.CMD_SHOW_MSG_CASHIER_CUSTOMER:
                Log.i("OnData","CMD_SHOW_MSG_CASHIER_CUSTOMER");
                setStatus(this.cliSiTef.getBuffer());
                break;
            case CliSiTef.CMD_SHOW_MENU_TITLE:
            case CliSiTef.CMD_SHOW_HEADER:
                //Primiro Entrada
                title = this.cliSiTef.getBuffer();
                Log.i("OnData","CMD_SHOW_HEADER");
                break;
            case CliSiTef.CMD_CLEAR_MSG_CASHIER:
            case CliSiTef.CMD_CLEAR_MSG_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MSG_CASHIER_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MENU_TITLE:
            case CliSiTef.CMD_CLEAR_HEADER:
                Log.i("OnData","CMD_CLEAR_HEADER");
                this.setStatus("");
                title = "";
                break;
            case CliSiTef.CMD_CONFIRM_GO_BACK:
            case CliSiTef.CMD_CONFIRMATION: {
                Log.i("OnData","CMD_CONFIRMATION");
                Intent i = new Intent(this, yesno.class);
                i.putExtra("title", title);
                i.putExtra("message", this.cliSiTef.getBuffer());
                starActivityForResult.launch(i);

                return;
            }
            case CliSiTef.CMD_GET_FIELD_CURRENCY:
            case CliSiTef.CMD_GET_FIELD_BARCODE:
            case CliSiTef.CMD_GET_FIELD: {
                Log.i("OnData","CMD_GET_FIELD");
                Intent i = new Intent(this, Dialog.class);
                i.putExtra("title", title);
                i.putExtra("message", this.cliSiTef.getBuffer());
                i.putExtra("request",RequestCode.GET_DATA);
                starActivityForResult.launch(i);
                return;
            }
            case CliSiTef.CMD_GET_MENU_OPTION: {
                //Segunda entrada
                Log.i("CMD_GET_MENU_OPTION","CMD_GET_MENU_OPTION");
                Intent i = new Intent(this, Itens.class);
                i.putExtra("title", title);
                i.putExtra("message", this.cliSiTef.getBuffer());
                i.putExtra("request",RequestCode.GET_DATA);
                starActivityForResult.launch(i);
                System.out.println(this.cliSiTef.getBuffer());
                return;
            }
            case CliSiTef.CMD_PRESS_ANY_KEY: {
                Log.i("OnData","CMD_PRESS_ANY_KEY");
                Intent i = new Intent(this, mensagem.class);
                i.putExtra("message", this.cliSiTef.getBuffer());
                starActivityForResult.launch(i);
                return;
            }
            case CliSiTef.CMD_ABORT_REQUEST:
                Log.i("OnData","CMD_ABORT_REQUEST");
                break;
            default:
                Log.i("default","default");
                break;
        }


        this.cliSiTef.continueTransaction(data);
    }
    private void setStatus(String s){
        ((TextView) findViewById(R.id.textStatusId)).setText(s);
    }
    private void alert(String message) {
        String mensagem = message;
        Toast.makeText(this,mensagem,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onTransactionResult(int stage, int resultCode) {
        trnResultCode = resultCode;
        //alert ("Fim do estágio " + stage + ", retorno " + resultCode);
        if (stage == 1 && resultCode == 0) { // Confirm the transaction
            try {
                this.cliSiTef.finishTransaction(1);
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

    ActivityResultLauncher<Intent> starActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                int code = result.getResultCode();
                switch (code){
                    case Activity.RESULT_OK:
                        if(result.getData().getStringExtra("input")!=null){
                            String returnClassItens = result.getData().getStringExtra("input");
                            this.cliSiTef.continueTransaction(returnClassItens);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        int i = this.cliSiTef.abortTransaction(-1);
                        break;
                    default:
                        break;
                }

            }
    );
}