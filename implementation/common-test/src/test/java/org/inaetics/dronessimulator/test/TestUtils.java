package org.inaetics.dronessimulator.test;

import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.junit.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TestUtils {
    public static void setField(Object target, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        List<Object> changedFields = doWithFields(target.getClass(),
                field -> {
                    field.setAccessible(true);
                    field.set(target, value);
                    return Optional.empty();
                },
                field1 -> field1.getName().equals(fieldname)
        );
        if (changedFields.isEmpty()) {
            throw new NoSuchFieldException();
        }
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
        if (results.isEmpty()) {
            throw new NoSuchFieldException();
        }
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
    private static <R> List<R> doWithFields(Class<?> aClass, FieldCallback<R> fc, FieldFilter ff) throws IllegalAccessException {
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

    public static Tuple<MockPublisher, MockSubscriber> getConnectedMockPubSub() {
        MockSubscriber subscriber = new MockSubscriber();
//        MockPublisher publisher = new MockPublisher() {
//            @Override
//            public void send(Topic topic, Message message) throws IOException {
//                super.send(topic, message);
//                subscriber.receive(message);
//            }
//        };
        MockPublisher publisher = new MockPublisher() {
            @Override
            public void send(Object message) {
                super.send(message);
                subscriber.receive(message, null);
            }
        };
        return new Tuple<>(publisher, subscriber);
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
