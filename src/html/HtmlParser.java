package html;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Парсер веб-страниц */
public class HtmlParser {
    /** Специальная строка для отделения html-блоков друг от друга  */
    private static final String separator = "<split>";

    /**
     * Замена всех совпадений в тексте
     * @param input исходный текст
     * @param regex регулярное выражение для замены
     * @param replacement текст замены
     * @return текст после замены
     */
    private static String Replace(String input, String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.replaceAll(replacement);
    }

    /**
     * Очистка множественных пробелов и пропусков в тексте
     * @param text исходный текст
     * @return текст без множественных пробелов и пропусков
     */
    private static String ClearWhiteSpace(String text) {
        // Поиск символов новой строки и произвольного количества пробелов
        return Replace(text, "[\\p{Zs}\\r\\n]+", " ");
    }

    /**
     * Удаление html-комментариев из текста
     * @param text исходный текст
     * @return текст без html-комментариев
     */
    private static String RemoveComments(String text) {
        // Поиск комментариев в тексте страницы
        // Так же захватываем <!DOCTYPE html> за ненадобностью
        return Replace(text, "<!.*?>", "");
    }

    /**
     * Удаление всех html-тэгов из текста по названию
     * @param text исходный текст
     * @param tag название html-тэга
     * @return текст после удаления указанных тэгов
     */
    private static String RemoveTag(String text, String tag) {
        // Поиск закрытых блоков с указанным названием,
        // открывающих и закрывающих, а так же всего текста между ними:
        // <tag arg="value" /> , <tag>value</tag>
        String regex = MessageFormat.format("<{0}.*?(/>|</{0}>)", tag);
        return Replace(text, regex, "");
    }

    /**
     * Подготовка текста веб-страницы к парсингу
     * @param text исходный текст страницы
     * @return обработанный текст
     */
    private static String PrepareText(String text) {
        // Поиск символов конца одного блока и начала другого
        return Replace(text, ">\\p{Zs}*<", String.format(">%s<", separator));
    }

    /**
     * Парсинг html-элемента {@link HtmlElement} из строки
     * @param line строка из текста веб-страницы
     * @return Объект {@link HtmlElement}
     */
    private static HtmlElement ParseElement(String line) {
        // Убираем весь текст кроме текста между первыми угловыми скобками и чистим лишние пробелы
        String block = Replace(line, "(<|\\/?>.*)", "").trim();
        // Разбиваем блок по пробелу на элементы
        String[] attributes = block.split(" ");
        if (attributes.length > 0) {
            // Первый аттрибут и есть сам тэг
            HtmlElement result = new HtmlElement(attributes[0]);
            // Все остальные аттрибуты разбиваем по символу '='
            for (int i = 1; i < attributes.length; i++) {
                String[] items = attributes[i].split("=");
                if (items.length > 1) {
                    // 0 - имя аргумента, 1 - значение (перед вставкой убираем кавычки)
                    result.getAttributes().add(new HtmlAttribute(items[0], items[1].replace("\"", "")));
                }
                else {
                    // Если не удалось разбить строку, то предполагаем, что это продолжение текста значения последнего аргумента,
                    // которое "отрезало" после разбиения по пробелу
                    if (result.getAttributes().size() > 0) {
                        // Если аргументы есть, то находим индекс последнего и дописываем в него оставшийся текст
                        int index = result.getAttributes().size() - 1;
                        String newValue = result.getAttributes().get(index).getValue() + " " + items[0];
                        result.getAttributes().get(index).setValue(newValue.replace("\"", ""));
                    }
                }
            }
            // Чтобы получить значение элемента, убираем весь текст кроме того,
            // что находится между открывающим и закрывающим тэгами
            String value = Replace(line, "<\\/?.*?>", "");
            result.setValue(value);
            return result;
        }
        return null;
    }

    /**
     * Парсинг текста веб-страницы в список {@link HtmlElement}
     * @param text исходный текст
     * @return Список {@link HtmlElement}
     */
    public static List<HtmlElement> Parse(String text) {
        String result = ClearWhiteSpace(text);
        result = RemoveComments(result);
        result = RemoveTag(result, "script");
        result = PrepareText(result);

        ArrayList<HtmlElement> blocks = new ArrayList<>();
        for (String line : result.split(separator)) {
            blocks.add(ParseElement(line));
        }

        return blocks;
    }
}
