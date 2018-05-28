package com.github.zalesskyi.base.summarization;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, инкапсулирующий данные об отдельном предложении.
 */
class Sentence {
    private int sentenceNumber;
    private int paragraphNumber;

    private String fullSentence;
    private List<Word> preparedWords;

    /**
     * @param sentenceNum Номер этого предложения
     * @param paragraphNum номер абзаца, в котором находится это предложение
     * @param sourceSentence исходный текст этого предложения
     * @param preparedSentence текст предложения без стоповых слов
     */
    public Sentence(int sentenceNum, int paragraphNum, String sourceSentence, String preparedSentence) {
        sentenceNumber = sentenceNum;
        paragraphNumber = paragraphNum;
        fullSentence = sourceSentence;

        initPreparedWords(preparedSentence);
    }

    /**
     * @return обработанные слова предложения
     */
    public List<Word> getWords() {
        return preparedWords;
    }


    /**
     * @return полное, необработанное предложение
     */
    public String getFullSentence() {
        return fullSentence;
    }

    /**
     * Разбиение предложения на слова.
     * Иннициализация массива слов.
     *
     * @param preparedSentence предложения, не содержащее предлогов и т.п.
     */
    private void initPreparedWords(String preparedSentence) {
        String[] words = preparedSentence.split(" ");

        preparedWords = new ArrayList<>(words.length);
        for (int i = 0; i < words.length; i++) {
            String prevWord = i > 0 ? words[i - 1] : null;
            preparedWords.add(new Word(words[i], sentenceNumber, paragraphNumber, prevWord));
        }
    }
}

// todo не понятно, откуда берется "". Разобраться.
