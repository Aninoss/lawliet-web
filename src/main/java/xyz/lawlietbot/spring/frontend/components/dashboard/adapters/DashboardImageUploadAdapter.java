package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import dashboard.DashboardComponent;
import dashboard.component.DashboardImageUpload;
import org.vaadin.addons.componentfactory.css.grid.GridLayoutComponent;
import org.vaadin.addons.componentfactory.css.grid.sizes.Flex;
import org.vaadin.addons.componentfactory.css.grid.sizes.Length;
import org.vaadin.addons.componentfactory.css.grid.sizes.MinMax;
import org.vaadin.addons.componentfactory.css.grid.sizes.Repeat;
import org.vaadin.addons.componentfactory.layout.FlexibleGridLayout;
import xyz.lawlietbot.spring.backend.FileCache;
import xyz.lawlietbot.spring.backend.report.ContentType;
import xyz.lawlietbot.spring.backend.util.FileUtil;
import xyz.lawlietbot.spring.backend.util.RandomUtil;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class DashboardImageUploadAdapter extends VerticalLayout implements DashboardAdapter<DashboardImageUpload> {

    private DashboardImageUpload dashboardImageUpload;
    private Upload upload;
    private Div div = null;
    private final FlexibleGridLayout flexibleGridLayout = new FlexibleGridLayout();
    private final FileCache fileCache;

    public DashboardImageUploadAdapter(DashboardImageUpload dashboardImageUpload, FileCache fileCache) {
        this.fileCache = fileCache;
        setPadding(false);

        flexibleGridLayout.setVisible(false);
        add(flexibleGridLayout);

        this.dashboardImageUpload = dashboardImageUpload;
        resetUpload(dashboardImageUpload.getMax());
        updateValuesField();
    }

    @Override
    public void update(DashboardImageUpload dashboardImageUpload) {
        DashboardComponent previousDashboardComponent = this.dashboardImageUpload;
        this.dashboardImageUpload = dashboardImageUpload;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardImageUpload)) {
            return;
        }

        upload.setMaxFiles(dashboardImageUpload.getMax());
        updateValuesField();
    }

    private void resetUpload(int maxFiles) {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        upload = new Upload(buffer);
        upload.setMaxFileSize(100_000_000);
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/bmp", "image/gif", ".png", ".jpg", ".jpeg", ".bmp", ".gif");
        upload.setWidth("calc(100% - 32px)");
        upload.setMaxFiles(maxFiles);

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            CustomNotification.showError(errorMessage);
        });

        ArrayList<String> uploads = new ArrayList<>();
        upload.addSucceededListener(event -> {
            ContentType contentType = ContentType.parseFromUrl(event.getFileName());
            if (contentType == null || contentType.isVideo()) {
                return;
            }

            String dir = this.dashboardImageUpload.getDir();
            InputStream inputStream = buffer.getInputStream(event.getFileName());

            String[] fileParts = event.getFileName().split("\\.");
            String fileExt = fileParts[fileParts.length - 1];
            String fileName = RandomUtil.generateRandomString(30) + "." + fileExt;
            String fileNameFull = System.getenv("DASHBOARD_IMAGE_PATH") + "/" + dir + "/" + fileName;

            File file = new File(fileNameFull);
            if (FileUtil.writeInputStreamToFile(inputStream, file)) {
                fileCache.addFile(file);
                uploads.add("https://lawlietbot.xyz/cdn/" + dir + "/" + fileName);
            } else {
                CustomNotification.showError(getTranslation("error"));
            }
        });
        upload.addAllFinishedListener(event -> {
            this.dashboardImageUpload.triggerAdd(uploads);
            resetUpload(upload.getMaxFiles());
        });

        if (div != null) {
            remove(div);
        }
        div = new Div(new Label(dashboardImageUpload.getLabel()), upload);
        div.setWidthFull();
        addComponentAsFirst(div);
    }

    private void updateValuesField() {
        ArrayList<Div> items = new ArrayList<>();
        for (String value : dashboardImageUpload.getValues()) {
            Div div = new Div();
            div.addClassName("dashboard-image-cell");
            div.setMinHeight("200px");
            div.getStyle().set("background-image", "url(\"" + value + "\")");

            Button closeButton = new Button(VaadinIcon.CLOSE.create());
            closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            closeButton.addClassName("dashboard-image-cell-button");
            closeButton.addClickListener(e -> dashboardImageUpload.triggerRemove(value));
            div.add(closeButton);

            items.add(div);
        }

        flexibleGridLayout.removeAll();
        flexibleGridLayout
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("200px"), new Flex(1)))
                .withItems(items.toArray(new Div[0]))
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);
        flexibleGridLayout.setWidthFull();
        flexibleGridLayout.setVisible(!dashboardImageUpload.getValues().isEmpty());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardImageUpload)) {
            return false;
        }

        DashboardImageUpload dashboardImageUpload = (DashboardImageUpload) dashboardComponent;
        return Objects.equals(this.dashboardImageUpload.getLabel(), dashboardImageUpload.getLabel());
    }

}
