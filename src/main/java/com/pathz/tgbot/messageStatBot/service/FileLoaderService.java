package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.FileDto;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;


@Service
public class FileLoaderService {

    @Value("${telegram.bot.token}")
    private String token;

    public FileLoaderService() {
    }

    @SneakyThrows
    public String downloadFile(FileDto fileDto) {
        String jsonString = getHTMLPage("https://api.telegram.org/bot" + token + "/getFile?file_id=" + fileDto.getFileId());
        if (Objects.nonNull(jsonString)) {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject result = obj.getJSONObject("result");
            String file_path = result.getString("file_path");
            String url = "https://api.telegram.org/file/bot" + token + "/" + file_path;
            File destination = new File("/home/edmvl/bot_files/" + fileDto.getFileNameForSave());
            FileUtils.copyURLToFile(
                    new URL(url),
                    destination,
                    1000,
                    100000);
            return null;
        }
        return null;
    }

    public static String getHTMLPage(String url) {
        try {
            Connection.Response execute = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("https://api.telegram.org")
                    .followRedirects(true)
                    .cookie("stel_ln", "it")
                    .ignoreContentType(true)
                    .execute();
            return execute.parse().text();
        } catch (IOException e) {
            return null;
        }
    }

}
