package xyz.lawlietbot.spring.frontend.components;

import xyz.lawlietbot.spring.frontend.Styles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class PageHeader extends Div {

    private final VerticalLayout outerLayout = new VerticalLayout();
    private final HorizontalLayout titleDiv = new HorizontalLayout();
    private final VerticalLayout innerLayout = new VerticalLayout();

    public PageHeader(String title, String description, String route, Component... components) {
        setWidthFull();
        setId("page-header");
        addClassName("only-pc");

        outerLayout.addClassName(Styles.APP_WIDTH);
        outerLayout.setPadding(true);
        innerLayout.setPadding(false);
        titleDiv.setPadding(false);
        titleDiv.setWidthFull();

        outerLayout.add(new HeaderDummy());
        if (route != null) addPageIcon(route);
        if (title != null) addTitle(title);
        if (description != null) addDescription(description);

        titleDiv.add(innerLayout);
        outerLayout.add(titleDiv);
        Arrays.stream(components).forEach(component -> {
            if (component != null) outerLayout.add(component);
        });

        add(outerLayout);
    }

    private void addPageIcon(@Nonnull String route) {
        String pageIconUrl = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/headers/" + route + ".svg",
                        VaadinSession.getCurrent().getBrowser());

        Image pageIcon = new Image(pageIconUrl, "");
        pageIcon.setId("page-icon");
        titleDiv.add(pageIcon);
    }

    private void addDescription(@Nonnull String description) {
        HtmlText htmlText = new HtmlText(description);
        htmlText.setWidthFull();
        htmlText.getStyle().set("margin-bottom", "8px");
        innerLayout.add(htmlText);
    }

    private void addTitle(@Nonnull String title) {
        String htmlString = String.format("<h1 id=\"page-title\">%s<span id=\"page-title-shadow\" class=\"unselectable\">%s</span></h1>", title, title);
        Html html = new Html(htmlString);
        innerLayout.add(html);
    }

    public VerticalLayout getOuterLayout() {
        return outerLayout;
    }

    public VerticalLayout getInnerLayout() {
        return innerLayout;
    }

    protected void removeOnlyPC() {
        removeClassName("only-pc");
    }

}
