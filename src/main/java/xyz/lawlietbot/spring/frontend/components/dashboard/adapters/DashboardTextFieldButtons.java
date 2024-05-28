package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DashboardTextFieldButtons extends FlexLayout {

    private final boolean useEnterShortcut;
    private Runnable cancelListener = () -> {};
    private Supplier<Boolean> confirmListener = () -> false;
    private Consumer<Boolean> modeChangeListener = e -> {};

    public DashboardTextFieldButtons(boolean useEnterShortcut) {
        this.useEnterShortcut = useEnterShortcut;
        getStyle().set("margin-top", "16px")
                .set("margin-left", "8px");
        setWidth("75px");
        setFlexDirection(FlexDirection.ROW);
        changeMode(false);
    }

    public void setCancelListener(Runnable cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(Supplier<Boolean> confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setModeChangeListener(Consumer<Boolean> modeChangeListener) {
        this.modeChangeListener = modeChangeListener;
    }

    private void changeMode(boolean editMode) {
        removeAll();
        modeChangeListener.accept(editMode);

        if (editMode) {
            Button saveButton = new Button(VaadinIcon.CHECK.create());
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            saveButton.setWidthFull();
            if (useEnterShortcut) {
                saveButton.addClickShortcut(Key.ENTER);
            }
            saveButton.addClickListener(e -> {
                if (confirmListener.get()) {
                    changeMode(false);
                }
            });

            Button resetButton = new Button(VaadinIcon.CLOSE.create());
            resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetButton.setWidthFull();
            resetButton.addClickListener(e -> {
                cancelListener.run();
                changeMode(false);
            });
            resetButton.setWidthFull();

            add(saveButton, resetButton);
        } else {
            Button editButton = new Button(getTranslation("dash.edit"));
            editButton.setWidthFull();
            editButton.addClickListener(e -> changeMode(true));
            add(editButton);
        }
    }

}
