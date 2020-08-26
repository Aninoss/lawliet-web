package com.gmail.leonard.spring.Backend.Feedback;

public class FeedbackBean {

    private String cause, reason;
    private boolean serverDetails;

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

    public Boolean getServerDetails(FeedbackBean feedbackBean) {
        return feedbackBean.serverDetails;
    }

    public void setServerDetails(FeedbackBean feedbackBean, Boolean aBoolean) {
        feedbackBean.serverDetails = aBoolean;
    }

}
