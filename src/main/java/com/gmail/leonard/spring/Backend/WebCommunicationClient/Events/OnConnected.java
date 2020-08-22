package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import io.socket.emitter.Emitter;

public class OnConnected implements Emitter.Listener {

    @Override
    public void call(Object... objects) {
        CommandListContainer.getInstance().clear();
        FAQListContainer.getInstance().clear();
    }

}
