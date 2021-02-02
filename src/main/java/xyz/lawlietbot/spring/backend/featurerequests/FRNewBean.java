package xyz.lawlietbot.spring.backend.featurerequests;

public class FRNewBean {

    private String title;
    private String description;
    private boolean notify = true;

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

    public void setNotify(FRNewBean frNewBean, boolean b) {
        frNewBean.notify = b;
    }

    public boolean getNotify(FRNewBean frNewBean) {
        return frNewBean.notify;
    }

}
