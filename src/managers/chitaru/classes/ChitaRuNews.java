package managers.chitaru.classes;

import managers.templates.NewsTemplate;

public class ChitaRuNews extends NewsTemplate {
    /** Ссылка на новость */
    private String url;
    /**
     * Возвращает ссылку на новость
     * @return ссылка на новость
     */
    public String getUrl() {
        return url;
    }
    /**
     * Устанавливает ссылку на новость
     * @param url ссылка
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /** Ключевые слова новости */
    private String keywords;
    /**
     * Возвращает ключевые слова новости
     * @return ключевые слова
     */
    public String getKeywords() {
        return keywords;
    }
    /**
     * Устанавливает ключевые слова новости
     * @param keywords ключевые слова
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /** Количество просмотров */
    private int views;
    /**
     * Возвращает количество просмотров новости
     * @return количество просмотров
     */
    public int getViews() {
        return views;
    }
    /**
     * Устанавливает количество просмотров новости
     * @param views количество просмотров
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Инициализирует новый объект {@link ChitaRuNews}
     * @param header заголовок новости
     */
    public ChitaRuNews(String header) {
        this.setHeader(header);
    }

    /**
     * Инициализирует новый объект {@link ChitaRuNews}
     * @param url ссылка на новость
     * @param header заголовок новости
     */
    public ChitaRuNews(String url, String header) {
        this.url = url;
        this.setHeader(header);
    }
}
