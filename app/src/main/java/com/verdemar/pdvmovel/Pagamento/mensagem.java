package com.verdemar.pdvmovel.Pagamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.verdemar.pdvmovel.R;

public class mensagem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem);

        TextView tv = (TextView) findViewById(R.id.tvTituloMsg);
        tv.setText(getIntent().getExtras().getString("message"));
    }
    public void btOnclick(View v){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}