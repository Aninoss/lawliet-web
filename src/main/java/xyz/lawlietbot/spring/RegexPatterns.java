package xyz.lawlietbot.spring;

import java.util.regex.Pattern;

public interface RegexPatterns {

    Pattern TEXT_PLACEHOLDER_PATTERN = Pattern.compile("\\{(?<inner>[^}]*)}");
    Pattern TEXT_MULTIOPTION_PATTERN = Pattern.compile("(?<!\\\\)\\[(?<inner>[^]]*)]");

}
