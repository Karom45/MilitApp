package com.example.militapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CalculatingActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculating);
        Button resultButton = (Button)findViewById(R.id.Resultbutton);
        resultButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText firstParam = (EditText)findViewById(R.id.editText1);
        EditText secondParam = (EditText)findViewById(R.id.editText2);
        EditText thirdParam = (EditText)findViewById(R.id.editText3);
        TextView resultText = (TextView)findViewById(R.id.resultView);
        if (view.getId() == R.id.Resultbutton){
            int  f1 = Integer.parseInt("" + firstParam.getText());
            int  f2 = Integer.parseInt("" + secondParam.getText());;
            int  f3 = Integer.parseInt("" + thirdParam.getText());;
            double result = 3 * f1 + f2 * f3;
            resultText.setText("" + result);
            resultText.setVisibility(View.VISIBLE);
        }
    }
}
