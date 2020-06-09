package com.example.militapp;

import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CardsActivity extends ListActivity {

    private ProgressDialog pDialog;


    // Создаем JSON парсер
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> cardsList;


    // url получения списка всех продуктов
    private static String url_all_cards = "http://test.devcolibri.com/get_all_products.php";
    private static final String url_delete_cards = "http://test.devcolibri.com/delete_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_CARDS = "cards";
    private static final String TAG_ID = "cards_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_COMPANY = "company_name";

    private static final int DELETE_ID = 1;
    private static final int UPDATE_ID = 0;
    // тут будет хранится список продуктов
    JSONArray cards = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        // Hashmap for ListView
        cardsList = new ArrayList<>();

        new LoadAllCards().execute();

        ListView lv = getListView();
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                Intent in = new Intent(getApplicationContext(), CreateCard.class);
                in.putExtra(TAG_ID, pid);

                startActivityForResult(in, 100);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, UPDATE_ID, 0, R.string.update);
        menu.add(0, DELETE_ID, 1, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == DELETE_ID) {
            String del_id =  ((TextView) acmi.targetView.findViewById(R.id.pid)).getText()
                    .toString();
            new DeleteProduct().execute(del_id);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == UPDATE_ID){
            String update_id =  ((TextView) acmi.targetView.findViewById(R.id.pid)).getText()
                    .toString();

            Intent in = new Intent(getApplicationContext(), UpdateCard.class);
            // отправляем pid в следующий activity
            in.putExtra(TAG_ID, update_id);

            // запуская новый Activity ожидаем ответ обратно
            startActivityForResult(in, 100);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Фоновый Async Task для загрузки всех продуктов по HTTP запросу
     * */
    class LoadAllCards extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CardsActivity.this);
            pDialog.setMessage("Загрузка. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Получаем все продукт из url
         * */
        protected String doInBackground(String... args) {
            // Будет хранить параметры
            List<NameValuePair> params = new ArrayList<>();
            // получаем JSON строк с URL
            JSONObject json = jParser.makeHttpRequest(url_all_cards, "GET", params);

            Log.d("All cards: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт найден
                    // Получаем масив из Продуктов
                    cards = json.getJSONArray(TAG_CARDS);

                    // перебор всех продуктов
                    for (int i = 0; i < cards.length(); i++) {
                        JSONObject c = cards.getJSONObject(i);

                        // Сохраняем каждый json елемент в переменную
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME)  + " " + c.getString(TAG_SURNAME);
                        String company = c.getString(TAG_COMPANY);

                        // Создаем новый HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // добавляем каждый елемент в HashMap ключ => значение
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_COMPANY, company);

                        // добавляем HashList в ArrayList
                        cardsList.add(map);
                    }
                } else {
                    // продукт не найден
                    // Запускаем Add New Product Activity
                    Intent i = new Intent(getApplicationContext(),
                           CreateCard.class);
                    // Закрытие всех предыдущие activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Обновляем распарсенные JSON данные в ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            CardsActivity.this, cardsList,
                            R.layout.card_item, new String[] {TAG_ID,
                            TAG_NAME, TAG_COMPANY},
                            new int[] { R.id.pid, R.id.name_sur , R.id.company });
                    // обновляем listview
                    setListAdapter(adapter);

                }
            });

        }

    }
    class DeleteProduct extends AsyncTask<String, String, String> {


        /**
         * На начале показываем прогресс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CardsActivity.this);
            pDialog.setMessage("Удаление визитки...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Удаление продукта
         **/
        protected String doInBackground(String... args) {

            int success;
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add((NameValuePair) new BasicNameValuePair(TAG_ID, args[0]));

                // получение продукта используя HTTP запрос
                JSONObject json = jParser.makeHttpRequest(url_delete_cards, "POST", params);

                Log.d("Delete card", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Продукт удачно удален
                    Intent i = getIntent();
                    // отправляем результирующий код 100 для уведомления об удалении продукта
                    setResult(100, i);
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
