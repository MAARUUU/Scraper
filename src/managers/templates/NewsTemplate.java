package managers.templates;

/**
 * Шаблон новости
 */
public abstract class NewsTemplate {
    /** Заголовок новости */
    private String header;

    /**
     * Возвращает заголовок новости
     * @return заголовок новости
     */
    public String getHeader() {
        return header;
    }

    /**
     * Устанавливает заголовок новости
     * @param header заголовок нововости
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /** Текст новости */
    private String text;

    /**
     * Возвращает текст новости
     * @return текст новости
     */
    public String getText() {
        return text;
    }

    /**
     * Устанавливает текст новости
     * @param text текст новости
     */
    public void setText(String text) {
        this.text = text;
    }


    public NewsTemplate() {
    }
}
