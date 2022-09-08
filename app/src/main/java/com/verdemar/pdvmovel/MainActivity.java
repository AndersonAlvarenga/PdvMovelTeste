package com.verdemar.pdvmovel;

import androidx.appcompat.app.AppCompatActivity;
import com.verdemar.pdvmovel.Classes.Beep;
import com.verdemar.pdvmovel.Classes.ICLDev;
import com.verdemar.pdvmovel.Classes.Led;
import com.verdemar.pdvmovel.Classes.ISmart;


import android.os.Bundle;
import android.view.View;


import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.ISMART;


public class MainActivity extends AppCompatActivity{
    private Beep beep;
    private Led led;
    private ISmart smart;
    private ISMART iSmartGedi;
    private ICLDev iclDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GEDI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        System.out.println("Aguardando cartão...");

        try {
            iSmartGedi = smart.getiSmart();
            System.out.println("getSMART\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getSMART\t\t\t- FAIL");
        }

        smart.smartCardPowerOff(smart.getiSmart());
        boolean teste = smart.checkCard(smart.getiSmart());
        smart.smartCardStatus(smart.getiSmart());

    }

    //Metodo ICL
    public void btnICL(View view) {
        this.iclDev = new ICLDev(this);
        this.iclDev.AtivarContactless();
    }
}