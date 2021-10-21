package xyz.lawlietbot.spring.backend.report;

public class ContentType {

    private final boolean animated;
    private final boolean video;
    private final String ext;

    private ContentType(boolean animated, boolean video, String ext) {
        this.animated = animated;
        this.video = video;
        this.ext = ext;
    }

    public boolean isAnimated() {
        return animated;
    }

    public boolean isVideo() {
        return video;
    }

    public String getExt() {
        return ext;
    }

    public static ContentType parseFromUrl(String url) {
        String[] urlParts = url.toLowerCase().split("\\.");
        String ext = urlParts[urlParts.length - 1];

        switch (ext) {
            case "jpeg":
            case "jpg":
            case "png":
            case "bmp":
                return new ContentType(false, false, ext);

            case "gif":
                return new ContentType(true, false, ext);

            case "mp4":
            case "avi":
            case "webm":
                return new ContentType(true, true, ext);

            default:
                return null;
        }
    }

}
