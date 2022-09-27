package com.verdemar.pdvmovel.Pagamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.verdemar.pdvmovel.R;

public class yesno extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yesno);

        String mensagem = getIntent().getStringExtra("message");
        TextView titulo = (TextView) findViewById(R.id.tituloYesNo);
        titulo.setText(mensagem);

    }
    public void sim(View v){
        Intent i = new Intent();
        i.putExtra("input", "0");
        setResult(RESULT_OK, i);
        finish();
    }
    public void nao(View v){
        Intent i = new Intent();
        i.putExtra("input", "1");
        setResult(RESULT_OK, i);
        finish();
    }
    public void cancelar(View v){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}