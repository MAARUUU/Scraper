package html;

import java.util.ArrayList;

/** Html-элемент */
public class HtmlElement {
    /** Тэг html-элемента */
    private final String tag;
    /**
     * Возвращает тэг html-элемента
     * @return значение тэга
     */
    public String getTag() {
        return tag;
    }

    /** Значение html-элемента */
    private String value;
    /**
     * Возвращает значение html-элемента
     * @return значение html-элемента
     */
    public String getValue() {
        return value;
    }
    /**
     * Устанавливает значение html-элемента
     * @param value значение html-элемента
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** Аттрибуты html-элемента */
    private final ArrayList<HtmlAttribute> attributes = new ArrayList<>();
    /**
     * Возвращает список аргументов html-элемента
     * @return список аргументов
     */
    public ArrayList<HtmlAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Создает новый объект html-элемента
     */
    public HtmlElement(String tag) {
        this.tag = tag;
    }

    /**
     * Возвращает значение аттрибуты по его названию
      * @param name название аттрибута
     * @return значение аттрибута
     */
    public String getArgumentValueByName(String name) {
        for (HtmlAttribute attribute : attributes) {
            if (attribute.getName().equals(name))
                return attribute.getValue();
        }
        return null;
    }
}
