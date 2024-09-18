package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.componentfactory.css.grid.GridLayoutComponent;
import org.vaadin.addons.componentfactory.css.grid.sizes.Flex;
import org.vaadin.addons.componentfactory.css.grid.sizes.Length;
import org.vaadin.addons.componentfactory.css.grid.sizes.MinMax;
import org.vaadin.addons.componentfactory.css.grid.sizes.Repeat;
import org.vaadin.addons.componentfactory.layout.FlexibleGridLayout;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.payment.ProductPremium;
import xyz.lawlietbot.spring.backend.payment.ProductTxt2Img;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PremiumProductsPage extends PremiumPage {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumProductsPage.class);

    private final SessionData sessionData;
    private Paragraph remainingParagraph;

    public PremiumProductsPage(SessionData sessionData) {
        this.sessionData = sessionData;
        setPadding(true);
    }

    @Override
    public void build() {
        Map<String, String> productPriceMap = PaddleManager.retrieveProductPrices(VaadinRequest.getCurrent().getHeader("CF-Connecting-IP"), " + " + getTranslation("premium.products.vat"));
        add(
                createPremiumField(productPriceMap),
                createTxt2ImgField(productPriceMap)
        );
    }

    @Override
    public void open() {
        if (!sessionData.isLoggedIn()) {
            return;
        }

        try {
            int remaining = SendEvent.sendToAnyCluster(EventOut.TXT2IMG_BOUGHT_IMAGES, Map.of("user_id", sessionData.getDiscordUser().get().getId())).get()
                    .getInt("remaining");
            remainingParagraph.setText(StringUtil.numToString(remaining));
        } catch (InterruptedException | ExecutionException | JSONException e) {
            LOGGER.error("Could not retrieve remaining images", e);
            remainingParagraph.setText("???");
        }
    }

    private Component createPremiumField(Map<String, String> productPriceMap) {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);

        H2 header = new H2(getTranslation("premium.products.premium"));
        header.getStyle().set("margin-top", "16px");
        mainLayout.add(header);

        Paragraph p = new Paragraph(getTranslation("premium.products.premium.desc"));
        mainLayout.add(p);

        if (sessionData.isLoggedIn()) {
            ConfirmationDialog dialog = new ConfirmationDialog();
            add(dialog);

            Button revealButton = new Button(getTranslation("premium.products.revealcodes"));
            revealButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            revealButton.getStyle().set("margin-top", "0.5rem")
                    .set("margin-bottom", "1rem");
            revealButton.addClickListener(e -> {
                try {
                    JSONObject responseJson = SendEvent.send(EventOut.BOUGHT_PREMIUM_CODES, Map.of("user_id", sessionData.getDiscordUser().get().getId())).join();
                    JSONArray codesJson = responseJson.getJSONArray("codes");

                    if (codesJson.length() == 0) {
                        dialog.open(getTranslation("premium.products.nocodes"), () -> {
                        });
                    } else {
                        VerticalLayout linksLayout = new VerticalLayout();
                        linksLayout.setPadding(false);
                        linksLayout.setSpacing(false);
                        for (int i = 0; i < codesJson.length(); i++) {
                            String url = ExternalLinks.LAWLIET_GIFT + codesJson.getString(i);
                            Anchor a = new Anchor(url, url);
                            a.setTarget("_blank");
                            linksLayout.add(a);
                        }
                        dialog.open(linksLayout, () -> {
                        });
                    }
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            });
            mainLayout.add(revealButton);
        }

        FlexibleGridLayout gridLayout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(generatePremiumArticles(productPriceMap))
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);
        gridLayout.setWidthFull();
        gridLayout.getStyle().set("margin-top", "24px");
        mainLayout.add(gridLayout);
        return mainLayout;
    }

    private Component createTxt2ImgField(Map<String, String> productPriceMap) {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);

        H2 header = new H2(getTranslation("premium.products.txt2img"));
        mainLayout.add(header);

        Paragraph p = new Paragraph(getTranslation("premium.products.txt2img.desc"));
        mainLayout.add(p);

        if (sessionData.isLoggedIn()) {
            HorizontalLayout remainingLayout = new HorizontalLayout();
            remainingLayout.setWidthFull();
            remainingLayout.setAlignItems(Alignment.CENTER);
            remainingLayout.getStyle().set("margin", "0");

            remainingLayout.add(new Paragraph(getTranslation("premium.products.txt2img.remaining")));

            remainingParagraph = new Paragraph("???");
            remainingParagraph.setId("txt2img-remaining");
            remainingLayout.add(remainingParagraph);

            mainLayout.add(remainingLayout);
        }

        FlexibleGridLayout gridLayout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(generateTxt2ImgArticles(productPriceMap))
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);
        gridLayout.setWidthFull();
        gridLayout.getStyle().set("margin-top", "24px");
        mainLayout.add(gridLayout);
        return mainLayout;
    }

    private Article[] generatePremiumArticles(Map<String, String> productPriceMap) {
        Article[] articles = new Article[ProductPremium.values().length];
        for (int i = 0; i < ProductPremium.values().length; i++) {
            ProductPremium product = ProductPremium.values()[i];

            VerticalLayout content = new VerticalLayout();
            content.setSpacing(false);
            content.addClassNames("tier-card2");

            Paragraph p = new Paragraph(getTranslation("premium.products." + product.name()));
            p.getStyle().set("margin-top", "0");
            content.add(p);

            HorizontalLayout buyLayout = new HorizontalLayout();
            buyLayout.setWidthFull();
            buyLayout.setPadding(false);
            buyLayout.setJustifyContentMode(JustifyContentMode.END);
            buyLayout.setAlignItems(Alignment.CENTER);

            buyLayout.add(new Label(productPriceMap.get(product.getPriceId())));
            buyLayout.add(generateBuyButton(product.getPriceId(), "premium"));
            content.add(buyLayout);

            articles[i] = new Article(content);
        }
        return articles;
    }

    private Article[] generateTxt2ImgArticles(Map<String, String> productPriceMap) {
        Article[] articles = new Article[ProductTxt2Img.values().length];
        for (int i = 0; i < ProductTxt2Img.values().length; i++) {
            ProductTxt2Img product = ProductTxt2Img.values()[i];

            VerticalLayout content = new VerticalLayout();
            content.setSpacing(false);
            content.addClassNames("tier-card2");

            Paragraph p = new Paragraph(getTranslation("premium.products.title", String.valueOf(product.getNumber())));
            p.getStyle().set("margin-top", "0");
            content.add(p);

            HorizontalLayout buyLayout = new HorizontalLayout();
            buyLayout.setWidthFull();
            buyLayout.setPadding(false);
            buyLayout.setJustifyContentMode(JustifyContentMode.END);
            buyLayout.setAlignItems(Alignment.CENTER);

            buyLayout.add(new Label(productPriceMap.get(product.getPriceId())));
            buyLayout.add(generateBuyButton(product.getPriceId(), "txt2img"));
            content.add(buyLayout);

            articles[i] = new Article(content);
        }
        return articles;
    }

    private Component generateBuyButton(String priceId, String type) {
        Button button;
        if (sessionData.isLoggedIn()) {
            button = new Button(getTranslation("premium.products.buy"));
            button.setIcon(VaadinIcon.CART.create());
        } else {
            button = new Button(getTranslation("login"));
        }
        button.addClickListener(e -> {
            DiscordUser discordUser = sessionData.getDiscordUser().orElse(null);
            if (discordUser != null) {
                try {
                    PaddleManager.openPopupBilling(priceId, discordUser, getLocale(), type);
                } catch (Exception ex) {
                    LOGGER.error("Exception", ex);
                    CustomNotification.showError(getTranslation("error"));
                }
            } else {
                new Redirector().redirect(sessionData.getLoginUrl());
            }
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

}
