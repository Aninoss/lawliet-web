package com.gmail.leonard.spring.frontend.components;

import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmationDialog extends Div {

    private final ConfirmationDialogConfirmListener confirmListener;
    private ConfirmationDialogCancelListener cancelListener = null;
    private final VerticalLayout dialogLayout = new VerticalLayout();
    private Label textParagraph;
    private boolean confirm = false;
    private boolean opened = true;

    public ConfirmationDialog(String text, ConfirmationDialogConfirmListener confirmListener, ConfirmationDialogCancelListener cancelListener, Component... components) {
        this(text, confirmListener, components);
        this.cancelListener = cancelListener;
    }

    public ConfirmationDialog(String text, ConfirmationDialogConfirmListener confirmListener, Component... components) {
        this.confirmListener = confirmListener;
        setBackgroundStyles();
        setDialogStyles();
        getStyle().set("transition", "opacity 0.2s");

        addText(text);
        if (components.length > 0) dialogLayout.add(components);
        addButtons();
        dialogLayout.setFlexGrow(1, textParagraph);
        add(dialogLayout);
    }

    private void setBackgroundStyles() {
        getStyle().set("position", "fixed")
                .set("background-color", "rgba(0, 0, 0, 0.5)")
                .set("z-index", "7")
                .set("left", "0")
                .set("top", "0");
        setSizeFull();

        addClickListener(e -> close());
    }

    private void setDialogStyles() {
        dialogLayout.setMaxWidth("min(500px, calc(100% - 32px))");
        dialogLayout.setWidthFull();
        dialogLayout.setMinHeight("200px");
        dialogLayout.getStyle().set("position", "fixed")
                .set("background-color", "white")
                .set("z-index", "8")
                .set("border-radius", "5px")
                .set("padding", "32px");
        dialogLayout.addClassName(Styles.CENTER_FIXED_FULL);
        close();
    }

    private void addText(String text) {
        textParagraph = new Label(text);
        textParagraph.setWidthFull();
        textParagraph.getStyle().set("color", "black");

        dialogLayout.add(textParagraph);
    }

    private void addButtons() {
        FlexLayout buttonLayout = new FlexLayout();
        buttonLayout.getStyle().set("flex-direction", "row-reverse");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();

        Button buttonCancel = new Button(getTranslation("dialog.cancel"), e -> onCancel());
        buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonCancel.setWidth("100px");
        buttonCancel.addClickShortcut(Key.ESCAPE);
        buttonLayout.add(buttonCancel);

        Button buttonConfirm = new Button(getTranslation("dialog.confirm"), e -> onConfirm());
        buttonConfirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonConfirm.setWidth("100px");
        buttonLayout.add(buttonConfirm);

        dialogLayout.add(buttonLayout);
    }

    private void onConfirm() {
        if (opened) {
            confirm = true;
            close();
        }
    }

    private void onCancel() {
        close();
    }

    public void open() {
        if (!opened) {
            confirm = false;
            getStyle().set("opacity", "1")
                    .set("pointer-events", "all");
            opened = true;
        }
    }

    public void close() {
        if (opened) {
            opened = false;
            getStyle().set("opacity", "0")
                    .set("pointer-events", "none");
            if (confirm) {
                confirmListener.onConfirm();
            } else {
                if (cancelListener != null) cancelListener.onCancel();
            }
        }
    }


    public interface ConfirmationDialogConfirmListener {
        void onConfirm();
    }

    public interface ConfirmationDialogCancelListener {
        void onCancel();
    }

}
