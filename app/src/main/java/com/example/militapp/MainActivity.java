package com.example.militapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onEnterClick(View View) {
        Intent intent = new Intent(this, CardsActivity.class);
        startActivity(intent);
    }

    public void onRegClick(View View) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }
    public void onQuitClick(View view) {
        finish();
        }
    }

