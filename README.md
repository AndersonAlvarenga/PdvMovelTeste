# PdvMovelTeste

Aplicação teste desenvolvida para GPOS 700X, apresentando resultado null na chamada 
  
pollingInfo[0] = iCl.ISO_Polling(5 * 1000);
System.out.println("iCl.ISO_Polling\t\t\t- OK");
System.out.printf("iCl.ISO_Polling - peType: %s\n", pollingInfo[0].peType);
Log.e("Polling","iCl.ISO_Polling - peType: %s\n"+ pollingInfo[0].peType);

byte[] abUID = pollingInfo[0].abUID;
