package org.inaetics.dronessimulator.test;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TestUtils {
    public static void setField(Object target, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        doWithFields(target.getClass(),
                field -> {
                    field.setAccessible(true);
                    field.set(target, value);
                    return Optional.empty();
                },
                field1 -> field1.getName().equals(fieldname)
        );
    }

    public static <R> R getField(Object target, String fieldname) throws IllegalAccessException, NoSuchFieldException {
        List<R> results = doWithFields(target.getClass(),
                field -> {
                    try {
                        field.setAccessible(true);
                        //noinspection unchecked since we use a try-catch
                        return (R) field.get(target);
                    } catch (ClassCastException e) {
                        return null;
                    }
                },
                field1 -> field1.getName().equals(fieldname)
        );
        Optional<R> result = results.stream().findFirst();
        return result.orElse(null);
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the class hierarchy to get all declared
     * fields.
     *
     * @param aClass - the target class to analyze
     * @param fc     - the callback to invoke for each field
     * @param ff     - the filter that determines the fields to apply the callback to
     */
    private static <R> List<R> doWithFields(Class<?> aClass, FieldCallback<R> fc, FieldFilter ff) throws
            IllegalAccessException {
        Class<?> i = aClass;
        List<R> results = new LinkedList<>();
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic() && ff.matches(field)) {
                    results.add(fc.doWith(field));
                }
            }
            i = i.getSuperclass();
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrows(Class<T> expectedException, Code codeToRun) throws AssertionError {
        try {
            codeToRun.run();
            Assert.fail("Expected " + expectedException.getSimpleName() + " to be thrown, but no exception was thrown");
        } catch (Exception e) {
            if (expectedException.isInstance(e)) {
                return (T) e;
            } else {
                Assert.fail("Expected " + expectedException.getSimpleName() + " to be thrown, but got: " + e.getClass().getSimpleName() + ". With message: " + e.getMessage());
            }
        }
        return null;
    }


    @FunctionalInterface
    private interface FieldFilter {
        boolean matches(final Field field);
    }

    @FunctionalInterface
    private interface FieldCallback<R> {
        R doWith(final Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    public interface Code {
        void run() throws Exception;
    }
}
