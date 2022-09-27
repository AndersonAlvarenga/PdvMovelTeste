package com.verdemar.pdvmovel.Pagamento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.verdemar.pdvmovel.R;

import br.com.softwareexpress.sitef.android.CliSiTefI;

public class transacion extends Activity {

    private static final int CAMPO_COMPROVANTE_CLIENTE = 121;
    private static final int CAMPO_COMPROVANTE_ESTAB = 122;

    private class RequestCode {
        private static final int VALOR = 0;
        private static final int COLETA = 1;
    }

    ;

    // Variaveis estaticas para nao serem reinicializadas ao rodar o display
    // Para tanto, vamos assumir que esta atividade nunca será executada em
    // paralelo com outra igual (singleton)

    private static boolean rodando = false;
    private static boolean espera = true;
    private static String mensagemVisor;
    private static String valor;
    private static int modalidade;
    private static CliSiTefI clisitef;
    private static Thread processoI = null;
    private static transacion instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacion);instance = this;
        clisitef = CliSiTefI.getInstance();
        clisitef.setMessageHandler(hndMessage);
        clisitef.setActivity(this);
        if (processoI == null) {
            modalidade = getIntent().getExtras().getInt("modalidade");
            // A definição do valor da transação depende da aplicação.
            // No caso deste exemplo , o valor é sorteado.
            String valorTrn = "" + (100 + System.currentTimeMillis() % 100);
            executaTrn(modalidade, valorTrn, "");
        }
    }

    private void setStatus(String s) {
        ((TextView) findViewById(R.id.tvStatusTrn)).setText(s);
        Log.d("SetStatus", "Status: " + s);
    }

    private void alert(String message) {
        if (message != null && message.length() > 0) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.d("Alert", "Alert: " + message);
        }
    }

    private static Handler hndErro = new Handler() {
        public void handleMessage(android.os.Message message) {
            instance.setStatus("Erro: " + message.what);
        }
    };

    private static Handler hndComando = new Handler() {
        public void handleMessage(android.os.Message message) {
            instance.setTitle(""); // Só para limpar eventuais mensagens de conexao do pinpad
            if (message.what == CliSiTefI.CMD_RETORNO_VALOR)
                instance.RotinaResultado(clisitef.getTipoCampo());
            else
                instance.RotinaColeta(message.what);
        }
    };

    // Aborta eventual processo interativo pendente anterior
    public void cancelaProcesso() {
        if (processoI != null) {
            rodando = false;
            synchronized (processoI) {
                espera = false;
                processoI.notifyAll();
            }
        }
    }

    private void executaTrn(int funcao, String valor, String restricoes) {
        mensagemVisor = "";
        setStatus("");
        cancelaProcesso();
        int sts = clisitef.iniciaFuncaoSiTefInterativo(funcao, valor, "123456", "20120514",
                "120000", "Operador1", restricoes);
        if (sts == 10000) {
            processoI = new Thread() {
                // Aguarda até o fim de uma atividade de coleta
                private void esperaFimColeta() throws InterruptedException {
                    synchronized (this) {
                        while (espera)
                            wait();
                    }
                }

                public void run() {
                    try {
                        int sts;
                        int proximoComando;

                        rodando = true;
                        do {
                            sts = clisitef.continuaFuncaoSiTefInterativo();
                            if (sts == 10000) {
                                proximoComando = clisitef.getProximoComando();
                                espera = true;
                                hndComando.sendEmptyMessage(proximoComando);
                                esperaFimColeta();
                            }
                        }
                        while (rodando && sts == 10000);

                        if (sts == 0) {
                            // deve confirmar (ou nao) a transacao
                            sts = clisitef.finalizaTransacaoSiTefInterativoEx(1, "123456", "20120514", "120000", "");
                            while (rodando && sts == 10000) {
                                sts = clisitef.continuaFuncaoSiTefInterativo();
                                if (sts == 10000) {
                                    proximoComando = clisitef.getProximoComando();
                                    espera = true;
                                    hndComando.sendEmptyMessage(proximoComando);
                                    esperaFimColeta();
                                }
                            }
                        } else {
                            hndErro.sendEmptyMessage(sts);
                        }
                    } catch (Exception e) {
                        Log.e("executaTrnException", "ExecutaTrn: " + e.getMessage());
                    }
                    processoI = null;
                    finish();
                }
            };
            processoI.start();
        } else {
            finish();
        }
    }

    private void RotinaColeta(int comando) {
        Log.d("RotinaColeta", "RotinaColeta: comando " + comando + ".");
        switch (comando) {
            case CliSiTefI.CMD_MENSAGEM_OPERADOR:
            case CliSiTefI.CMD_MENSAGEM_CLIENTE:
            case CliSiTefI.CMD_MENSAGEM:
                setStatus(clisitef.getBuffer());
                break;
            case CliSiTefI.CMD_TITULO_MENU:
            case CliSiTefI.CMD_EXIBE_CABECALHO:
                mensagemVisor = clisitef.getBuffer();
                break;
            case CliSiTefI.CMD_REMOVE_MENSAGEM_OPERADOR:
            case CliSiTefI.CMD_REMOVE_MENSAGEM_CLIENTE:
            case CliSiTefI.CMD_REMOVE_MENSAGEM:
            case CliSiTefI.CMD_REMOVE_TITULO_MENU:
            case CliSiTefI.CMD_REMOVE_CABECALHO:
                mensagemVisor = "";
                setStatus("");
                break;
            case 19:
            case CliSiTefI.CMD_CONFIRMA_CANCELA: {
                Intent i = new Intent(getApplicationContext(), yesno.class);
                i.putExtra("mensagemVisor", mensagemVisor);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, transacion.RequestCode.COLETA);
                return;
            }
            case CliSiTefI.CMD_OBTEM_CAMPO:
            case CliSiTefI.CMD_OBTEM_VALOR: {
                Intent i = new Intent(getApplicationContext(), Dialog.class);
                i.putExtra("mensagemVisor", mensagemVisor);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, transacion.RequestCode.COLETA);
                return;
            }
            case CliSiTefI.CMD_SELECIONA_MENU: {
                Intent i = new Intent(getApplicationContext(), Itens.class);
                i.putExtra("mensagemVisor", mensagemVisor);
                i.putExtra("message", clisitef.getBuffer());
                startActivityForResult(i, transacion.RequestCode.COLETA);
                return;
            }
            case CliSiTefI.CMD_OBTEM_QUALQUER_TECLA:
            case CliSiTefI.CMD_PERGUNTA_SE_INTERROMPE:
                alert(clisitef.getBuffer());
                break;
            default:
                Log.d("CMD_PERGUNTA_SE_INTERROMPE", "RotinaColeta: comando " + comando + " não tratado.");
                break;
        }
        synchronized (processoI) {
            espera = false;
            processoI.notifyAll();
        }
    }

    private void RotinaResultado(int campo) {
        Log.d("RotinaResultado", "RotinaResultado: campo " + campo + ".");
        switch (campo) {
            case CAMPO_COMPROVANTE_CLIENTE:
            case CAMPO_COMPROVANTE_ESTAB:
                alert(clisitef.getBuffer());
                break;
            default:
                Log.d("RotinaResultadoDefault", "RotinaResultado: campo " + campo + " não tratado.");
                break;
        }
        synchronized (processoI) {
            espera = false;
            processoI.notifyAll();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == transacion.RequestCode.COLETA) {
            if (resultCode == RESULT_OK) {
                String in = data.getExtras().getString("input");
                clisitef.setBuffer(in);
            } else if (resultCode == RESULT_CANCELED) {
                clisitef.setContinuaNavegacao(-1);
            }
            synchronized (processoI) {
                espera = false;
                processoI.notifyAll();
            }
        } else if (requestCode == transacion.RequestCode.VALOR) {
            if (resultCode == RESULT_OK) {
                valor = data.getExtras().getString("valor");
                executaTrn(modalidade, valor, "");
            } else if (requestCode == RESULT_CANCELED) {
                finish();
            }
        }
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
}
