package com.github.zalesskyi;

import com.github.zalesskyi.base.summarization.Dictionary;
import com.github.zalesskyi.base.summarization.FullText;
import com.github.zalesskyi.base.summarization.Word;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Поток обработки текста.
 * Для каждого запроса создается свой поток обработки.
 */
public class ProcessThread implements Runnable {

    private String mSource;
    private Thread mThread;
    private HttpExchange mHttpExchange;

    public ProcessThread(String source, HttpExchange httpExchange) throws IOException {
        mSource = source;
        mHttpExchange = httpExchange;

        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void run() {
        FullText fText = new FullText(prepareData(mSource));
        Dictionary dictionary = fText.getAllWords();
        Utils.log(Integer.toString(dictionary.size()));
        dictionary.compression();
        dictionary.stemming();
        Utils.log(dictionary.toString());
        dictionary.sort();
        List<Word> mainWords = dictionary.getMainBasicWords();
        List<Word> secondaryWords = dictionary.getSecondaryBasicWords();
        Utils.log("---------------");
        mainWords.forEach(item -> Utils.log(item.toString()));
        Utils.log("---------------");
        secondaryWords.forEach(System.out::println);
        Utils.log("---------------------------------");
        Map<Integer, Set<Integer>> keySentencesAddr = dictionary.getKeySentencesAddresses();
        String keySentences = fText.getKeySentences(keySentencesAddr);
        Utils.log(keySentences);
        Utils.log(Integer.toString(keySentences.length()));

        sendData(keySentences);

        closeConnection();
    }


    /**
     * Подготовка данных, принятых от клиента.
     * @param text данные от клиента
     * @return читаемая обработчиком строка,
     * которую можно реферировать. (Абзацы отделены друг от друга метасимволами)
     */
    private String prepareData(String text) {
        return text.replaceAll("\\. ?\n", Utils.Constants.NEW_PARAGRAPH_MARKER_REGEX);
    }

    /**
     * Отправка данных клиенту.
     *
     * @param result Результат реферирования.
     */
    private void sendData(String result) {
        try (BufferedOutputStream out = new BufferedOutputStream(mHttpExchange.getResponseBody())) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(result.getBytes())) {
                mHttpExchange.sendResponseHeaders(Utils.Constants.HTTP_OK, 0);
                byte [] buffer = new byte [1024];
                int count ;
                while ((count = bis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private void closeConnection() {
        mHttpExchange.close();
    }
}
