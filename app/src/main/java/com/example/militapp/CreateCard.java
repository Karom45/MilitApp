package com.example.militapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.militapp.client.CardClient;
import com.example.militapp.dto.BaseResponse;
import com.example.militapp.dto.CardDto;

public class CreateCard extends AppCompatActivity {
    EditText inputCompany;
    EditText inputName;
    EditText inputSurname;
    EditText inputPosition;
    EditText inputPhone;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_creation);

        inputCompany = findViewById(R.id.company_name);
        inputName = findViewById(R.id.name);
        inputSurname = findViewById(R.id.surname);
        inputPosition = findViewById(R.id.position);
        inputPhone = findViewById(R.id.phone);
        Button resultButton = findViewById(R.id.Resultbutton);
        resultButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new CreateNewProduct().execute();
            }
        });
    }


    class CreateNewProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateCard.this);
            pDialog.setMessage("Создание продукта...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Создание продукта
         **/
        protected String doInBackground(String[] args) {
            CardDto cardDto = getCard();
            BaseResponse resp = CardClient.cardClient().createCard(cardDto);

            if (resp != null && resp.getSuccess() == 1) {
                // продукт удачно создан
                Intent i = new Intent(getApplicationContext(), CardsActivity.class);
                startActivity(i);

                // закрываем это окно
                finish();
            }

            return null;
        }

        private CardDto getCard() {
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
            return cardDto;
        }

        /**
         * После оконачния скрываем прогресс диалог
         **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }

    }
}
