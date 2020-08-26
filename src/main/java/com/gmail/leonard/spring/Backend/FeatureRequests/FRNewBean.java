package com.gmail.leonard.spring.Backend.FeatureRequests;

public class FRNewBean {

    private String title, description;

    public String getTitle(FRNewBean frNewBean) {
        return frNewBean.title;
    }

    public void setTitle(FRNewBean frNewBean, String s) {
        frNewBean.title = s;
    }

    public String getDescription(FRNewBean frNewBean) {
        return frNewBean.description;
    }

    public void setDescription(FRNewBean frNewBean, String s) {
        frNewBean.description = s;
    }

}
