package com.verdemar.pdvmovel.Classes;

import android.content.Context;
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

public class ISmart {
    private Context context;
    private ISMART iSmart;
    private StringBuilder sb;

    public ISmart(Context context){
        this.context = context;
    }
    public ISMART getiSmart(){
        return GEDI.getInstance().getSMART();
    }
    public void smartCardWarmResetEMV() {
        try {
            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                iSmart.WarmResetEMV(c, GEDI_SMART_e_Voltage.VOLTAGE_1_8V);
                System.out.printf("iSmart.WarmResetEMV: %s\t\t\t- OK\n", c);
            }
        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.WarmResetEMV\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iSmart.WarmResetEMV\t\t\t- FAIL - " + e.getMessage());
        }
    }

    public void smartCardResetEMV() {
        try {
            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                iSmart.ResetEMV(c, GEDI_SMART_e_Voltage.VOLTAGE_1_8V);
                System.out.printf("iSmart.ResetEMV: %s\t\t\t- OK\n", c);
            }
        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.ResetEMV\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iSmart.ResetEMV\t\t\t- FAIL - " + e.getMessage());
        }
    }

    public void smartCardPowerOff(ISMART ismart) {
        this.iSmart = ismart;
        try {

            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                iSmart.PowerOff(c);
                System.out.printf("iSmart.PowerOff: %s\t\t\t- OK\n", c);
            }

        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.PowerOff\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iSmart.PowerOff\t\t\t- FAIL - " + e.getMessage());
        }
    }

    public String smartCardStatus(ISMART ismart) {
        sb = new StringBuilder();
        this.iSmart = ismart;
        for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {

            try {

                GEDI_SMART_e_Status status = iSmart.Status(c);

                final String r = String.format("iSmart - Status - %s:\t%s\n", c, status);
                System.out.printf(r);

                sb.append(r);

            } catch (GediException gedi_e_ret) {
                System.out.println("iSmart.Status\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
            } catch (Exception e) {
                System.out.println("iSmart.Status\t\t\t- FAIL - " + e.getMessage());
            }
        }
        return sb.toString();
    }

    public boolean checkCard(ISMART ismart){
        boolean conected = false;
        long index = 0;
        while(conected == false && index<5000){
            try {
                if (iSmart.Status(GEDI_SMART_e_Slot.USER) == GEDI_SMART_e_Status.PRESENT) {
                    conected = true;
                }
            }catch (Exception e){

            }


            index+=1;
        }
        return conected;
    }

}
