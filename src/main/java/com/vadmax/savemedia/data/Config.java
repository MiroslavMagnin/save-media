package com.vadmax.savemedia.data;

/**
 * Тут подгружаются все необходимые данные для работы приложения
 * Пока что просто набор статичных переменных
 */
public class Config {
    public static String homeDrive = System.getenv("HOMEDRIVE").substring(0, 1);
    public static String YtDlpLocation = "M:\\Java Projects\\save-media\\src\\main\\resources\\yt-dlp"; // Путь к yt-dlp.exe
    public static String savePath;
    public static String url;
    public static String fileName;

}
