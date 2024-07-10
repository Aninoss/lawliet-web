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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfirmationDialog extends Div {

    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private ConfirmationDialogConfirmListener confirmListener = null;
    private ConfirmationDialogCancelListener cancelListener = null;
    private final VerticalLayout dialogLayout = new VerticalLayout();
    private boolean opened = false;
    private boolean canInteract = true;
    private boolean triggerConfirmListenerOnClose = false;

    public ConfirmationDialog() {
        setSizeFull();
        getStyle().set("position", "fixed")
                .set("background-color", "rgba(0, 0, 0, 0.5)")
                .set("z-index", "7")
                .set("left", "0")
                .set("top", "0")
                .set("transition", "opacity 0.2s")
                .set("opacity", "0")
                .set("pointer-events", "none");

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
        dialogLayout.getElement().addEventListener("click", ignore -> {}).addEventData("event.stopPropagation()");
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

    public void open(String text, ConfirmationDialogConfirmListener confirmListener) {
        open(text, confirmListener, null);
    }

    public void open(Component component, ConfirmationDialogConfirmListener confirmListener) {
        open(component, confirmListener, null);
    }

    public void open(String text, ConfirmationDialogConfirmListener confirmListener, ConfirmationDialogCancelListener cancelListener) {
        open(generateText(text), confirmListener, cancelListener);
    }

    public void open(Component component, ConfirmationDialogConfirmListener confirmListener, ConfirmationDialogCancelListener cancelListener) {
        if (!opened) {
            this.confirmListener = confirmListener;
            this.cancelListener = cancelListener;

            this.dialogLayout.removeAll();
            this.dialogLayout.add(component);

            this.dialogLayout.add(generateButtons(cancelListener != null));
            this.dialogLayout.setFlexGrow(1, component);

            getStyle().set("opacity", "1")
                    .set("pointer-events", "all");
            this.opened = true;

            canInteract = false;
            executor.schedule(() -> canInteract = true, 250, TimeUnit.MILLISECONDS);
        }
    }

    public void close() {
        close(false);
    }

    private void close(boolean confirmed) {
        if (opened && canInteract) {
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
                } else if (triggerConfirmListenerOnClose) {
                    confirmListener.onConfirm();
                }
            }
        }
    }

    public boolean isOpened() {
        return opened;
    }

    public void setTriggerConfirmListenerOnClose(boolean triggerConfirmListenerOnClose) {
        this.triggerConfirmListenerOnClose = triggerConfirmListenerOnClose;
    }

    public interface ConfirmationDialogConfirmListener {

        void onConfirm();

    }

    public interface ConfirmationDialogCancelListener {

        void onCancel();

    }

}
