package com.verdemar.pdvmovel.Classes;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.verdemar.pdvmovel.MainActivity;
import com.verdemar.pdvmovel.R;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.ISMART;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Slot;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Voltage;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.structs.GEDI_SMART_st_ResetInfo;

public class ISmart {
    private Context context;
    private ISMART iSmart;
    private StringBuilder sb;
    private GEDI_SMART_st_ResetInfo resetEMV;
    private GEDI_SMART_st_ResetInfo warmResetEMV;
    private int contadorTesteCartao =10;

    public ISmart(Context context){
        this.context = context;
        iSmart = GEDI.getInstance().getSMART();
    }
    public GEDI_SMART_e_Status status(GEDI_SMART_e_Slot eSlot) {
        try {
            return iSmart.Status(eSlot);
        } catch (GediException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void powerOff() {
        try {
            iSmart.PowerOff(GEDI_SMART_e_Slot.USER);
        } catch (GediException e) {
            e.printStackTrace();
        }
    }
    public void gedi_SmartCard() {
        try {

                try {
                    for (String a = status(GEDI_SMART_e_Slot.USER).toString(); !a.equals("PRESENT") && this.contadorTesteCartao > 0; a = status(GEDI_SMART_e_Slot.USER).toString()) {
                    }
                    this.resetEMV = iSmart.ResetEMV(GEDI_SMART_e_Slot.USER, GEDI_SMART_e_Voltage.VOLTAGE_5V);
                    this.warmResetEMV = iSmart.WarmResetEMV(GEDI_SMART_e_Slot.USER, GEDI_SMART_e_Voltage.VOLTAGE_5V);
                } catch (GediException e) {
                    e.printStackTrace();
                }

        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }
    public static String byteArrayToHexString(byte[] in) {
        StringBuilder builder = new StringBuilder();
        if (in != null) {
            byte[] var2 = in;
            int var3 = in.length;
            for (int var4 = 0; var4 < var3; var4++) {
                builder.append(String.format("%02X", new Object[]{Byte.valueOf(var2[var4])}));
            }
        }
        return builder.toString();
    }
    public byte[] sendApu(GEDI_SMART_e_Slot slot,byte[] b){
        try {
            return iSmart.SendAPDU(slot,b);
        } catch (GediException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getCard(){
        powerOff();
        gedi_SmartCard();
        return byteArrayToHexString(resetEMV.abATR);
    }

}
