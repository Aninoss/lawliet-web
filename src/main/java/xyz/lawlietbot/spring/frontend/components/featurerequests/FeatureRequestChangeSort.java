package xyz.lawlietbot.spring.frontend.components.featurerequests;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import xyz.lawlietbot.spring.frontend.components.featurerequests.sort.FeatureRequestSort;

public class FeatureRequestChangeSort extends VerticalLayout {

    private final HorizontalLayout content = new HorizontalLayout();
    private Label pageIndicator;
    private Label pageIndicatorMobile;

    public FeatureRequestChangeSort(OnSortChange listener, OnPagePrevious onPagePrevious, OnPageNext onPageNext,
                                    OnSearch onSearch, FeatureRequestSort[] comparators, FeatureRequestSort sortDefault,
                                    String search
    ) {
        setWidthFull();
        setPadding(false);
        getStyle().set("margin-top", "12px");

        content.setWidthFull();
        content.setPadding(false);
        content.setAlignItems(Alignment.CENTER);

        TextField searchField = generateSearchField(search, onSearch);
        searchField.setWidthFull();
        searchField.addClassName(Styles.VISIBLE_MOBILE);
        searchField.getStyle().set("margin-bottom", "-12px");
        add(searchField);

        addPageIndicator(onPagePrevious, onPageNext);
        addSearchField(search, onSearch);
        addDropdownMenu(listener, comparators, sortDefault);
        add(content);
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
        content.add(buttonPrevious, pageIndicator, pageIndicatorMobile, buttonNext);
    }

    private void addSearchField(String search, OnSearch onSearch) {
        Div div = new Div();

        TextField searchField = generateSearchField(search, onSearch);
        searchField.addClassName(Styles.VISIBLE_NOTMOBILE);
        div.add(searchField);

        content.add(div);
        content.setFlexGrow(1, div);
    }

    private TextField generateSearchField(String search, OnSearch onSearch) {
        TextField searchField = new TextField();
        searchField.setValue(search);
        searchField.setWidthFull();
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setPlaceholder(getTranslation("commands.search"));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setValueChangeTimeout(500);
        searchField.addValueChangeListener(event -> onSearch.onSearch(event.getValue()));
        return searchField;
    }

    private void addDropdownMenu(OnSortChange listener, FeatureRequestSort[] comparators, FeatureRequestSort sortDefault) {
        Label label = new Label(getTranslation("fr.sort.label"));
        label.addClassName(Styles.VISIBLE_NOTMOBILE);
        content.add(label);
        String[] options = getTranslation("fr.sort.options").split("\n");

        Select<String> labelSelect = new Select<>();
        labelSelect.setItems(options);
        labelSelect.setValue(options[getSortIndex(comparators, sortDefault)]);
        labelSelect.addValueChangeListener(selected -> listener.onSortChange(comparators[getIndexOfValue(options, selected.getValue())]));
        content.add(labelSelect);
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

    public interface OnSearch {
        void onSearch(String search);
    }

}
