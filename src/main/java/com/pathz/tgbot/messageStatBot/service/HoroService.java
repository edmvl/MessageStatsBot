package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Horo;
import com.pathz.tgbot.messageStatBot.repo.HoroRepository;
import com.pathz.tgbot.messageStatBot.util.HoroscopeEnum;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class HoroService {

    private final HoroRepository horoRepository;

    public HoroService(HoroRepository horoRepository) {
        this.horoRepository = horoRepository;
    }

    public void grubDataFromResource() {
        LocalDate now = LocalDate.now();
        Arrays.stream(HoroscopeEnum.values())
                .map(sign -> parseHoro(sign.getSysname()))
                .forEach(data -> horoRepository.save(Horo.builder()
                        .date(now)
                        .sign(data.getFirst())
                        .text(data.getSecond())
                        .build()));
    }

    public String getHoroToDate(LocalDate date) {
        HoroscopeEnum[] values = HoroscopeEnum.values();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("\n")
                .append("Гороскоп на сегодня")
                .append("\n")
                .append("---------------------")
                .append("\n");
        for (HoroscopeEnum value : values) {
            Optional<Horo> horoByDateAndSign = getHoroByDateAndSign(date, value.getSysname());
            if (horoByDateAndSign.isPresent()) {
                Horo horo = horoByDateAndSign.get();
                stringBuilder
                        .append(HoroscopeEnum.bySysname(horo.getSign()).getName())
                        .append("\n")
                        .append(horo.getText())
                        .append("\n")
                        .append("--------------------")
                        .append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private Optional<Horo> getHoroByDateAndSign(LocalDate date, String sysname) {
        return horoRepository.getAllByDateAndSign(date, sysname);
    }

    @SneakyThrows
    private Pair<String, String> parseHoro(String sign) {
        String url = "https://horo.mail.ru/prediction/" + sign + "/today/";
        Document document = getHTMLPage(url);
        while (Objects.isNull(document)) {
            Thread.sleep(10000);
            document = getHTMLPage(url);
        }
        String text = document.body().select(".article__item").text();
        return Pair.of(sign, text);
    }

    private Document getHTMLPage(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .execute()
                    .parse();
        } catch (IOException e) {
            return null;
        }
    }
}
