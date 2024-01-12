package xyz.lawlietbot.spring.frontend.components.featurerequests;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        List<Integer> pages;
        if (pageSize <= 9) {
            pages = IntStream.range(0, pageSize).boxed().collect(Collectors.toList());
        } else {
            pages = new ArrayList<>();

            if (page <= 4) {
                pages.addAll(IntStream.rangeClosed(0, page).boxed().collect(Collectors.toList()));
            } else {
                pages.addAll(List.of(0, -1));
                int minPage = Math.min(page - 2, pageSize - 7);
                for (int i = minPage; i <= page; i++) {
                    pages.add(i);
                }
            }

            if (page >= pageSize - 5) {
                pages.addAll(IntStream.range(page + 1, pageSize).boxed().collect(Collectors.toList()));
            } else {
                int maxPage = Math.max(page + 2, 6);
                for (int i = page + 1; i <= maxPage; i++) {
                    pages.add(i);
                }
                pages.addAll(List.of(-1, pageSize - 1));
            }
        }

        for (int i : pages) {
            Button button = new Button(String.valueOf(i + 1));
            button.setDisableOnClick(true);
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            if (i < 0) {
                button.setText("…");
                button.setEnabled(false);
            } else if (page == i) {
                button.setEnabled(false);
            } else {
                button.addClickListener(click -> {
                    UI.getCurrent().getPage().executeJs("scrollToTop()");
                    onPageChange.onPageChange(i);
                });
            }

            pageLayout.add(button);
        }
    }

    public interface OnPageChange {

        void onPageChange(int newPage);

    }

}
