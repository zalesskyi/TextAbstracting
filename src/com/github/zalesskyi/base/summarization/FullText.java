package com.github.zalesskyi.base.summarization;

import com.github.zalesskyi.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FullText {
    private String sourceText;       // исходный текст (без изменений)
    private String preparedText;     // обработанный текст (без предлогов, числ, знаков пунктуации и т.д.)

    private List<Paragraph> paragraphs;

    public FullText(String text) {
        Utils.log(Integer.toString(text.length()));
        sourceText = text;
        preparedText = prepareText(text);
        initParagraphs();
    }


    /**
     * @return все обработанные слова
     */
    public Dictionary getAllWords() {
        Dictionary words = new Dictionary(paragraphs.size(),
                MorphologyUtil.getWordsCountOf(sourceText));
        paragraphs.forEach(paragraph ->
           paragraph.getSentences().forEach(sentence -> {
               sentence.getWords().forEach(words::addWord);
           })
        );
        return words;
    }


    public String getKeySentences(Map<Integer, Set<Integer>> addresses) {
        String keyText = "";
        for (Map.Entry<Integer, Set<Integer>> entry : addresses.entrySet()) {
            keyText += "\t";
            for (int i : entry.getValue()) {
                keyText += paragraphs.get(entry.getKey()).getSentences().get(i).getFullSentence() + ". ";
            }
            keyText += "\n";
        }
        return keyText;
    }

    /**
     * Обработка исходного текста.
     * Включает в себя:
     * todo удалить запятые
     *  1) Удаление предлогов
     *  2) Удаление частиц
     *  3) Удаление местоимений
     *  4) Удаление наречий
     *  5) Удаление числительных
     *  6) Удаление вводных слов
     *  7) Удаление союзов
     *
     * @param text исходный текст
     * @return обработанный текст
     */
    private String prepareText(String text) {
        text = MorphologyUtil.removeStopWordsFrom(text.toLowerCase());
        return text;
    }

    /**
     * Разбиение текста на абзацы.
     * Инициализация массива абзацев.
     */
    private void initParagraphs() {
        String[] sourceParags = sourceText.split(Utils.Constants.NEW_PARAGRAPH_MARKER_REGEX);
        String[] preparedParags = preparedText.split(Utils.Constants.NEW_PARAGRAPH_MARKER_REGEX);

        assert sourceParags.length == preparedParags.length;

        paragraphs = new ArrayList<>(sourceParags.length);

        for (int i = 0; i < sourceParags.length; i++) {
            paragraphs.add(new Paragraph(sourceParags[i], preparedParags[i], i));
        }
    }
}
