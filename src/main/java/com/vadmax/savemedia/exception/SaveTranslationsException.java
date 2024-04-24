package com.vadmax.savemedia.exception;

public abstract class SaveTranslationsException extends Exception {
    private SaveTranslationsException(String message) {
        super(message);
    }

    public static class DownloadException extends SaveTranslationsException {
        public DownloadException(String message) {
            super(message);
        }
    }

    public static class BadPageException extends SaveTranslationsException {
        public BadPageException(String message) {
            super(message);
        }
    }
}
