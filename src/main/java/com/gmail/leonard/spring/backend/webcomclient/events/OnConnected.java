package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.commandlist.CommandListContainer;
import com.gmail.leonard.spring.backend.faq.FAQListContainer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

public class OnConnected implements Runnable {

    @Override
    public void run() {
        CommandListContainer.getInstance().clear();
        FAQListContainer.getInstance().clear();
    }

}
