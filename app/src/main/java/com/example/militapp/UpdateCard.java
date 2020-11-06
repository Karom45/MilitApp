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
import com.example.militapp.dto.BaseResponse;
import com.example.militapp.dto.CardDto;
import com.example.militapp.dto.CardListResponse;

public class UpdateCard extends Activity {

    private static final String TAG_ID = "cards_id";

    private ProgressDialog pDialog;
    private EditText inputCompany;
    private EditText inputName;
    private EditText inputSurname;
    private EditText inputPosition;
    private EditText inputPhone;

    private String id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_creation);

        inputCompany = findViewById(R.id.company_name);
        inputName = findViewById(R.id.name);
        inputSurname = findViewById(R.id.surname);
        inputPosition = findViewById(R.id.position);
        inputPhone = findViewById(R.id.phone);

        TextView tv = findViewById(R.id.textView);
        tv.setText("Изменение визитной карточки");
        Button resultButton = findViewById(R.id.Resultbutton);
        resultButton.setText(R.string.update);

        Intent i = getIntent();

        id = i.getStringExtra(TAG_ID);

        new GetProductDetails().execute();

        resultButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new SaveProductDetails().execute();
            }
        });


    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Перед началом показать в фоновом потоке прогресс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateCard.this);
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

        /**
         * После завершения фоновой задачи закрываем диалог прогресс
         **/
        protected void onPostExecute(String file_url) {
            // закрываем диалог прогресс
            pDialog.dismiss();
        }
    }

    /**
     * В фоновом режиме выполняем асинхроную задачу на сохранение продукта
     **/
    class SaveProductDetails extends AsyncTask<String, String, String> {

        /**
         * Перед началом показываем в фоновом потоке прогрксс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateCard.this);
            pDialog.setMessage("Saving product ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Сохраняем продукт
         **/
        protected String doInBackground(String[] args) {
            CardDto cardDto = getCard();
            BaseResponse resp = CardClient.cardClient().updateCard(cardDto);

            if (resp != null && resp.getSuccess() == 1) {
                // продукт удачно обнавлён
                Intent i = getIntent();
                // отправляем результирующий код 100 чтобы сообщить об обновлении продукта
                setResult(100, i);
                finish();
            } else {
                // продукт не обновлен
            }

            return null;
        }

        private CardDto getCard() {
            // получаем обновленные данные с EditTexts
            String company = inputCompany.getText().toString();
            String name = inputName.getText().toString();
            String surname = inputSurname.getText().toString();
            String position = inputPosition.getText().toString();
            String phone = inputPhone.getText().toString();

            CardDto cardDto = new CardDto();
            cardDto.setCompanyName(company);
            cardDto.setName(name);
            cardDto.setSurname(surname);
            cardDto.setPhone(phone);
            cardDto.setPosition(position);
            cardDto.setId(id);
            return cardDto;
        }

        /**
         * После окончания закрываем прогресс диалог
         **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            pDialog.dismiss();
        }
    }
}
