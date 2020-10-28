package com.example.militapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CardListResponse extends BaseResponse {

    @JsonProperty("cards")
    private List<CardDto> cards;

    public List<CardDto> getCards() {
        return cards;
    }

    public void setCards(List<CardDto> cards) {
        this.cards = cards;
    }
}
