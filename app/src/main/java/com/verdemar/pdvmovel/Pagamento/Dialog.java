package com.verdemar.pdvmovel.Pagamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.verdemar.pdvmovel.R;

public class Dialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        TextView tv = (TextView) findViewById(R.id.tvMsg);
        tv.setText(getIntent().getExtras().getString("message"));
    }
    public void ok(View v){
        EditText ed = (EditText) findViewById(R.id.edInput);
        Intent i = new Intent();
        i.putExtra("input", ed.getText().toString());
        setResult(RESULT_OK, i);
        finish();

    }
    public void cancelar(View v){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}