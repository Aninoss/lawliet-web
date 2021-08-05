package xyz.lawlietbot.spring;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LoginAccess {

    boolean withGuilds() default false;

}