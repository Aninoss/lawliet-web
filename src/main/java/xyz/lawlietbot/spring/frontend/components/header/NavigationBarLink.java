package xyz.lawlietbot.spring.frontend.components.header;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouterLink;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.language.PageTitleGen;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

import java.util.Optional;

public class NavigationBarLink {

    private Anchor anchor;
    private RouterLink routerLink;
    private final boolean hiddenInLiteVersion;

    public NavigationBarLink(String externalLink, String id) {
        this(externalLink, id, false);
    }

    public NavigationBarLink(String externalLink, String id, boolean hiddenInLiteVersion) {
        anchor = new Anchor(externalLink, new Text(PageTitleGen.getTitle(id)));
        anchor.setTarget("_blank");
        anchor.setWidthFull();

        this.hiddenInLiteVersion = hiddenInLiteVersion;
    }

    public NavigationBarLink(Class<? extends PageLayout> page) {
        this.routerLink = new RouterLink(PageTitleGen.getTitle(PageLayout.getRouteStatic(page)), page);
        hiddenInLiteVersion = page.isAnnotationPresent(NoLiteAccess.class);
    }

    public NavigationBarLink focus(boolean focus) {
        if (focus && routerLink != null) {
            Icon icon = VaadinIcon.EXCLAMATION_CIRCLE.create();
            icon.addClassName("attention-circle");
            routerLink.addComponentAsFirst(icon);
        }
        return this;
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
