package xyz.lawlietbot.spring.frontend.components.dashboard;

import xyz.lawlietbot.spring.frontend.components.Card;
import xyz.lawlietbot.spring.frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DashboardServerCard extends Card {

    public DashboardServerCard(String iconURL, String name) {
        setHeightFull();

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        Image image = new Image(iconURL, "");
        image.setWidth("70%");
        image.addClassName(Styles.ROUND);

        Div titleLabel = new Div(new Text(name));
        titleLabel.getElement().getStyle()
                .set("width", "100%")
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("overflow", "hidden")
                .set("white-space", "nowrap");

        if (name.length() < 14) titleLabel.getElement().getStyle().set("font-size", "100%");
        else if (name.length() < 18) titleLabel.getElement().getStyle().set("font-size", "90%");
        else titleLabel.getElement().getStyle().set("font-size", "80%");

        content.add(image, titleLabel);
        content.setHeightFull();
        content.setFlexGrow(1, titleLabel);

        add(content);
    }

}