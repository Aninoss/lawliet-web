package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class Video extends Html {

    public Video(String videoURL, String posterURL) {
        super("<video ondragstart=\"return false;\" ondrop=\"return false;\" onmousedown=\"return false\" class=\"unselectable\" poster=\"" + posterURL + "\" autoplay muted loop><source src=\"" + videoURL + "\" type=\"video/mp4\"></video>");
    }

    public Video(String videoURL, String posterURL, String id) {
        super("<video ondragstart=\"return false;\" ondrop=\"return false;\" onmousedown=\"return false\" id=\"" + id + "\" class=\"unselectable\" poster=\"" + posterURL + "\" autoplay muted loop><source src=\"" + videoURL + "\" type=\"video/mp4\"></video>");
    }

}
