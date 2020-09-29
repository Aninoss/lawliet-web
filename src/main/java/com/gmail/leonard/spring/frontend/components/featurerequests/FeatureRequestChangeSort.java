package com.gmail.leonard.spring.frontend.components.featurerequests;

import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.frontend.Styles;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;

public class FeatureRequestChangeSort extends HorizontalLayout {

    private final FeatureRequestSort[] comparators = new FeatureRequestSort[] {
            new FeatureRequestSortByPopular(),
            new FeatureRequestSortByBoosts(),
            new FeatureRequestSortByNewest(),
            new FeatureRequestSortByTitle()
    };

    private Label pageIndicator;

    public FeatureRequestChangeSort(OnSortChange listener, OnPagePrevious onPagePrevious, OnPageNext onPageNext) {
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        setWidthFull();

        addPageIndicator(onPagePrevious, onPageNext);
        Div emptyDiv = new Div();
        add(emptyDiv);
        addDropdownMenu(listener);
        setFlexGrow(1, emptyDiv);
    }

    private void addPageIndicator(OnPagePrevious onPagePrevious, OnPageNext onPageNext) {
        Button buttonPrevious = new Button(VaadinIcon.ARROW_LEFT.create(), click -> onPagePrevious.onPagePrevious());
        buttonPrevious.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button buttonNext = new Button(VaadinIcon.ARROW_RIGHT.create(), click -> onPageNext.onPageNext());
        buttonNext.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        pageIndicator = new Label("");
        add(buttonPrevious, pageIndicator, buttonNext);
    }

    private void addDropdownMenu(OnSortChange listener) {
        Label label = new Label(getTranslation("fr.sort.label"));
        label.addClassName(Styles.VISIBLE_NOTMOBILE);
        add(label);
        String[] options = getTranslation("fr.sort.options").split("\n");

        Select<String> labelSelect = new Select<>();
        labelSelect.setItems(options);
        labelSelect.setValue(options[0]);
        labelSelect.addValueChangeListener(selected -> listener.onSortChange(comparators[getIndexOfValue(options, selected.getValue())]));
        add(labelSelect);
    }

    private int getIndexOfValue(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            String valueAt = values[i];
            if (valueAt.equals(value))
                return i;
        }

        return 0;
    }

    public void onPageChanged(int page, int pageSize) {
        pageIndicator.setText(getTranslation("page", StringUtil.numToString(getLocale(), page + 1), StringUtil.numToString(getLocale(), pageSize)));
    }

    public interface OnSortChange {
        void onSortChange(FeatureRequestSort newSort);
    }

    public interface OnPagePrevious {
        void onPagePrevious();
    }

    public interface OnPageNext {
        void onPageNext();
    }

}
