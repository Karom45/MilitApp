package com.example.militapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateCard extends AppCompatActivity {
    EditText inputCompany;
    EditText inputName;
    EditText inputSurname;
    EditText inputPosition;
    EditText inputPhone;

    private static final String TAG_COMPANY = "company_name";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_POSITION = "position";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_SUCCESS = "success";

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();


    private static String url_create_card = "http://test.devcolibri.com/create_product.php";
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
            ProgressDialog pDialog = new ProgressDialog(CreateCard.this);
            pDialog.setMessage("Создание продукта...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Создание продукта
         **/
        protected String doInBackground(String[] args) {

            String company = inputCompany.getText().toString();
            String name = inputName.getText().toString();
            String surname = inputSurname.getText().toString();
            String position = inputPosition.getText().toString();
            String phone = inputPhone.getText().toString();

            // Заполняем параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_COMPANY, company));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_SURNAME, surname));
            params.add(new BasicNameValuePair(TAG_POSITION, position));
            params.add(new BasicNameValuePair(TAG_PHONE, phone));

            // получаем JSON объект
            JSONObject json = jsonParser.makeHttpRequest(url_create_card, "POST", params);

            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт удачно создан
                    Intent i = new Intent(getApplicationContext(), CardsActivity.class);
                    startActivity(i);

                    // закрываем это окно
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * После оконачния скрываем прогресс диалог
         **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }

    }
}
