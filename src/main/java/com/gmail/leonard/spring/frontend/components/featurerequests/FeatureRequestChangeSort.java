package com.gmail.leonard.spring.frontend.components.featurerequests;

import com.gmail.leonard.spring.backend.util.StringUtil;
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

    private Label pageIndicator;
    private Label pageIndicatorMobile;

    public FeatureRequestChangeSort(OnSortChange listener, OnPagePrevious onPagePrevious, OnPageNext onPageNext, FeatureRequestSort[] comparators, FeatureRequestSort sortDefault) {
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        setWidthFull();

        addPageIndicator(onPagePrevious, onPageNext);
        Div emptyDiv = new Div();
        add(emptyDiv);
        addDropdownMenu(listener, comparators, sortDefault);
        setFlexGrow(1, emptyDiv);
    }

    private void addPageIndicator(OnPagePrevious onPagePrevious, OnPageNext onPageNext) {
        Button buttonPrevious = new Button(VaadinIcon.ARROW_LEFT.create(), click -> onPagePrevious.onPagePrevious());
        buttonPrevious.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button buttonNext = new Button(VaadinIcon.ARROW_RIGHT.create(), click -> onPageNext.onPageNext());
        buttonNext.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        pageIndicator = new Label("");
        pageIndicator.addClassNames(Styles.VISIBLE_NOTMOBILE);
        pageIndicatorMobile = new Label("");
        pageIndicatorMobile.addClassNames(Styles.VISIBLE_MOBILE);
        add(buttonPrevious, pageIndicator, pageIndicatorMobile, buttonNext);
    }

    private void addDropdownMenu(OnSortChange listener, FeatureRequestSort[] comparators, FeatureRequestSort sortDefault) {
        Label label = new Label(getTranslation("fr.sort.label"));
        label.addClassName(Styles.VISIBLE_NOTMOBILE);
        add(label);
        String[] options = getTranslation("fr.sort.options").split("\n");

        Select<String> labelSelect = new Select<>();
        labelSelect.setItems(options);
        labelSelect.setValue(options[getSortIndex(comparators, sortDefault)]);
        labelSelect.addValueChangeListener(selected -> listener.onSortChange(comparators[getIndexOfValue(options, selected.getValue())]));
        add(labelSelect);
    }

    private int getSortIndex(FeatureRequestSort[] comparators, FeatureRequestSort sortDefault) {
        for (int i = 0; i < comparators.length; i++) {
            if (comparators[i].getId().equals(sortDefault.getId()))
                return i;
        }

        return 0;
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
        pageIndicator.setText(getTranslation("page", StringUtil.numToString(page + 1), StringUtil.numToString(pageSize)));
        pageIndicatorMobile.setText(StringUtil.numToString(page + 1));
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
