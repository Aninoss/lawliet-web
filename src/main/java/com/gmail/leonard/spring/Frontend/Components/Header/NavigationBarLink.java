package com.gmail.leonard.spring.Frontend.Components.Header;

import com.gmail.leonard.spring.Backend.Language.PageTitleFactory;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouterLink;

import java.util.Optional;

public class NavigationBarLink {

    private Anchor anchor;
    private RouterLink routerLink;
    private boolean hiddenInLiteVersion = false;

    public NavigationBarLink(String externalLink, String id, boolean hiddenInLiteVersion) {
        this(externalLink, id);
        this.hiddenInLiteVersion = hiddenInLiteVersion;
    }

    public NavigationBarLink(String externalLink, String id) {
        anchor = new Anchor(externalLink, PageTitleFactory.getTitle(id));
        anchor.setTarget("_blank");
        anchor.setWidthFull();
    }

    public NavigationBarLink(Class c, String id, boolean hiddenInLiteVersion) {
        this(c, id);
        this.hiddenInLiteVersion = hiddenInLiteVersion;
    }

    public NavigationBarLink(Class c, String id) {
        this.routerLink = new RouterLink(PageTitleFactory.getTitle(id), c);
    }

    public Optional<Anchor> getAnchor() {
        return Optional.ofNullable(anchor);
    }

    public Optional<RouterLink> getRouterLink() {
        return Optional.ofNullable(routerLink);
    }

    public String getLabel() {
        if (getRouterLink().isPresent()) return getRouterLink().get().getText();
        else if (getAnchor().isPresent()) return getAnchor().get().getText();
        return "";
    }

    public boolean isHiddenInLiteVersion() {
        return hiddenInLiteVersion;
    }
}
