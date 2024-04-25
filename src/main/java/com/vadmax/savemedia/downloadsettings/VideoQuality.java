package com.vadmax.savemedia.downloadsettings;

public enum VideoQuality {
    BEST("best"),
    MIDDLE("bestvideo[height<720]+bestaudio"),
    WORST("worst"),
    BEST_VIDEO("bestvideo"),
    MIDDLE_VIDEO("bestvideo[height<720]"),
    WORST_VIDEO("worstvideo"),
    BEST_AUDIO("bestaudio"),
    MIDDLE_AUDIO("bestvideo[height<720]+bestaudio"),
    WORST_AUDIO("worstaudio");

    private String quality;
    VideoQuality(String quality) {
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }

    public static class FormatToVideoQuality {
        public static VideoQuality toVideoQuality(String format) {
            switch (format) {
                case "Best [video + audio]":
                    return BEST;
                case "Middle [video + audio]":
                    return MIDDLE;
                case "Worst [video + audio]":
                    return WORST;
                case "Best [only video]":
                    return BEST_VIDEO;
                case "Middle [only video]":
                    return MIDDLE_VIDEO;
                case "Worst [only video]":
                    return WORST_VIDEO;
                case "Best [only audio]":
                    return BEST_AUDIO;
                case "Middle [only audio]":
                    return MIDDLE_AUDIO;
                case "Worst [only audio]":
                    return WORST_AUDIO;
                default:
                    throw new IllegalStateException("Unexpected value: " + format);
            }
        }
    }
}
