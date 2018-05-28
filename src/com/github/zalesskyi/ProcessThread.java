package com.github.zalesskyi;

import com.github.zalesskyi.base.summarization.Dictionary;
import com.github.zalesskyi.base.summarization.FullText;
import com.github.zalesskyi.base.summarization.Word;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Поток обработки текста.
 * Для каждого запроса создается свой поток обработки.
 */
public class ProcessThread implements Runnable {

    private ByteArrayOutputStream mByteArrayStream;
    private InputStream mReader;
    private PrintWriter mWriter;
    private Socket mSocket;

    private Thread mThread;

    public ProcessThread(Socket socket) throws IOException {
        mSocket = socket;
        mReader = mSocket.getInputStream();
        mWriter = new PrintWriter(socket.getOutputStream(), true);

        mThread = new Thread(this);
        Utils.log("New connection: " + socket.getInetAddress());
        mThread.start();
    }

    @Override
    public void run() {
        FullText fText = new FullText(prepareData(getData()));
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
     * Получение данных от клиента.
     * @return данные от клиента.
     */
    private byte[] getData() {
        byte[] buf = new byte[1024];
        int bytesRead;
        mByteArrayStream = new ByteArrayOutputStream();
        try {
            while ((bytesRead = mReader.read(buf)) > 0) {
                mByteArrayStream.write(buf, 0, bytesRead);
            }
            return mByteArrayStream.toByteArray();
        } catch (IOException exc) {
            exc.printStackTrace();
            Utils.log("Error: " + exc.getMessage());
        }
        return null;
    }

    /**
     * Подготовка данных, принятых от клиента.
     * @param data данные от клиента
     * @return читаемая обработчиком строка,
     * которую можно реферировать. (Абзацы отделены друг от друга метасимволами)
     */
    private String prepareData(byte[] data) {
        String text = new String(data);
        return text.replaceAll("\\. ?\n", Utils.Constants.NEW_PARAGRAPH_MARKER_REGEX);
    }

    /**
     * Отправка данных клиенту.
     *
     * @param data данные, которые необходимо отправить.
     */
    private void sendData(String data) {
        mWriter.println(data);
    }

    /**
     * Закрытие соединения с текущим клиентом.
     */
    private void closeConnection() {
        try {
            mSocket.close();
            mReader.close();
            mWriter.close();
            Utils.log("The connection with " + mSocket.getInetAddress() + " has been disconnected");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
