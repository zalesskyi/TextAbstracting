package com.github.zalesskyi.base.summarization;

import com.sun.istack.internal.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс, помогающий в работе с частями речи:
 *  1) Приблизительно определяет часть речи слова.
 *  2) Реализует алгоритм стемминга Портера.
 *     http://snowball.tartarus.org/algorithms/russian/stemmer.html
 */
public class PartOfSpeechHelper {

    private static final class StemmingRegex {
        private static final Pattern PERFECTIVE_GERUND = Pattern.compile("((ив|ивши|ившись|ыв|ывши|ывшись)|((?<=[ая])(в|вши|вшись)))$");
        private static final Pattern REFLEXIVE = Pattern.compile("(с[яь])$");
        private static final Pattern ADJECTIVE = Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$");
        private static final Pattern PARTICIPLE = Pattern.compile("((ивш|ывш|ующ)|(([а|я])(ем|нн|вш|ющ|щ)))$");
        private static final Pattern VERB = Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|(([а|я])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");
        private static final Pattern NOUN = Pattern.compile("(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях|ы|ь|ию|ью|ю|ия|ья|я)$");
        private static final Pattern RVRE = Pattern.compile("^(.*?[аеиоуыэюя])(.*)$");
        private static final Pattern DERIVATIONAL = Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");
        private static final Pattern DER = Pattern.compile("ость?$");
        private static final Pattern SUPERLATIVE = Pattern.compile("(ейше|ейш)$");

        private static final Pattern I = Pattern.compile("и$");
        private static final Pattern SOFT_SIGN = Pattern.compile("ь$");
        private static final Pattern NN = Pattern.compile("нн$");
    }

    private static final class PartOfSpeechRegex {
        private static final Pattern PERFECTIVE_GERUND = Pattern.compile("((ив|ивши|ившись|ыв|ывши|ывшись)|((?<=[ая])(в|вши|вшись)))$");
        private static final Pattern REFLEXIVE = Pattern.compile("(с[яь])$");
        private static final Pattern ADJECTIVE = Pattern.compile("(.(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|(ист(а|ы|о|у)??)|их|ых|ую|юю|ая|яя|ою|ею|ьи|ья|ье|(ив(а|ы|о|у)??)|ат))$|.(?ui:[аеёиоуыэюяь])??(н|нн)(?ui:[аеёиоуыэюя])??$");
        private static final Pattern PARTICIPLE = Pattern.compile("((ивш|ывш|ующ)|(([а|я|е])(ем|нн|вш|ющ|щ)))");
        private static final Pattern VERB = Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|(([а|я])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");
        private static final Pattern NOUN = Pattern.compile("(.(ев|ов|ье|к|ч|иями|ист(а|ы|о|у)??|ями|ами|еи|ж|с|р|од|от|як|иям|ям|ием|з|ах|иях|ях|ы|ия|ья|ек|ец|изм|ик|иц|во|ар|ец|ор|ив|оид|ок|оз|вед|фил|фоб|яд|ин|ек|ист|он|аж|ес|ис|оп|ог|ан|ад|ык|(ф(a|о|ы)??))$)|(.(?ui:[бвгджзйклмнпрстфхцчшщ]){2,}$)");
        private static final Pattern DERIVATIONAL = Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");
        private static final Pattern SUPERLATIVE = Pattern.compile("(ейше|ейш)$");
    }

    /**
     * Определение, является ли слово прилагательным.
     *
     * @param word слово
     * @return значение, определяющее слово.
     */
    public static boolean isAdjective(String word) {
        return PartOfSpeechRegex.ADJECTIVE.matcher(word).find() || PartOfSpeechRegex.SUPERLATIVE.matcher(word).find();
    }


    /**
     * Определение, является ли слово существительным.
     *
     * @param word слово
     * @return значение, определяющее слово
     */
    public static boolean isNoun(String word) {
        return PartOfSpeechRegex.NOUN.matcher(word).find() || PartOfSpeechRegex.DERIVATIONAL.matcher(word).find();
    }


    public static boolean isVerb(String word) {
        return PartOfSpeechRegex.VERB.matcher(word).find() || PartOfSpeechRegex.PERFECTIVE_GERUND.matcher(word).find() || PartOfSpeechRegex.REFLEXIVE.matcher(word).find();
    }

    public static boolean isParticiple(String word) {
        return PartOfSpeechRegex.PARTICIPLE.matcher(word).find();
    }


    /**
     * Реализация алгоритма стеммига Портера.
     *
     * @param word слово
     * @return основа слова
     */
    public String stem(String word) {
        word = wordPrepare(word);
        Matcher matcher = StemmingRegex.RVRE.matcher(word);
        String pre = "";
        String rv = "";

        if (matcher.find()) {
            pre = matcher.group(1);
            rv = matcher.group(2);
        }

        String stem = step4(step3(step2(step1(rv))));

        return pre + stem;
    }

