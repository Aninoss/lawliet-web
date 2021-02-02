package xyz.lawlietbot.spring.syncserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SyncServerEvent {

    String event();

}
