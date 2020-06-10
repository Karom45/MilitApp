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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailedCard  extends Activity {
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
    private static String url_card_detials = "http://localhost/android/get_card_details.php";

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

                        } else {
                            // продукт с pid не найден
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }
    }
}
