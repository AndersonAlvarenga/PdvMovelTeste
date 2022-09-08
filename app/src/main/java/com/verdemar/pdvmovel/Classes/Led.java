package com.verdemar.pdvmovel.Classes;

import android.content.Context;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.ILED;
import br.com.gertec.gedi.enums.GEDI_LED_e_Id;
import br.com.gertec.gedi.exceptions.GediException;

public class Led {
    private ILED iLed;
    private Context context;

    public Led(Context context){
        this.context = context;
        iLed = GEDI.getInstance().getLED();
    }

    public void ledOn() {
        for (GEDI_LED_e_Id c : GEDI_LED_e_Id.values()) {

            if (c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_RED) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_GREEN) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_ORANGE) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_BLUE)) {

                try {
                    iLed.Set(c, true);
                    System.out.println("iLed.Set - " + c + ":\t- OK");


                } catch (GediException e) {
                    System.out.println("iLed.Set - " + c + ":\t- FAIL -- " + e.getErrorCode().name());


                } catch (Exception e) {
                    System.out.println("iLed.Set - \t- FAIL - " + e.getMessage());


                }
            }


        }
    }
    public void ledOff() {
        System.out.println("ILed - Desligado");
        for (GEDI_LED_e_Id c : GEDI_LED_e_Id.values()) {

            if (c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_RED) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_GREEN) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_ORANGE) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_BLUE)) {
                try {
                    iLed.Set(c, false);
                    System.out.println("iLed.Set - " + c + ":\t\t\t- OK");
                } catch (GediException e) {
                    System.out.println("iLed.Set - " + c + ":\t\t\t- FAIL -- " + e.getErrorCode().name());
                } catch (Exception e) {
                    System.out.println("iLed.Set - \t\t\t- FAIL - " + e.getMessage());
                }
            }
        }
    }


}
