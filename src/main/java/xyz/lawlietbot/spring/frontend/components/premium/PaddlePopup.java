package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.json.JSONObject;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;

public class PaddlePopup extends Div {

    public PaddlePopup(SubDuration duration, SubLevel level, DiscordUser discordUser, int quantity) {
        setSizeFull();
        setId("paddle-popup");
        addClassName("fadein-class");
        add(generateMainLayout(duration));

        UI.getCurrent().getPage().executeJs("openPaddle($0, $1, $2, $3, $4)",
                Integer.parseInt(System.getenv("PADDLE_VENDOR_ID")),
                PaddleManager.getPlanId(duration, level),
                quantity,
                getLocale().getLanguage(),
                generatePassthrough(discordUser)
        );
    }

    private String generatePassthrough(DiscordUser discordUser) {
        JSONObject json = new JSONObject();
        json.put("discord_id", discordUser.getId());
        json.put("discord_tag", discordUser.getUsername() + "#" + discordUser.getDiscriminator());
        json.put("discord_avatar", discordUser.getUserAvatar());
        return json.toString();
    }

    private Component generateMainLayout(SubDuration duration) {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        layout.setId("paddle-main");
        layout.add(generateHeader(duration));

        Div div = new Div();
        div.setClassName("paddle-container");
        layout.add(div);

        return layout;
    }

    private Component generateHeader(SubDuration duration) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setId("paddle-header");
        layout.add(generateTitle());

        FlexLayout details = new FlexLayout();
        details.setFlexDirection(FlexLayout.FlexDirection.COLUMN);

        details.add(
                generatePriceTag("subtotal", duration),
                generatePriceTag("tax", duration),
                generatePriceTag("total", duration)
        );
        layout.add(details);
        return layout;
    }

    private Component generatePriceTag(String propertyId, SubDuration duration) {
        Span mainSpan = new Span(getTranslation("premium.paddle." + propertyId) + ": ");

        Span currency = new Span("USD ");
        currency.setClassName("paddle-currency");

        Span value = new Span("0.00");
        value.setId("paddle-" + propertyId);

        Span interval = new Span(" / " + getTranslation("premium.paddle.interval." + duration.name()));
        mainSpan.add(currency, value, interval);
        return mainSpan;
    }

    private Component generateTitle() {
        FlexLayout titleLayout = new FlexLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H3 title = new H3(getTranslation("premium.paddle.loading"));
        title.setId("paddle-title");

        Icon icon = VaadinIcon.CLOSE.create();
        icon.getStyle().set("cursor", "pointer");
        icon.addClickListener(e -> onClose());

        titleLayout.add(title, icon);
        return titleLayout;
    }

    private void onClose() {
        ((HtmlContainer) getParent().get()).remove(this);
    }

}
