package xyz.lawlietbot.spring.frontend.components.premium;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.payment.ProductTxt2Img;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
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
        H2 header = new H2(getTranslation("premium.products.txt2img"));
        header.getStyle().set("margin-top", "16px");
        add(header);

        Paragraph p = new Paragraph(getTranslation("premium.products.txt2img.desc"));
        add(p);

        if (sessionData.isLoggedIn()) {
            HorizontalLayout remainingLayout = new HorizontalLayout();
            remainingLayout.setWidthFull();
            remainingLayout.setAlignItems(Alignment.CENTER);
            remainingLayout.getStyle().set("margin", "0");

            remainingLayout.add(new Paragraph(getTranslation("premium.products.txt2img.remaining")));

            remainingParagraph = new Paragraph("???");
            remainingParagraph.setId("txt2img-remaining");
            remainingLayout.add(remainingParagraph);

            add(remainingLayout);
        }

        FlexibleGridLayout gridLayout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(generateTxt2ImgArticles())
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);
        gridLayout.setWidthFull();
        gridLayout.getStyle().set("margin-top", "24px");
        add(gridLayout);
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
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Could not retrieve remaining images", e);
            remainingParagraph.setText("???");
        }
    }

    private Article[] generateTxt2ImgArticles() {
        Map<ProductTxt2Img, String> productPriceMap = PaddleManager.retrieveProductPrices(VaadinRequest.getCurrent().getHeader("CF-Connecting-IP"));

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

            buyLayout.add(new Label(productPriceMap.get(product)));
            buyLayout.add(generateBuyButton(product.getPriceId()));
            content.add(buyLayout);

            articles[i] = new Article(content);
        }
        return articles;
    }

    private Component generateBuyButton(String priceId) {
        Button button;
        if (sessionData.isLoggedIn()) {
            button = new Button(getTranslation("premium.products.buy"));
            button.setIcon(VaadinIcon.CART.create());
        } else {
            button = new Button(getTranslation("premium.buylogin"));
        }
        button.addClickListener(e -> {
            DiscordUser discordUser = sessionData.getDiscordUser().orElse(null);
            if (discordUser != null) {
                try {
                    PaddleManager.openPopupBilling(priceId, discordUser, getLocale());
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
