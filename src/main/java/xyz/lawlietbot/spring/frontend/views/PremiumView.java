package xyz.lawlietbot.spring.frontend.views;

import java.util.Arrays;
import java.util.List;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.Card;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

@Route(value = "premium", layout = MainLayout.class)
@NoLiteAccess
//@LoginAccess
public class PremiumView extends PageLayout {

    private final VerticalLayout mainContent = new VerticalLayout();

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        addSlots();
        add(
                new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()),
                mainContent
        );
    }

    private void addSlots() {
        H2 title = new H2("Unlock Servers");
        title.getStyle().set("margin-top", "8px");
        mainContent.add(title);

        Paragraph p = new Paragraph("Which server do you want to unlock?");
        p.getStyle().set("margin-bottom", "26px")
                .set("margin-top", "0");
        mainContent.add(p);

        /*Paragraph p = new Paragraph(getTranslation("premium.noactive"));
        mainContent.add(p);*/

        addCard("Anicord", "https://cdn.discordapp.com/icons/462405241955155979/a_2fb215184c7946b3b46f6b542213729f.gif", Arrays.asList("Server One", "Server Two"));
        addCard(null, null, Arrays.asList("Server One", "Server Two"));
    }

    private void addCard(String guildName, String iconUrl, List<String> guilds) {
        Card card = new Card();
        card.setWidthFull();
        card.setHeight("72px");
        card.getStyle().set("margin-bottom", "-8px");

        card.add(getCardContent(guildName, iconUrl, guilds));
        mainContent.add(card);
    }

    private HorizontalLayout getCardContent(String guildName, String iconUrl, List<String> guilds) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setPadding(true);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        if (guildName == null) {
            Label label = new Label(getTranslation("premium.notset"));
            horizontalLayout.add(label);
            horizontalLayout.setFlexGrow(1, label);

            ComboBox<String> guildComboBox = new ComboBox<>();
            guildComboBox.setItems(guilds);
            horizontalLayout.add(guildComboBox);

            Button button = new Button(VaadinIcon.PLUS.create());
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            horizontalLayout.add(button);
        } else {
            Image guildIcon = new Image(iconUrl, "Server Icon");
            guildIcon.setHeightFull();
            guildIcon.addClassName(Styles.ROUND);
            horizontalLayout.add(guildIcon);

            Label label = new Label(guildName);
            horizontalLayout.add(label);
            horizontalLayout.setFlexGrow(1, label);

            Button button = new Button(getTranslation("premium.remove"), VaadinIcon.CLOSE_SMALL.create());
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            horizontalLayout.add(button);
        }

        return horizontalLayout;
    }

}
