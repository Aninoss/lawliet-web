package xyz.lawlietbot.spring.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileCache {

    private final List<File> files = new ArrayList<>();

    public void addFile(File file) {
        files.add(file);
    }

    public void flush() {
        files.clear();
    }

    public void delete() {
        files.forEach(File::delete);
    }

}
