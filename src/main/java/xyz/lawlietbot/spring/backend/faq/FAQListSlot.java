package xyz.lawlietbot.spring.backend.faq;

import xyz.lawlietbot.spring.backend.LanguageString;

public class FAQListSlot {

    private LanguageString question = new LanguageString();
    private LanguageString answer = new LanguageString();

    public LanguageString getQuestion() { return question; }

    public LanguageString getAnswer() { return answer; }

}
