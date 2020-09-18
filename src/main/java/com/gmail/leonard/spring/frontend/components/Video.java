package com.gmail.leonard.spring.frontend.components;

import com.vaadin.flow.component.Html;

public class Video extends Html {

    public Video(String videoURL, String posterURL) {
        super("<video ondragstart=\"return false;\" ondrop=\"return false;\" onmousedown=\"return false\" class=\"unselectable\" poster=\"" + posterURL + "\" autoplay muted loop><source src=\"" + videoURL + "\" type=\"video/mp4\"></video>");
    }

    public Video(String videoURL, String posterURL, String id) {
        super("<video ondragstart=\"return false;\" ondrop=\"return false;\" onmousedown=\"return false\" id=\"" + id + "\" class=\"unselectable\" poster=\"" + posterURL + "\" autoplay muted loop><source src=\"" + videoURL + "\" type=\"video/mp4\"></video>");
    }

}
