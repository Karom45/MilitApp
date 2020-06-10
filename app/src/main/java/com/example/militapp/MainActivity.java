package com.example.militapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    final int ABOUT_APP = 1;
    final String LOG_TAG = "myLogs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ABOUT_APP, 0, R.string.about);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == ABOUT_APP) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title);
            builder.setMessage(R.string.text);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() { // Кнопка ОК
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Отпускает диалоговое окно
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEnterClick(View View) {
        Intent intent = new Intent(this, CardsActivity.class);
        Log.d(LOG_TAG, "Вход");
        startActivity(intent);
    }

    public void onRegClick(View View) {
        Intent intent = new Intent(this, CreateCard.class);
        startActivity(intent);
    }
    public void onQuitClick(View view) {
        finish();
        }
    }

