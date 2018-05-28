package com.github.zalesskyi.base.summarization;

import com.github.zalesskyi.base.database.DbSchema;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Контейнер для объектов класса Word.
 */
public class Dictionary {
    private final double MIN_BASIC_IMPORTANCE_COEFF;
    private final double MAX_BASIC_IMPORTANCE_COEFF = 1;
    private final double MIN_SECONDARY_IMPORTANCE_COEFF;

    private List<Word> words;
    private List<Word> mainBasicWords;
    private List<Word> secondaryBasicWords;

    private Map<String, String> annotation;

    private int allParagraphsCount;
    private int allWordsCount;

    public Dictionary(int paragsCount, int wordsCount) {
        allParagraphsCount = paragsCount;
        allWordsCount = wordsCount;
        words = new ArrayList<>();

        MIN_BASIC_IMPORTANCE_COEFF = (double) 9 / (allParagraphsCount * allWordsCount);
        MIN_SECONDARY_IMPORTANCE_COEFF = Math.pow(0.075 *
                allParagraphsCount + 1, 2) / (allParagraphsCount * allWordsCount);
    }

    /**
     * Добавление нового слова в контейнер.
     * Если текущее слово уже имеется, то его значения
     * (общая частота, количество абзацев, номера абзацев) просто суммируются.
     *
     * @param word слово, которе необходимо добавить
     **/
    public void addWord(Word word) {
        for (Word w : words) {
            if (w.getWord().equals(word.getWord())) {
                merge(w, word);
                return;
            }
        }
        words.add(word);
    }

    /**
     * Сжатие словаря:
     *  Из словаря выбрасываются слова, которые встречаются только в одном абзаце.
     */
    public void compression() {
        if (allParagraphsCount < 2) {
            return;
        }
        for (Iterator<Word> it = words.iterator(); it.hasNext(); ) {
            Word word = it.next();
            if (word.getParagraphsCount() <= 1 || word.getWord().equals("")) {
                it.remove();
            }
        }
    }

    /**
     * Для каждого слова выделяется основа.
     * Слова с одинаковыми основами объединяются.
     */
    public void stemming() {
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < words.size(); j++) {
                if (words.get(i) != words.get(j) &&
                        words.get(i).getStem().equals(words.get(j).getStem())) {
                    merge(words.get(i), words.get(j));
                    words.remove(j);
                    j--;
                    i = i > j ? --i : i;
                }
            }
        }
    }

    /**
     * Сортировка словаря.
     * Словоформы сортируются по количеству абзацев, где встречается слово (по убыванию).
     */
    public void sort() {
        words = words.stream().sorted((word1, word2) -> {
            int hashWord1 = word1.getParagraphsCount();
            int hashWord2 = word2.getParagraphsCount();

            if (hashWord1 < hashWord2) {
                return 1;
            } else if (hashWord1 == hashWord2) {
                return 0;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());
    }

    /**
     * @return главные опорные слова текста
     */
    public List<Word> getMainBasicWords() {
        if (mainBasicWords == null) {
            mainBasicWords = new ArrayList<>();

            for (int i = 0; i < words.size() * 0.2; i++) { // todo 0.2
                mainBasicWords.add(words.get(i));
            }

            mainBasicWords = mainBasicWords.stream().filter(word -> {
                String partOfSpeech = MorphologyUtil.getPartOfSpeech(word.getWord());
                return partOfSpeech != null && partOfSpeech.equals(DbSchema.NounsTable.NAME);
            }).collect(Collectors.toList());
        }
        return mainBasicWords;
    }

    /**
     * @return второстепенные опорные слова текста.
     */
    public List<Word> getSecondaryBasicWords() {
        if (secondaryBasicWords == null) {
            secondaryBasicWords = new ArrayList<>();

            for (int i = 0; i < words.size() * 0.35; i++) {   // todo 0.35
                secondaryBasicWords.add(words.get(i));
            }
        }
        return secondaryBasicWords;
    }

    /**
     * @return размер словаря
     */
    public int size() {
        return words.size();
    }


    public Map<Integer, Set<Integer>> getKeySentencesAddresses() {
        Map<Integer, List<Integer>> bufAddresses = mergeAddresses();
        Map<Integer, Set<Integer>> keySentencesAddrs = new TreeMap<>();
        for (int parag : bufAddresses.keySet()) {
            List<Integer> sentenses = bufAddresses.get(parag);
            Set<Integer> keySentensesInThisParag = new TreeSet<>();
            for (int i = 0; i < sentenses.size(); i++) {
                int count = 0;
                for (int j = i + 1; j < sentenses.size(); j++) {
                    if (sentenses.get(i).equals(sentenses.get(j))) {
                        count++;
                    }
                }
                if (count > 2) {
                    keySentensesInThisParag.add(sentenses.get(i));
                }
            }
            if (keySentensesInThisParag.size() > 0) {
                keySentencesAddrs.put(parag, keySentensesInThisParag);
            }
        }
        return keySentencesAddrs;
    }

    @Override
    public String toString() {
        String textDict = "[";

        for (Word w : words) {
            textDict += w.toString() + "\n";
        }
        return textDict + "]";
    }


    /**
     * Слияние 2-ух слов.
     *
     * @param w1 Слово, с которым сливается 2 слово. После
     *           слития будет содержать адреса 2-ого слова тоже.
     * @param w2 Слово, которое после слития должно быть удалено.
     */
    private void merge(Word w1, Word w2) {
        for (int paragraphNum : w2.getParagraphsNums()) {
            for (int sentenceNum : w2.getSentencesNums(paragraphNum)) {
                w1.addWordAddress(paragraphNum, sentenceNum);
            }
        }
    }


    /**
     * Объединение адресов всех слов в одну структуру адресов ключевых слов.
     * Выполняется для облегчения поиска ключевых предложений.
     * Ключевое предложение - это предложение содержащее более 3 опорных слов.
     */
    private Map<Integer, List<Integer>> mergeAddresses() {
        Map<Integer, List<Integer>> addresses = new TreeMap<>();

        words.forEach(word -> {
            for (int parag : word.getParagraphsNums()) {
                if (!addresses.containsKey(parag)) {
                    addresses.put(parag, new ArrayList<>());
                }
                addresses.get(parag).addAll(word.getSentencesNums(parag));
            }
        });
        return addresses;
    }
}
