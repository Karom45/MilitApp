package com.example.militapp;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.militapp.client.CardClient;
import com.example.militapp.dto.BaseResponse;
import com.example.militapp.dto.CardDto;
import com.example.militapp.dto.CardListResponse;
import com.example.militapp.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CardsActivity extends ListActivity {

    private static final String TAG_ID = "cards_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_COMPANY = "company_name";

    private static final int DELETE_ID = 1;
    private static final int UPDATE_ID = 0;

    private ArrayList<HashMap<String, String>> cardsList = new ArrayList<>();

    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        LogUtil.debug("Запуск активности");
        // Hashmap for ListView
        cardsList = new ArrayList<>();
        LogUtil.debug("Заход в функцию");
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
            String del_id = ((TextView) acmi.targetView.findViewById(R.id.pid)).getText()
                    .toString();
            new DeleteProduct().execute(del_id);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == UPDATE_ID) {
            String update_id = ((TextView) acmi.targetView.findViewById(R.id.pid)).getText()
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
     */
    class LoadAllCards extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CardsActivity.this);
            pDialog.setMessage("Загрузка. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            // получаем JSON строк с URL
            LogUtil.debug("Вход в функцию");
            CardListResponse cardListResponse = CardClient.cardClient().getAllCards();
            if (cardListResponse != null && cardListResponse.getSuccess() == 1) {
                List<CardDto> allCards = cardListResponse.getCards();
                for (CardDto cardDto : allCards) {
                    // Сохраняем каждый json елемент в переменную
                    String id = cardDto.getId();
                    String name = cardDto.getName() + " " + cardDto.getSurname();
                    String company = cardDto.getCompanyName();

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
                Intent i = new Intent(getApplicationContext(), CreateCard.class);
                // Закрытие всех предыдущие activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            return null;
        }


        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            runOnUiThread(new Runnable() {
                public void run() {
                    // Обновляем распарсенные JSON данные в ListView
                    ListAdapter adapter = new SimpleAdapter(
                            CardsActivity.this,
                            cardsList,
                            R.layout.card_item,
                            new String[]{TAG_ID, TAG_NAME, TAG_COMPANY},
                            new int[]{R.id.pid, R.id.name_sur, R.id.company});
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
            String cardId = args[0];
            BaseResponse resp = CardClient.cardClient().deleteCard(cardId);
            if (resp != null && resp.getSuccess() == 1) {
                // Продукт удачно удален
                Intent i = getIntent();
                // отправляем результирующий код 100 для уведомления об удалении продукта
                setResult(100, i);
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
