package com.gmail.leonard.spring.Backend;

import java.io.*;

public class FileString {

    private String content = "";

    public FileString(File file) throws IOException {
        System.out.println(file.exists());

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
        BufferedInputStream bip = new BufferedInputStream(inputStream);

        int chr;
        while((chr = bip.read()) >= 0) {
            content = content.concat(String.valueOf((char) chr));
        }

        bip.close();
        inputStream.close();
    }

    @Override
    public String toString() {
        return content;
    }
}
