package xyz.lawlietbot.spring.frontend.views;

import xyz.lawlietbot.spring.backend.faq.FAQListContainer;
import xyz.lawlietbot.spring.backend.faq.FAQListSlot;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.Card;
import xyz.lawlietbot.spring.frontend.components.HtmlText;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "faq", layout = MainLayout.class)
public class FAQView extends PageLayout {

    private final VerticalLayout mainContent = new VerticalLayout();

    public FAQView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        addEntries();
        add(new PageHeader(getTitleText(), getTranslation("faq.desc"), getRoute()), mainContent);
    }

    private void addEntries() {
        for(int i = 0; i < FAQListContainer.getInstance().size(); i++) {
            FAQListSlot slot = FAQListContainer.getInstance().get(i);

            if (i != 3 || !getUiData().isNSFWDisabled()) {
                H4 question = new H4(new Text(slot.getQuestion().get(getLocale())));
                HtmlText answer = new HtmlText(slot.getAnswer().get(getLocale()));

                Card card = new Card(new VerticalLayout(question, new Hr(), answer));
                card.setWidthFull();
                mainContent.add(card);
            }
        }
    }

}
