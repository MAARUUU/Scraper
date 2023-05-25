package html;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Html-страница */
public class HtmlPage {
    /** Url-адрес страницы */
    private String pageUrl;

    /**
     * Возвращает url-адрес страницы
     * @return url страницы
     */
    public String getPageUrl() {
        return pageUrl;
    }

    /** Список элементов страницы */
    private final ArrayList<HtmlElement> elements;

    /**
     * Возвращает список элементов страницы
     * @return список элементов
     */
    public ArrayList<HtmlElement> getElements() {
        return elements;
    }

    /**
     * Инициализирует новый объект html-страницы
     * @param text текст страницы
     * @see HtmlPage#HtmlPage(String, String)
     */
    public HtmlPage(String text) throws Exception {
        try {
            elements = (ArrayList<HtmlElement>) HtmlParser.Parse(text);
        }
        catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * Инициализирует новый объект html-страницы
     * @param text текст страницы
     * @param pageUrl url-адрес страницы
     * @see HtmlPage#HtmlPage(String)
     */
    public HtmlPage(String text, String pageUrl) throws Exception {
        try {
            elements = (ArrayList<HtmlElement>) HtmlParser.Parse(text);
            this.pageUrl = pageUrl;
        }
        catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * Возвращает список всех элементов страницы с указанным тэгом
     * @param tag тэг элемента
     * @return список элементов
     */
    public List<HtmlElement> getElementsByTag(String tag) {
        List<HtmlElement> answer = elements
                .stream()
                .filter(x -> x.getTag().equals(tag))
                .collect(Collectors.toList());
        return answer;
    }
}
