package com.gmail.leonard.spring.Backend.Feedback;

public class FeedbackBean {

    private String cause, reason, usernameDiscriminated;
    private boolean contact;

    public String getReason(FeedbackBean feedbackBean) {
        return feedbackBean.reason;
    }

    public void setReason(FeedbackBean feedbackBean, String s) {
        feedbackBean.reason = s;
    }

    public String getCause(FeedbackBean feedbackBean) {
        return feedbackBean.cause;
    }

    public void setCause(FeedbackBean feedbackBean, String s) {
        feedbackBean.cause = s;
    }

    public Boolean getContact(FeedbackBean feedbackBean) {
        return feedbackBean.contact;
    }

    public void setContact(FeedbackBean feedbackBean, Boolean aBoolean) {
        feedbackBean.contact = aBoolean;
    }

    public String getUsernameDiscriminated(FeedbackBean feedbackBean) {
        return feedbackBean.usernameDiscriminated;
    }

    public void setUsernameDiscriminated(FeedbackBean feedbackBean, String s) {
        feedbackBean.usernameDiscriminated = s;
    }

}
