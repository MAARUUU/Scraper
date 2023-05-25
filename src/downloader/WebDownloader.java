package downloader;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * Загрузчик веб-страниц
 */
public class WebDownloader {
    /** Задержка подключения */
    private int connectTimeout = 5000;
    /**
     * Возвращает задержку подключения
     * @return время задержки подключения в миллисекундах
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }
    /**
     * Устанавливает задержку подключения
     * @param timeout время задержки подключения в миллисекундах
     */
    public void setConnectTimeout(int timeout) {
        connectTimeout = timeout;
    }

    /** Задержка чтения страницы */
    private int readTimeout = 5000;
    /**
     * Возвращает задержку чтения страницы
     * @return время задержки чтения в миллисекундах
     */
    public int getReadTimeout() {
        return readTimeout;
    }
    /**
     * Устанавливает задержку чтения страницы
     * @param timeout время задержки чтения в миллисекундах
     */
    public void setReadTimeout(int timeout) {
        readTimeout = timeout;
    }

    /** Путь для сохранения скачанных файлов по умолчанию */
    private String downloadPath;

    /**
     * Возвращает путь для сохранения скачанных файлов по умолчанию
     * @return путь для сохранения файлов
     */
    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     * Устанавливает путь для сохранения скачанных файлов по умолчанию
     * @param path путь для сохранения файлов
     */
    public void setDownloadPath(String path) {
        downloadPath = path;
    }

    /**
     * Инициализирует новый объект {@link WebDownloader}
     * @see WebDownloader#WebDownloader(int, int)
     */
    public WebDownloader() {
        downloadPath = System.getProperty("user.dir") + "\\downloads";
    }

    /**
     * Инициализирует новый объект {@link WebDownloader}
     * @param connectTimeout задержка подключения в миллисекундах
     * @param readTimeout задержка чтения в миллисекундах
     * @see WebDownloader#WebDownloader() 
     */
    public WebDownloader(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        downloadPath = System.getProperty("user.dir") + "\\downloads";
    }

    /**
     * Скачивает полный html-текст веб-страницы
     * @param url адрес веб-страницы
     * @return текст страницы
     * @throws Exception
     */
    public String DownloadWebPage(String url) {
        if (url == null)
            throw new IllegalArgumentException("Url is null.");

        StringBuilder result = new StringBuilder();
        String line;

        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla");
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);

            try (InputStream is = urlConnection.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
            }
            catch (Exception ex) {
                return ex.getMessage();
            }
        }
        catch (Exception ex) {
            return ex.getMessage();
        }

        return result.toString();
    }

    /**
     * Скачивает изображение в директорию по указанному пути
     * @param url адрес изображения
     * @param filepath путь для сохранения изображения
     * @throws IOException
     * @see WebDownloader#DownloadImage(String)
     */
    public void DownloadImage(String url, String filepath)
            throws IOException, InvalidPathException, URISyntaxException {
        try(InputStream in = new URL(url).openStream()){
            String fileName = Paths.get(new URI(url).getPath()).getFileName().toString();
            Files.createDirectories(Paths.get(filepath));
            Files.copy(in, Paths.get(filepath + "\\" + fileName));
        }
    }

    /**
     * Скачивает изображение в директорию по умолчанию (.\downloads\images)
     * @param url адрес изображения
     * @throws IOException
     * @see WebDownloader#DownloadImage(String, String)
     */
    public void DownloadImage(String url)
            throws IOException, URISyntaxException {
        DownloadImage(url, downloadPath + "\\images\\");
    }

    @Deprecated
    public String TestFromFile(String filepath) throws Exception {
        if (filepath == null)
            throw new IllegalArgumentException("Url is null.");

        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        }
    }
}