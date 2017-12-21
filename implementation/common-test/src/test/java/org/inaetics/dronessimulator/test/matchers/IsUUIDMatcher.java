package org.inaetics.dronessimulator.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsUUIDMatcher extends TypeSafeMatcher<String> {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

    @Factory
    public static Matcher<String> isUUID() {
        return new IsUUIDMatcher();
    }

    public boolean matchesSafely(String item) {
        return item.matches(UUID_REGEX);
    }

    public void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendValue(item).appendText(" does not match a UUID pattern");
    }

    public void describeTo(Description description) {
        description.appendText("a string matching the pattern of a UUID");
    }
}
