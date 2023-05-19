package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.html.Span;

public class SpanWithLinebreaks extends Span {

    public SpanWithLinebreaks(String text) {
        super();
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                add(new LineBreak());
            }
            add(lines[i]);
        }
    }

}
