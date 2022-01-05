package xyz.lawlietbot.spring.frontend.components;

import java.util.Arrays;
import javax.annotation.Nonnull;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

public class PageHeader extends Div {

    private final VerticalLayout outerLayout = new VerticalLayout();
    private final HorizontalLayout titleLayout = new HorizontalLayout();
    private final VerticalLayout innerLayout = new VerticalLayout();

    public PageHeader(UIData uiData, String title, String description, Class<? extends PageLayout> clazz, Component... components) {
        this(uiData, title, description, clazz, null, components);
    }

    public PageHeader(UIData uiData, String title, String description, Component... components) {
        this(uiData, title, description, null, null, components);
    }

    public PageHeader(UIData uiData, String title, String description, String route, Component... components) {
        this(uiData, title, description, null, route, components);
    }

    private PageHeader(UIData uiData, String title, String description, Class<? extends PageLayout> clazz, String route, Component... components) {
        setWidthFull();
        setId("page-header");

        outerLayout.addClassName(Styles.APP_WIDTH);
        outerLayout.setPadding(true);
        innerLayout.setPadding(false);
        titleLayout.setPadding(false);
        titleLayout.setWidthFull();

        outerLayout.add(new HeaderDummy(uiData));
        if (clazz != null) addBackButton(clazz);
        if (route != null) addPageIcon(route);
        if (title != null) addTitle(title);
        if (description != null) addDescription(description);

        titleLayout.add(innerLayout);
        outerLayout.add(titleLayout);
        Arrays.stream(components).forEach(component -> {
            if (component != null) outerLayout.add(component);
        });

        add(outerLayout);
    }

    private void addBackButton(Class<? extends PageLayout> clazz) {
        Icon icon = VaadinIcon.ARROW_LEFT.create();
        icon.setId("page-header-back-icon");

        Button backButton = new Button(icon);
        backButton.setId("page-header-back");
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate(clazz));
        titleLayout.add(backButton);
    }

    private void addPageIcon(@Nonnull String route) {
        String pageIconUrl = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/headers/" + route + ".svg",
                        VaadinSession.getCurrent().getBrowser());

        Image pageIcon = new Image(pageIconUrl, "");
        pageIcon.setId("page-icon");
        titleLayout.add(pageIcon);
    }

    private void addDescription(@Nonnull String description) {
        HtmlText htmlText = new HtmlText(description);
        htmlText.setWidthFull();
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

}
