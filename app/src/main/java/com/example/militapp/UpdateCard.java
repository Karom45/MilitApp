package com.example.militapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateCard extends Activity {
    EditText inputCompany;
    EditText inputName;
    EditText inputSurname;
    EditText inputPosition;
    EditText inputPhone;
    TextView tv;

    private static final String TAG_COMPANY = "company_name";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_POSITION = "position";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ID = "cards_id";
    private static final String TAG_CARDS = "cards";

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    String id;

    private static String url_update_card = "http://test.devcolibri.com/create_product.php";
    private static String url_card_detials = "http://test.devcolibri.com/create_product.php";
    
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
                    // проверяем статус success тега
                    int success;
                    try {
                        // Список параметров
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("pid", id));

                        // получаем продукт по HTTP запросу
                        JSONObject json = jsonParser.makeHttpRequest(url_card_detials, "GET", params);

                        Log.d("Single Product Details", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // Успешно получинна детальная информация о продукте
                            JSONArray cardsObj = json.getJSONArray(TAG_CARDS);

                            // получаем первый обьект с JSON Array
                            JSONObject cards = cardsObj.getJSONObject(0);

                            // продукт с pid найден
                            // Edit Text
                            inputCompany = findViewById(R.id.company_name);
                            inputName = findViewById(R.id.name);
                            inputSurname = findViewById(R.id.surname);
                            inputPosition = findViewById(R.id.position);
                            inputPhone = findViewById(R.id.phone);

                            // покаываем данные о продукте в EditText
                            inputCompany.setText(cards.getString(TAG_COMPANY));
                            inputName.setText(cards.getString(TAG_NAME));
                            inputSurname.setText(cards.getString(TAG_SURNAME));
                            inputPosition.setText(cards.getString(TAG_POSITION));
                            inputPhone.setText(cards.getString(TAG_PHONE));

                        }else{
                            // продукт с pid не найден
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

            // получаем обновленные данные с EditTexts
            String company = inputCompany.getText().toString();
            String name = inputName.getText().toString();
            String surname = inputSurname.getText().toString();
            String position = inputPosition.getText().toString();
            String phone = inputPhone.getText().toString();

            // формируем параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_ID, id));
            params.add(new BasicNameValuePair(TAG_COMPANY, company));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_SURNAME, surname));
            params.add(new BasicNameValuePair(TAG_POSITION, position));
            params.add(new BasicNameValuePair(TAG_PHONE, phone));

            // отправляем измененные данные через http запрос
            JSONObject json = jsonParser.makeHttpRequest(url_update_card, "POST", params);

            // проверяем json success тег
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт удачно обнавлён
                    Intent i = getIntent();
                    // отправляем результирующий код 100 чтобы сообщить об обновлении продукта
                    setResult(100, i);
                    finish();
                } else {
                    // продукт не обновлен
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
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
