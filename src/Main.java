import database.DBWorker;
import html.HtmlAttribute;
import html.HtmlElement;
import html.HtmlPage;
import downloader.WebDownloader;
import managers.chitaru.ChitaRuManager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        try {
            /*DBWorker worker = new DBWorker("localhost", 5432, "news");
            worker.Query();*/

            ChitaRuManager manager = new ChitaRuManager();

            //var news = manager.GetNews(1, false);
            var news = manager.GetTest();

            for (int i = 0; i < news.size(); i++) {
                System.out.println("Заголовок: " + news.get(i).getHeader());
                System.out.println("Текст: " + news.get(i).getText());
                System.out.println("Ключевые слова: " + news.get(i).getKeywords());
            }
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}