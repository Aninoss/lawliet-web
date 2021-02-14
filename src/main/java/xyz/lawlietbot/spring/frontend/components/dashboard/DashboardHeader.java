package xyz.lawlietbot.spring.frontend.components.dashboard;

import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.backend.userdata.DiscordServerData;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.frontend.components.IconLabel;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.Styles;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.json.JSONObject;

public class DashboardHeader extends PageHeader {

    private final DashboardTitle dashboardTitle;

    public DashboardHeader(UIData uiData, String dashboardName, DiscordServerData discordServerData) {
        super(uiData, null, null, null);

        dashboardTitle = new DashboardTitle(dashboardName, discordServerData.getName());

        JSONObject data = null;/*Dashboard.fetchServerMembersCount(sessionData, discordServerData.getId()).join();*/
        IconLabel iconLabel = new IconLabel(VaadinIcon.USER.create(), StringUtil.numToString(data.getLong("members_online")) + " / " + StringUtil.numToString(data.getLong("members_total")));
        iconLabel.getStyle().set("margin-top", "0");
        iconLabel.setWidthFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.setPadding(false);
        mainLayout.setId("dashboard-info");

        VerticalLayout serverDataLayout = new VerticalLayout(dashboardTitle, iconLabel);
        serverDataLayout.getStyle().set("margin-top", "10px");
        serverDataLayout.setPadding(false);

        if (discordServerData.getIcon().isPresent()) {
            Image image = new Image(discordServerData.getIcon().get(), "");
            image.setId("dashboard-servericon");
            image.addClassName(Styles.ROUND);
            mainLayout.add(image);
        }

        mainLayout.add(serverDataLayout);
        getOuterLayout().add(mainLayout);
    }

    public void addServerClickListener(DashboardServerClickListener listener) { dashboardTitle.addServerClickListener(listener); }

    public void removeServerClickListener(DashboardServerClickListener listener) { dashboardTitle.removeServerClickListener(listener); }

}