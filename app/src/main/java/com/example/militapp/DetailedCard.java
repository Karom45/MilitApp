package com.example.militapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.militapp.client.CardClient;
import com.example.militapp.dto.CardDto;
import com.example.militapp.dto.CardListResponse;

public class DetailedCard extends Activity {
    EditText inputCompany;
    EditText inputName;
    EditText inputSurname;
    EditText inputPosition;
    EditText inputPhone;
    TextView tv;

    private static final String TAG_ID = "cards_id";

    private ProgressDialog pDialog;

    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_creation);

        inputCompany = findViewById(R.id.company_name);
        inputName = findViewById(R.id.name);
        inputSurname = findViewById(R.id.surname);
        inputPosition = findViewById(R.id.position);
        inputPhone = findViewById(R.id.phone);

        tv = findViewById(R.id.textView);
        tv.setText("Взитная карточка");
        Button resultButton = findViewById(R.id.Resultbutton);
        resultButton.setVisibility(View.GONE);

        Intent i = getIntent();

        id = i.getStringExtra(TAG_ID);

        new GetProductDetails().execute();

    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Перед началом показать в фоновом потоке прогресс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailedCard.this);
            pDialog.setMessage("Loading card details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Получение детальной информации о продукте в фоновом режиме
         **/
        protected String doInBackground(String[] params) {

            // обновляем UI форму
            runOnUiThread(new Runnable() {
                public void run() {
                    // проверяем статус success тега
                    CardListResponse resp = CardClient.cardClient().getCardDetails(id);

                    if (resp != null && resp.getSuccess() == 1) {
                        // Успешно получинна детальная информация о продукте
                        // получаем первый обьект с JSON Array
                        CardDto cardDto = resp.getCards().get(0);

                        // продукт с pid найден
                        // Edit Text
                        inputCompany = findViewById(R.id.company_name);
                        inputName = findViewById(R.id.name);
                        inputSurname = findViewById(R.id.surname);
                        inputPosition = findViewById(R.id.position);
                        inputPhone = findViewById(R.id.phone);

                        // покаываем данные о продукте в EditText
                        inputCompany.setText(cardDto.getCompanyName());
                        inputName.setText(cardDto.getName());
                        inputSurname.setText(cardDto.getSurname());
                        inputPosition.setText(cardDto.getPosition());
                        inputPhone.setText(cardDto.getPhone());
                    } else {
                        // продукт с pid не найден
                    }
                }
            });

            return null;
        }
    }
}
