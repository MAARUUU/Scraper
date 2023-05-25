package managers.chitaru;

import downloader.WebDownloader;
import html.HtmlElement;
import html.HtmlPage;
import managers.chitaru.classes.ChitaRuNews;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Скрапер для новостной ленты сайта chita.ru */
public class ChitaRuManager {
    /** Ссылка на сайт */
    private final String rootUrl = "https://www.chita.ru/text/";
    /** Параметр "страница" */
    private final String pageQuery = "?page=";

    /** Задержка после отправки запроса */
    private int requestTimeout = 5000;

    /**
     * Возвращает задержку после отправки запроса
     * @return время задержки в миллисекундах
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Устанавливает задержку после отправки запроса
     * @param timeout время задержски в миллисекундах
     */
    public void setRequestTimeout(int timeout) {
        requestTimeout = timeout;
    }
    /** Количество запросов, после которого стоит сделать задержку перед отправкой следующих запросов */
    private int timeoutAfter = 10;

    /**
     * Возвращает максимальное число запросов между задержками
     * @return число запросов
     */
    public int getTimeoutAfter() {
        return timeoutAfter;
    }

    /**
     * Устанавливает максимальное число запросов между задержками
     * @param value число запросов
     */
    public void setTimeoutAfter(int value) {
        timeoutAfter = value;
    }

    /**
     * Инициализирует новый объект {@link ChitaRuManager} со значениями по умолчанию
     * @see ChitaRuManager#ChitaRuManager(int, int)
     */
    public ChitaRuManager() {
    }

    /**
     * Инициализирует новый объект {@link ChitaRuManager}
     * @param requestTimeout задержка перед отправкой запроса в миллисекундах
     * @param timeoutAfter количество запросов между задержками
     * @see ChitaRuManager#ChitaRuManager()
     */
    public ChitaRuManager(int requestTimeout, int timeoutAfter) {
        this.requestTimeout = requestTimeout;
        this.timeoutAfter = timeoutAfter;
    }

    /**
     * Парсинг ленты новостных заголовков
     * @param page html-страница {@link HtmlPage} ленты новостей
     * @return список новостей {@link ChitaRuNews}
     */
    private List<ChitaRuNews> ParseNewsHeaders(HtmlPage page) {
        ArrayList<ChitaRuNews> news = new ArrayList<>();

        for (HtmlElement element : page.getElementsByTag("a")) {
            if (element.getAttributes().size() > 0) {
                var IsNewsAttribute = element
                        .getAttributes()
                        .stream()
                        .filter(x -> x.getName().equals("data-test") && x.getValue().equals("archive-record-header"))
                        .findAny();

                if (IsNewsAttribute.isPresent()) {
                    news.add(new ChitaRuNews(element.getArgumentValueByName("href"), element.getValue()));
                }
            }
        }

        return news;
    }

