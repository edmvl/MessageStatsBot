package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class MessageFormatterTest {


    @Test
    void getUrlByDateTest() {
        String urlByDate = MessageFormatter.getUrlByDate(LocalDate.of(2022, 3, 25));
        Assertions.assertEquals("https://kakoysegodnyaprazdnik.ru/baza/mart/25", urlByDate);
    }

    @Test
    void getHTMLPageTest() {
        Document document = MessageFormatter.getHTMLPage("https://kakoysegodnyaprazdnik.ru/baza/mart/25");
        List<String> strings = document.body().select("span[itemprop='text']").eachText();
        System.out.println(strings);
    }
}