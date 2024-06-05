package xyz.lawlietbot.spring.frontend.components.home.botinfo;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import xyz.lawlietbot.spring.frontend.ComponentChanger;
import xyz.lawlietbot.spring.frontend.Styles;

import java.util.ArrayList;

public class BotInfoCarouselLayout extends Div {
    public BotInfoCarouselLayout() {
        setId("bot-info-carousel");
        addClassName(Styles.VISIBLE_NOT_SMALL);
        setWidthFull();

        add(generateCarousel(), generateFade());
    }

    private Component generateCarousel() {
        String[] labels = new String[]{"fishery", "fishery", "reactionroles", "alerts", "mod", "invitetracking"};

        ArrayList<Slide> slides = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Image image = new Image("styles/img/carousel_slides/" + i + ".webp", "");
            ComponentChanger.setNotInteractive(image);

            Text text = new Text(getTranslation("bot.card." + labels[i] + ".title"));
            Slide slide = new Slide(image, new Div(text));
            slide.getElement().setAttribute("class", "carousel-slide");
            slides.add(slide);
        }

        Carousel c = new Carousel(slides.toArray(Slide[]::new))
                .withAutoProgress()
                .withSlideDuration(5)
                .withStartPosition(0)
                .withoutSwipe();
        c.getElement().setAttribute("class", "carousel");
        return c;
    }

    private Component generateFade() {
        Div fade = new Div();
        ComponentChanger.setNotInteractive(fade);
        fade.addClassName("carousel-fade");
        fade.addClassName(Styles.VISIBLE_NOT_SMALL);
        return fade;
    }

}