    /**
     * Парсинг страницы отдельной новости
     * @param page html-страница {@link HtmlPage} отдельной новости
     * @param getImage флаг скачивания изображений
     * @return новость {@link ChitaRuNews} со страницы
     * @see ChitaRuManager#ParseNewsPage(HtmlPage)
     */
    private ChitaRuNews ParseNewsPage(HtmlPage page, boolean getImage)
            throws IOException, URISyntaxException {
        String header = null;
        String keywords = null;
        List<String> imageUrls = new ArrayList<>();

        for (var element : page.getElementsByTag("meta")) {
            var isHeaderMeta = element
                    .getAttributes()
                    .stream()
                    .filter(x -> x.getName().equals("name") && x.getValue().equals("relap-title"))
                    .findAny();

            if (isHeaderMeta.isPresent()) {
                header = element.getArgumentValueByName("content");
                continue;
            }

            var isKeywordsMeta = element
                    .getAttributes()
                    .stream()
                    .filter(x -> x.getName().equals("name") && x.getValue().equals("news_keywords"))
                    .findAny();

            // TODO: распарсить ключевые слова в коллекцию, чтобы группировать "похожие" новости?
            if (isKeywordsMeta.isPresent()) {
                keywords = element.getArgumentValueByName("content");
                continue;
            }

            // TODO: выбирать несколько изображений?
            var isImageMeta = element
                    .getAttributes()
                    .stream()
                    .filter(x -> x.getName().equals("name") && x.getValue().equals("relap-image"))
                    .findAny();

            if (isImageMeta.isPresent()){
                imageUrls.add(element.getArgumentValueByName("content"));
            }
        }

        // Качаем картинки после проверки всех мета-тэгов, т.к. заголовок новости может встретиться позже ссылок на картинки
        if (getImage) {
            WebDownloader downloader = new WebDownloader();
            for (var url : imageUrls)
                // TODO: скачивать в несколько потоков?
                downloader.DownloadImage(url, downloader.getDownloadPath() + "\\images\\" + header);
        }

        StringBuilder text = new StringBuilder();
        for (var element : page.getElementsByTag("p")) {
            if (element.getAttributes().size() == 0) {
                text.append(element.getValue());
            }
        }

        ChitaRuNews news = new ChitaRuNews(header);
        news.setText(text.toString());
        if (keywords != null)
            news.setKeywords(keywords);
        return news;
    }

    /**
     * Парсинг страницы отдельной новости
     * @param page html-страница {@link HtmlPage} отдельной новости
     * @return новость {@link ChitaRuNews} со страницы
     * @see ChitaRuManager#ParseNewsPage(HtmlPage, boolean)
     */
    private ChitaRuNews ParseNewsPage(HtmlPage page)
            throws IOException, URISyntaxException {
        return ParseNewsPage(page, false);
    }

    /**
     * Загрузка страниц ленты новостей
     * @param skipPages число страниц для пропуска
     * @param pageCount число страниц для получения
     * @return список html-страниц {@link HtmlPage}
     */
    private List<HtmlPage> LoadNewsPages(int skipPages, int pageCount) throws Exception {
        ArrayList<HtmlPage> pages = new ArrayList<>();

        WebDownloader downloader = new WebDownloader();
        if (pageCount > 0) {
            for (int i = skipPages + 1; i <= skipPages + pageCount; i++) {
                String pageUrl = rootUrl + pageQuery + i;
                String pageText = downloader.DownloadWebPage(pageUrl);
                pages.add(new HtmlPage(pageText, pageUrl));

                if (i % timeoutAfter == 0)
                    Thread.sleep(requestTimeout);
            }
        }

        return pages;
    }

    /**
     * Загрузка страниц ленты новостей в отдельных потоках
     * @param skipPages число страниц для пропуска
     * @param pageCount число страниц для получения
     * @param nThreads количество потоков
     * @return список html-страниц {@link HtmlPage}
     */
    private List<HtmlPage> LoadNewsPages2(int skipPages, int pageCount, int nThreads) throws Exception {
        ArrayList<HtmlPage> pages = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        List<Future<String>> futures = new ArrayList<>();

        WebDownloader downloader = new WebDownloader();
        if (pageCount > 0) {
            for (int i = skipPages + 1; i <= skipPages + pageCount; i++) {
                String pageUrl = rootUrl + pageQuery + i;
                futures.add(
                        CompletableFuture.supplyAsync(
                                () -> downloader.DownloadWebPage(pageUrl),
                                threadPool
                        )
                );

                if (i % timeoutAfter == 0)
                    Thread.sleep(requestTimeout);
            }
        }

        for (Future<String> future : futures) {
            // TODO: сохранить url страницы?
            pages.add(new HtmlPage(future.get()));
        }

        threadPool.shutdown();
        return pages;
    }

