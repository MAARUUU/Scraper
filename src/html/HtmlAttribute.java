package html;

/** Аргумент html-элемента */
public class HtmlAttribute {
    /** Имя аргумента */
    private final String name;
    /**
     * Возвращает значение поля {@link HtmlAttribute#name}
     * @return название аргумента
     */
    public String getName() {
        return name;
    }

    /** Значение аргумента */
    private String value;
    /**
     * Возвращает значение поля {@link HtmlAttribute#value}
     * @return значение аргумента
     */
    public String getValue() {
        return value;
    }
    /**
     * Устанавливает значение поля {@link HtmlAttribute#value}
     * @param value значение аргумента
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Создаёт новый объект аргумента html-элемента
     * @param name название аргумента
     * @param value значение аргумента
     */
    public HtmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
