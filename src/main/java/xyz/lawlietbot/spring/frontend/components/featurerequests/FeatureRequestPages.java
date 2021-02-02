package xyz.lawlietbot.spring.frontend.components.featurerequests;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FeatureRequestPages extends VerticalLayout {

    private final HorizontalLayout pageLayout = new HorizontalLayout();
    private final OnPageChange onPageChange;

    public FeatureRequestPages(OnPageChange onPageChange) {
        this.onPageChange = onPageChange;

        setPadding(false);
        setAlignItems(Alignment.CENTER);

        addSeperator();
        pageLayout.setPadding(false);
        add(pageLayout);
    }

    private void addSeperator() {
        Hr hr = new Hr();
        hr.getStyle().set("margin-top", "16px");
        add(hr);
    }

    public void setPage(int page, int pageSize) {
        pageLayout.removeAll();
        for(int i = 0; i < pageSize; i++) {
            Button button = new Button(String.valueOf(i + 1));
            button.setDisableOnClick(true);
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            if (page == i) {
                button.setEnabled(false);
            } else {
                int finalI = i;
                button.addClickListener(click -> {
                    UI.getCurrent().getPage().executeJs("scrollToTop()");
                    onPageChange.onPageChange(finalI);
                });
            }

            pageLayout.add(button);
        }
    }

    public interface OnPageChange {

        void onPageChange(int newPage);

    }

}
