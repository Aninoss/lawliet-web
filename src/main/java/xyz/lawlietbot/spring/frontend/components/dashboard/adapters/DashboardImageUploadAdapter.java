package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import java.io.File;
import java.io.InputStream;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import dashboard.component.DashboardImageUpload;
import xyz.lawlietbot.spring.backend.util.FileUtil;
import xyz.lawlietbot.spring.backend.util.RandomUtil;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;

public class DashboardImageUploadAdapter extends Div {

    public DashboardImageUploadAdapter(DashboardImageUpload dashboardImageUpload) {
        String dir = dashboardImageUpload.getDir();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setMaxFileSize(100_000_000);
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/bmp", "image/gif", ".png", ".jpg", ".jpeg", ".bmp", ".gif");
        upload.setWidth("calc(100% - 32px)");

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            CustomNotification.showError(errorMessage);
        });

        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            String[] fileParts = event.getFileName().split("\\.");
            String fileExt = fileParts[fileParts.length - 1];
            String fileName = RandomUtil.generateRandomString(30) + "." + fileExt;
            String fileNameFull = System.getenv("DASHBOARD_IMAGE_PATH") + "/" + dir + "/" + fileName;
            if (FileUtil.writeInputStreamToFile(inputStream, new File(fileNameFull))) {
                dashboardImageUpload.trigger("https://lawlietbot.xyz/cdn/" + dir + "/" + fileName);
            } else {
                CustomNotification.showError(getTranslation("error"));
            }
        });

        Label label = new Label(dashboardImageUpload.getLabel());
        add(label, upload);
    }

}