    /**
     * Получение списка новостей с сайта chita.ru
     * @param skipPages число страниц для пропуска
     * @param pageCount число страниц для получения
     * @param headersOnly флаг скачивания только заголовков новостей
     * @return список новостей {@link ChitaRuNews}
     * @see ChitaRuManager#GetNews(int, boolean)
     * @see ChitaRuManager#GetNews2(int, boolean, int)
     */
    public List<ChitaRuNews> GetNews(int skipPages, int pageCount, boolean headersOnly) throws Exception {
        ArrayList<ChitaRuNews> news = new ArrayList<>();

        for (var page : LoadNewsPages(skipPages, pageCount)) {
            if (headersOnly) {
                news.addAll(ParseNewsHeaders(page));
            }
            else {
                WebDownloader downloader = new WebDownloader();
                for (var header : ParseNewsHeaders(page)) {
                    String pageText = downloader.DownloadWebPage(header.getUrl());
                    HtmlPage newsPage = new HtmlPage(pageText, header.getUrl());
                    news.add(ParseNewsPage(newsPage));
                }
            }
        }

        return news;
    }

    /**
     * Получение списка новостей с сайта chita.ru
     * @param pageCount число страниц для получения
     * @param headersOnly флаг скачивания только заголовков новостей
     * @return список новостей {@link ChitaRuNews}
     * @see ChitaRuManager#GetNews(int, int, boolean)
     * @see ChitaRuManager#GetNews2(int, boolean, int) 
     */
    public List<ChitaRuNews> GetNews(int pageCount, boolean headersOnly) throws Exception {
        return GetNews(0, pageCount, headersOnly);
    }

    /**
     * Получение списка новостей с сайта chita.ru в отдельных потокох
     * @param skipPages число страниц для пропуска
     * @param pageCount число страниц для получения
     * @param headersOnly флаг скачивания только заголовков новостей
     * @param nThreads количество потоков
     * @return список новостей {@link ChitaRuNews}
     * @see ChitaRuManager#GetNews2(int, boolean, int)
     * @see ChitaRuManager#GetNews(int, int, boolean)
     */
    public List<ChitaRuNews> GetNews2(int skipPages, int pageCount, boolean headersOnly, int nThreads) throws Exception {
        ArrayList<ChitaRuNews> news = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = new ArrayList<>();

        for (var page : LoadNewsPages2(skipPages, pageCount, nThreads)) {
            if (headersOnly) {
                news.addAll(ParseNewsHeaders(page));
            }
            else {
                WebDownloader downloader = new WebDownloader();
                int i = 0;
                for (var header : ParseNewsHeaders(page)) {
                    futures.add(
                            CompletableFuture.supplyAsync(
                                    () -> downloader.DownloadWebPage(header.getUrl()),
                                    threadPool
                            )
                    );

                    i++;
                    if (i % timeoutAfter == 0)
                        Thread.sleep(requestTimeout);
                }

                for (Future<String> future : futures) {
                    HtmlPage newsPage = new HtmlPage(future.get());
                    news.add(ParseNewsPage(page));
                }
            }
        }

        threadPool.shutdown();
        return news;
    }

    /**
     * Получение списка новостей с сайта chita.ru в отдельных потокох
     * @param pageCount число страниц для получения
     * @param headersOnly флаг скачивания только заголовков новостей
     * @param nThreads количество потоков
     * @return список новостей {@link ChitaRuNews}
     * @see ChitaRuManager#GetNews2(int, int, boolean, int)
     * @see ChitaRuManager#GetNews(int, boolean)
     */
    public List<ChitaRuNews> GetNews2(int pageCount, boolean headersOnly, int nThreads) throws Exception {
        return GetNews2(0, pageCount, headersOnly, nThreads);
    }

    @Deprecated
    public List<ChitaRuNews> GetTest() throws Exception {
        ArrayList<ChitaRuNews> news = new ArrayList<>();
        WebDownloader downloader = new WebDownloader();

        String text = downloader.TestFromFile("Y:\\Other\\Pages\\news1.txt");
        HtmlPage page = new HtmlPage(text);
        news.add(ParseNewsPage(page, true));

        return news;
    }
}