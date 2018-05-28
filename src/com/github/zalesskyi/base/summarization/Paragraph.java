package com.github.zalesskyi.base.summarization;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, иекапсулирующий данные об отдельном абзаце.
 **/
class Paragraph {
    private List<Sentence> sentences;
    private int number;

    /**
     * @param sourceParagraph исходный текст этого параграфа
     * @param preparedParagraph текст параграфа без стоповых слов
     * @param paragraphNum номер этого параграфа
     */
    public Paragraph(String sourceParagraph, String preparedParagraph, int paragraphNum) {
        number = paragraphNum;
        initSentences(sourceParagraph, preparedParagraph);
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * Разбиение абзаца на предложения.
     * Иннициализация массива предложений.
     *
     * @param sourcePar исходный параграф
     * @param preparedPar обработанный параграф (без предлогов и т.п.)
     */
    private void initSentences(String sourcePar, String preparedPar) {
        String[] sourceSentences = sourcePar.split("\\. |\\? |! ");
        String[] preparedSentences = preparedPar.split("\\. |\\? |! ");

        assert (sourceSentences.length == preparedSentences.length);

        sentences = new ArrayList<>(sourceSentences.length);
        for (int i = 0; i < sourceSentences.length; i++) {
            sentences.add(new Sentence(i, number, sourceSentences[i], preparedSentences[i]));
        }
    }
}
