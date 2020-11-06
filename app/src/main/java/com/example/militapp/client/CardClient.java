package com.example.militapp.client;

import androidx.annotation.Nullable;

import com.example.militapp.dto.BaseResponse;
import com.example.militapp.dto.CardDto;
import com.example.militapp.dto.CardListResponse;
import com.example.militapp.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CardClient {

    private static final CardClient INSTANCE = new CardClient();

    private static final String HOST = "http://192.168.31.86";
    private static final String URL_ALL_CARDS = HOST + "/android/get_all_cards.php";
    private static final String URL_DELETE_CARDS = HOST + "/android/delete_card.php";
    private static final String URL_CREATE_CARD = HOST + "/android/create_card.php";
    private static final String URL_CARD_DETAILS = HOST + "/android/get_card_details.php";
    private static final String URL_UPDATE_CARD = HOST + "/android/update_card.php";


    private final ObjectMapper objectMapper = new ObjectMapper();

    private CardClient() {
    }

    public static CardClient cardClient() {
        return INSTANCE;
    }

    @Nullable
    public CardListResponse getAllCards() {
        try {
            String resp = getRawResponse(URL_ALL_CARDS, "GET", "");
            LogUtil.debug(resp);
            return objectMapper.readValue(resp, CardListResponse.class);
        } catch (Exception ex) {
            LogUtil.error("Exception while getting all cards", ex);
            return null;
        }
    }

    @Nullable
    public BaseResponse deleteCard(String id) {
        try {
            String url = URL_DELETE_CARDS + "?cards_id=" + id;
            // todo delete performs using post :(
            String resp = getRawResponse(url, "POST", "");
            LogUtil.debug(resp);
            return objectMapper.readValue(resp, BaseResponse.class);
        } catch (Exception ex) {
            LogUtil.error("Exception while getting all cards", ex);
            return null;
        }
    }

    @Nullable
    public BaseResponse createCard(CardDto cardDto) {
        try {
            String body = objectMapper.writeValueAsString(cardDto);
            String resp = getRawResponse(URL_CREATE_CARD, "POST", body);
            LogUtil.debug(resp);
            return objectMapper.readValue(resp, BaseResponse.class);
        } catch (Exception ex) {
            LogUtil.error("Exception while getting all cards", ex);
            return null;
        }
    }

    @Nullable
    public BaseResponse updateCard(CardDto cardDto) {
        try {
            String body = objectMapper.writeValueAsString(cardDto);
            String resp = getRawResponse(URL_UPDATE_CARD, "POST", body);
            LogUtil.debug(resp);
            return objectMapper.readValue(resp, BaseResponse.class);
        } catch (Exception ex) {
            LogUtil.error("Exception while getting all cards", ex);
            return null;
        }
    }

    @Nullable
    public CardListResponse getCardDetails(String id) {
        try {
            String url = URL_CARD_DETAILS + "?cards_id=" + id;
            String respBody = getRawResponse(url, "GET", "");
            LogUtil.debug(respBody);
            return objectMapper.readValue(respBody, CardListResponse.class);
        } catch (Exception ex) {
            LogUtil.error("Exception while getting all cards", ex);
            return null;
        }
    }

    private String getRawResponse(String url, String method, String requestBody)
            throws IOException {
        LogUtil.debug(url + " " + method + " " + requestBody);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);

        connection.setDoOutput(true);

        if (!method.equals("GET")) {
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(requestBody);
            }
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String curLine = reader.readLine();
            while (curLine != null) {
                stringBuilder.append(curLine);
                curLine = reader.readLine();
            }
            return stringBuilder.toString();
        }
    }
}
