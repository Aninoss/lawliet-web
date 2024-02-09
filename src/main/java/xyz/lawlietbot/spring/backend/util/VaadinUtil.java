package xyz.lawlietbot.spring.backend.util;

import com.vaadin.flow.component.UI;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

public class VaadinUtil {

    public static ZoneId getCurrentZoneId() {
        AtomicReference<ZoneId> zoneId = new AtomicReference<>(ZoneId.systemDefault());
        UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
            if (details.getTimeZoneId() == null || details.getTimeZoneId().isEmpty()) {
                zoneId.set(ZoneId.of(details.getTimeZoneId()));
            } else {
                zoneId.set(ZoneOffset.ofTotalSeconds(details.getTimezoneOffset() / 1000));
            }
        });

        return zoneId.get();
    }

}
