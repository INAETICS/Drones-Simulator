package org.inaetics.dronessimulator.test.hamcrest.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class StringMatchesUUIDPattern extends StringMatchesPattern {
    public static final String UUID_REGEX = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

    public StringMatchesUUIDPattern() {
        super(UUID_REGEX);
    }

    @Factory
    public static Matcher<String> matchesThePatternOfAUUID() {
        return new StringMatchesUUIDPattern();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching the pattern of a UUID");
    }
}

