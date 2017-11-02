package org.inaetics.dronessimulator.test.hamcrest.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class StringMatchesPattern extends TypeSafeMatcher<String> {

    private final String pattern;

    public StringMatchesPattern(String pattern) {
        this.pattern = pattern;
    }

    @Factory
    public static Matcher<String> matches(String pattern) {
        return new StringMatchesPattern(pattern);
    }

    @Override
    protected boolean matchesSafely(String s) {
        return s.matches(pattern);
    }


    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching the pattern " + pattern);
    }
}