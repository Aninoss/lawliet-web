package xyz.lawlietbot.spring.frontend.components.header;

import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.ExternalLinks;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import xyz.lawlietbot.spring.frontend.views.*;

public class NavigationBar extends Tabs implements AfterNavigationObserver {

    public NavigationBar(UIData uiData) {
        super();

        NavigationBarLink[] navigationBarLinks = new NavigationBarLink[]{
                new NavigationBarLink(HomeView.class),
                new NavigationBarLink(CommandsView.class),
                new NavigationBarLink(FAQView.class),
                new NavigationBarLink(FeatureRequestsView.class),
                new NavigationBarLink(PremiumView.class).standOut(),
                new NavigationBarLink(uiData.getBotInviteUrl(), "invite"),
                new NavigationBarLink(ExternalLinks.SERVER_INVITE_URL, "server"),
        };

        for(NavigationBarLink navigationBarLink: navigationBarLinks) {
            if (!navigationBarLink.isHiddenInLiteVersion() || !uiData.isLite()) {
                Tab tab = new Tab();
                if (navigationBarLink.getRouterLink().isPresent()) tab.add(navigationBarLink.getRouterLink().get());
                else if (navigationBarLink.getAnchor().isPresent()) tab.add(navigationBarLink.getAnchor().get());
                add(tab);
            }
        }

        setSelectedIndex(-1);

        addSelectedChangeListener(this::onSelectChangeListener);
    }

    private void onSelectChangeListener(SelectedChangeEvent event) {
        if (event.getSelectedTab().getChildren().anyMatch(children -> children instanceof Anchor))
            setSelectedTab(event.getPreviousTab());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        String path = afterNavigationEvent.getLocation().getPath();

        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponentAt(i);
            if (c instanceof Tab) {
                boolean found = c.getChildren()
                        .filter(child -> child instanceof RouterLink)
                        .map(routerLink -> (RouterLink) routerLink)
                        .anyMatch(routerLink -> (path.startsWith(routerLink.getHref()) && !routerLink.getHref().isEmpty()) || path.equals(routerLink.getHref()));

                if (found) setSelectedIndex(i);
            }
        }
    }

}
