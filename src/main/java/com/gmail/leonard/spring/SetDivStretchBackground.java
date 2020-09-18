package com.gmail.leonard.spring;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SetDivStretchBackground {

    String background();

}