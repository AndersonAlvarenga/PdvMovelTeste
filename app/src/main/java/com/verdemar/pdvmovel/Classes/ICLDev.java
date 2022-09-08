package com.verdemar.pdvmovel.Classes;

import android.content.Context;
import android.util.Log;

import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.structs.GEDI_CL_st_ISO_PollingInfo;
import br.com.gertec.gedi.structs.GEDI_CL_st_MF_Key;
import br.com.gertec.gedi.enums.GEDI_CL_e_MF_KeyType;


public class ICLDev {
    private Context context;
    private ICL iCl;

    public ICLDev(Context context) {
        this.context = context;
    }

    public void AtivarContactless() {
        try {
            iCl = GEDI.getInstance().getCL();

            System.out.println("getCL\t\t\t- OK");

        } catch (Exception e) {
            System.out.println("getCL\t\t\t- FAIL - " + e.getMessage());
        }



        cl_PowerOn();
        cl_PowerOff();

        final GEDI_CL_st_ISO_PollingInfo[] pollingInfo = new GEDI_CL_st_ISO_PollingInfo[1];
        final GEDI_CL_st_MF_Key key = new GEDI_CL_st_MF_Key();

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 1; i <= 3; i++) {

                    final int aux = i;
                    try {


                        pollingInfo[0] = iCl.ISO_Polling(5 * 1000);

                        System.out.println("iCl.ISO_Polling\t\t\t- OK");
                        System.out.printf("iCl.ISO_Polling - peType: %s\n", pollingInfo[0].peType);
                        Log.e("Polling","iCl.ISO_Polling - peType: %s\n"+ pollingInfo[0].peType);

                        byte[] abUID = pollingInfo[0].abUID;

                        String UID = arrayBytesToString(abUID);
                        System.out.println("iCl.PollingInfo UID: " + UID);
                        Log.e("UID","iCl.PollingInfo UID: " + UID);
                        key.abValue = new byte[]{0xf, 0xf, 0xf, 0xf};
                        key.abValue = new byte[]{0x0f, 0x1a, 0x2c, 0x33}; //Cartão Gertec
                        key.abValue = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}; // Cartão Cliente


                        System.out.println("iCl.MF_BlockREAD: BEGIN");

                        key.eType = GEDI_CL_e_MF_KeyType.KEY_A;
                        byte[] blockInfo = null;

                        for (i = 0; i < 130; i += 4) {
                            try {
                                iCl.MF_Authentication(i, key, key.abValue);


                                blockInfo = GEDI.getInstance().getCL().MF_BlockRead(i);
                            } catch (GediException e) {
                                if (e.toString().contains("252")) {
                                    System.out.println("iCl.GEDI Exception - Senha Errada!!!! - " + e);

                                } else {
                                    System.out.println("iCl.read error: " + e);
                                }
                                e.printStackTrace();
                            }
                            if (blockInfo != null)
                                System.out.println("iCl.PollingInfo MF_BlockRead[" + String.format("%03d", i) + "]: " + arrayBytesToString(blockInfo));
                            blockInfo = null;
                        }
                        System.out.println("iCl.MF_BlockREAD: END");
                        System.out.println("iCl.");


                        System.out.println("iCl.WRITE");
                        iCl.MF_Authentication(112, key, key.abValue);
                        iCl.MF_BlockWrite(112, hexStringToByteArray("bcde"));

                        iCl.MF_Authentication(116, key, key.abValue);
                        iCl.MF_BlockWrite(116, hexStringToByteArray("ffddd"));
                        Log.e("Tese","Chego aki");
                        System.out.println("iCl.MF_BlockREAD: BEGIN");
                        for (i = 0; i < 130; i += 4) {
                            try {
                                iCl.MF_Authentication(i, key, key.abValue);
                                blockInfo = GEDI.getInstance().getCL().MF_BlockRead(i);
                            } catch (GediException e) {
                                if (e.toString().contains("252")) {
                                    System.out.println("iCl.GEDI Exception - Senha Errada!!!!");

                                } else {
                                    System.out.println("iCl.read error: " + e);
                                }
                                e.printStackTrace();
                            }
                            if (blockInfo != null)
                                System.out.println("iCl.PollingInfo MF_BlockRead[" + String.format("%03d", i) + "]: " + arrayBytesToString(blockInfo));
                            blockInfo = null;
                        }
                        System.out.println("iCl.MF_BlockREAD: END");
                        System.out.println("iCl.");
                        System.out.println("iCl.END");
                        return;

                    } catch (GediException gedi_e_ret) {
                        System.out.println("iCl.ISO_Polling\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());


                    } catch (Exception e) {
                        System.out.println("iCl.ISO_Polling\t\t\t- FAIL - " + e.getMessage());


                    }
                }

            }

        }).start();


    }

    public boolean cl_PowerOn() {
        boolean ativado = false;
        try {
            iCl.PowerOn();
            ativado = true;
            System.out.println("iCl.PowerOn\t\t\t- OK");
        } catch (GediException gedi_e_ret) {
            System.out.println("iCl.PowerOn\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iCl.PowerOn\t\t\t- FAIL - " + e.getMessage());
        }
        return ativado;
    }

    public boolean cl_PowerOff() {
        boolean ativado = true;
        try {
            iCl.PowerOff();
            ativado = false;
            System.out.println("iCl.PowerOff\t\t\t- OK");
        } catch (GediException gedi_e_ret) {
            System.out.println("iCl.PowerOff\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println(" iCl.PowerOff\t\t\t- FAIL - " + e.getMessage());
        }
        return ativado;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String arrayBytesToString(byte[] bValues) {

        StringBuilder sbValues = new StringBuilder();
        for (byte b : bValues) {
            sbValues.append(String.format("%02X ", b).replace(" ", ""));
        }
        return sbValues.toString();
    }
}
