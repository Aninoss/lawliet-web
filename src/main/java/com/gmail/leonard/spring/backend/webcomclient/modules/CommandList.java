package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.webcomclient.WebComClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.concurrent.CompletableFuture;

public class CommandList {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandList.class);

    public static CompletableFuture<JSONObject> fetchCommandList() {
        File cacheFile = new File("commands_cache.json");
        if (cacheFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(cacheFile));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return CompletableFuture.completedFuture(new JSONObject(sb.toString()));
            } catch (IOException e) {
                LOGGER.error("File error", e);
            }
        }

        return WebComClient.getInstance().send(WebComClient.EVENT_COMMANDLIST, JSONObject.class);
    }

}
