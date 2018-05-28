package com.github.zalesskyi.base.summarization;

import com.sun.istack.internal.Nullable;

import java.util.*;

/**
 * Класс, хранящий информацию о каждом конкретном слове.
 */
public class Word {
    private String word;                                       // словоформа
    private volatile String parentWord = null;                 // родительская словоформа слова
    private String stem = null;                                // основа слова (получается с помощью алгоритма Портера).
    private int totalFreq = 0;                                 // общая частота в тексте
    private Map<Integer, Set<Integer>> wordAddresses;          // Адрес слова. (key: номер абзаца, value: массив номеров предложений)
    private double importanceCoeff = 0;                        // Коэффициент важности
    private String previousWord;

    Word(String w, int sentenceNum, int paragraphNum, String prevWord) {
        word = w;
        previousWord = prevWord;
        wordAddresses = new HashMap<>();                          // Иннициализация адреса слова
        totalFreq ++;
        Set<Integer> sentencesNums = new TreeSet<>();
        sentencesNums.add(sentenceNum);
        wordAddresses.put(paragraphNum, sentencesNums);
    }

    public void setTotalFreq(int totalFreq) {
        this.totalFreq = totalFreq;
    }

    public String getWord() {
        return word;
    }

    /**
     * @return общая частота слова в тексте
     */
    public int getTotalFreq() {
        return totalFreq;
    }

    /**
     * @return основа текущего слова
     */
    public String getStem() {
        if (stem == null) {
            stem = new PartOfSpeechHelper().stem(word);
        }
        return stem;
    }

    /**
     * @return Количество абзацев, где астречается это слово.
     */
    public int getParagraphsCount() {
        return wordAddresses.size();
    }

    /**
     * @return номера абзацев, где встречается слово
     */
    public Set<Integer> getParagraphsNums() {
        return wordAddresses.keySet();
    }

    /**
     * Получение номеров предложений в конкретном абзаце, где встречается слово.
     *
     * @param paragraphNum номер конкретного абзаца.
     * @return номера предложений
     */
    public Set<Integer> getSentencesNums(int paragraphNum) {
        return wordAddresses.get(paragraphNum);
    }

    /**
     * Добавление адреса, где встречается это слово.
     *
     * @param pn номер абзаца
     * @param sn номер предложения
     */
    public void addWordAddress(int pn, int sn) {
        if (wordAddresses.containsKey(pn)) {
            wordAddresses.get(pn).add(sn);
        } else {
            Set<Integer> sentencesNums = new TreeSet<>();
            sentencesNums.add(sn);
            wordAddresses.put(pn, sentencesNums);
        }
        totalFreq++;
    }

    /**
     * @param paragraphsCount общее число абзацев в тексте
     * @param wordsCount общее количество слов в тексте
     *
     * @return коэффициент важности словоформы
     */
    public double getImportanceCoefficient(int paragraphsCount, int wordsCount) {
        if (importanceCoeff == 0) {
            importanceCoeff = (double) (totalFreq * getParagraphsCount())
                    / (wordsCount * paragraphsCount);
        }
        return importanceCoeff;
    }

    @Override
    public String toString() {
        return "word: \"" + word + "\""
                + " total frequency: " + totalFreq
                + " paragraphs count: " + getParagraphsCount()
                + " wordAddresses: " + wordAddresses
                + " parent word: " + parentWord
                + " stem: " + getStem() + "\n";
    }
}
