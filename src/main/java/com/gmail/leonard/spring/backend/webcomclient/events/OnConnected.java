package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.commandlist.CommandListContainer;
import com.gmail.leonard.spring.backend.faq.FAQListContainer;
import io.socket.emitter.Emitter;

public class OnConnected implements Emitter.Listener {

    @Override
    public void call(Object... objects) {
        CommandListContainer.getInstance().clear();
        FAQListContainer.getInstance().clear();
    }

}
