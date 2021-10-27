package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import xyz.lawlietbot.spring.frontend.Styles;

public class ConfirmationDialog extends Div {

    private ConfirmationDialogConfirmListener confirmListener = null;
    private ConfirmationDialogCancelListener cancelListener = null;
    private final VerticalLayout dialogLayout = new VerticalLayout();
    private boolean opened = false;

    public ConfirmationDialog() {
        setSizeFull();
        getStyle().set("position", "fixed")
                .set("background-color", "rgba(0, 0, 0, 0.5)")
                .set("z-index", "7")
                .set("left", "0")
                .set("top", "0")
                .set("transition", "opacity 0.2s")
                .set("opacity", "0")
                .set("pointer-events", "none");;
        addClickListener(e -> close(false));

        dialogLayout.setMaxWidth("min(500px, calc(100% - 32px))");
        dialogLayout.setWidthFull();
        dialogLayout.setMinHeight("200px");
        dialogLayout.addClassName(Styles.CENTER_FIXED_FULL);
        dialogLayout.getStyle().set("position", "fixed")
                .set("background-color", "white")
                .set("z-index", "8")
                .set("border-radius", "5px")
                .set("padding", "32px");
        add(dialogLayout);
    }

    private Component generateText(String text) {
        Label textParagraph = new Label(text);
        textParagraph.setWidthFull();
        textParagraph.getStyle().set("color", "black");
        return textParagraph;
    }

    private Component generateButtons(boolean withCancelButton) {
        FlexLayout buttonLayout = new FlexLayout();
        buttonLayout.getStyle().set("flex-direction", "row-reverse");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();

        if (withCancelButton) {
            Button buttonCancel = new Button(getTranslation("dialog.cancel"), e -> close(false));
            buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            buttonCancel.setWidth("100px");
            buttonCancel.addClickShortcut(Key.ESCAPE);
            buttonLayout.add(buttonCancel);
        }

        Button buttonConfirm = new Button(getTranslation("dialog.confirm"), e -> close(true));
        buttonConfirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonConfirm.setWidth("100px");
        buttonLayout.add(buttonConfirm);

        return buttonLayout;
    }

    public void open(String text, ConfirmationDialogConfirmListener confirmListener, Component... components) {
        open(text, confirmListener, null, components);
    }

    public void open(String text, ConfirmationDialogConfirmListener confirmListener, ConfirmationDialogCancelListener cancelListener, Component... components) {
        if (!opened) {
            this.confirmListener = confirmListener;
            this.cancelListener = cancelListener;

            this.dialogLayout.removeAll();
            Component textComponent = generateText(text);
            this.dialogLayout.add(textComponent);

            if (components != null && components.length > 0) {
                this.dialogLayout.add(components);
            }
            this.dialogLayout.add(generateButtons(cancelListener != null));
            this.dialogLayout.setFlexGrow(1, textComponent);

            getStyle().set("opacity", "1")
                    .set("pointer-events", "all");
            this.opened = true;
        }
    }

    public void close() {
        close(false);
    }

    private void close(boolean confirmed) {
        if (opened) {
            opened = false;
            getStyle().set("opacity", "0")
                    .set("pointer-events", "none");
            if (confirmed) {
                if (confirmListener != null) {
                    confirmListener.onConfirm();
                }
            } else {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
            }
        }
    }

    public boolean isOpened() {
        return opened;
    }


    public interface ConfirmationDialogConfirmListener {

        void onConfirm();

    }

    public interface ConfirmationDialogCancelListener {

        void onCancel();

    }

}
