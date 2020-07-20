package com.gmail.leonard.spring.Backend;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileString {

    private String content = "";

    public FileString(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String line;
        while((line = br.readLine()) != null) {
            content = content.concat(line).concat("\r\n");
        }

        br.close();
        fr.close();
    }

    public FileString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        int chr;
        while((chr = br.read()) >= 0) {
            content = content.concat(String.valueOf((char) chr));
        }

        br.close();
        inputStream.close();
    }

    @Override
    public String toString() {
        return content;
    }
}