    /**
     * Перевод слова в нижний регистр, замена всех символов 'ё' на 'е'.
     *
     * @param oldWord слово, которое необходимо отредактировать
     * @return отредактированное слово
     */
    private String wordPrepare(String oldWord) {
        return oldWord.toLowerCase().replace('ё', 'е');
    }


    /**
     * Шаг 1:
     *  1) Поиск PERFECTIVE_GERUND-окончания в слове.
     *     Если найдено - удаляем. Завершение шага 1.
     *  2) Поиск REFLEXIVE-окончания.
     *     Если найдено - удаляем.
     *  3) Поиск ADJECTIVE-окончания в слове.
     *     Если найдено - удаляем. Завершение шага 1.
     *  4) Поиск VERB-окончания в слове.
     *     Если найдено - удаляем. Завершение шага 1.
     *  5) Поиск NOUN-окончания в слове.
     *     Если найдено - удаляем. Завершение шага 1.
     *
     * @param word слово
     * @return слово без окончания, если окончание было найдено.
     *         Если окончание не было найдено, возвращается неизмененное слово.
     */
    private String step1(String word) {
        String wordWithoutSuffix;

        if (!(wordWithoutSuffix = deleteMatchSubstring(StemmingRegex.PERFECTIVE_GERUND, word)).equals(word)) {
            return wordWithoutSuffix;
        }

        word = deleteMatchSubstring(StemmingRegex.REFLEXIVE, word);

        if (!(wordWithoutSuffix = deleteMatchSubstring(StemmingRegex.ADJECTIVE, word)).equals(word)) {
            return wordWithoutSuffix;
        }

        if (!(wordWithoutSuffix = deleteMatchSubstring(StemmingRegex.VERB, word)).equals(word)) {
            return wordWithoutSuffix;
        }

        if (!(wordWithoutSuffix = deleteMatchSubstring(StemmingRegex.NOUN, word)).equals(word)) {
            return wordWithoutSuffix;
        }

        return word;
    }

    /**
     * Шаг 2:
     *  Если слово оканчивается на "и", то удаляем "и".
     *
     * @param word слово
     * @return Если совпадение присутствует, возвращается слово без "и".
     *         Если совпадения нет, то возвращается неизмененное слово.
     */
    private String step2(String word) {
        return deleteMatchSubstring(StemmingRegex.I, word);
    }


    /**
     * Шаг 3:
     *  Если в R2 присутствует DERIVOTIONAL-окончание, то удаляем его.
     *
     * @param word слово
     * @return Если DERIVOTIONAL-окончание присутствует, то возвращается слово без окончания.
     *         Если окончания нет, то возвращается исходное слово.
     */
    private String step3(String word) {
        if (StemmingRegex.DERIVATIONAL.matcher(word).find()) {
            return deleteMatchSubstring(StemmingRegex.DER, word);
        }
        return word;
    }

    /**
     * Шаг 4:
     *  1) Если слово заканчивается на "ь", то удаляем "ь".
     *  2) Иначе, удаляем SUPERLATIVE-окончание и заменяем "нн" на "н".
     *
     * @param word слово
     * @return измененное слово
     */
    private String step4(String word) {
        String newWord = deleteMatchSubstring(StemmingRegex.SOFT_SIGN, word);
        if (!word.equals(newWord)) {
            return newWord;
        }
        newWord = deleteMatchSubstring(StemmingRegex.SUPERLATIVE, newWord);
        newWord = replaceMatchSubstringWith("н", StemmingRegex.NN, newWord);

        return newWord;
    }


    /**
     * Метод, удаляющий подстроку, совпадающую с заданным шаблоном.
     *
     * @param regex шаблон
     * @param word Строка
     * @return Если совпадение есть, возврвщается строка без подстроки, совпадающей с шаблоном
     *         Если совпадения нет, возвращается текущая строка
     */
    private String deleteMatchSubstring(Pattern regex, @NotNull String word) {
        Matcher m = regex.matcher(word);
        if (m.find()) {
            return word.replace(word
                    .substring(m.start(), m.end()), "");
        } else {
            return word;
        }
    }

    /**
     * Метод, заменяющий подстроку, совпадающую с заданным шаблоном на новую подстроку.
     *
     * @param newSubstring Новая подстрока
     * @param regex шаблон
     * @param word слово
     *
     * @return Если совпадение присутсвует, то возвращается строка с новой подстрокой.
     *         Если совпадения не найдено, то возвращается старая строка.
     */
    private String replaceMatchSubstringWith(String newSubstring, Pattern regex, @NotNull String word) {
        Matcher m = regex.matcher(word);
        if (m.find()) {
            return word.replace(word
                    .substring(m.start(), m.end()), newSubstring);
        } else {
            return word;
        }
    }
}
