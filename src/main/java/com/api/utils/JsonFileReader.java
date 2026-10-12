package com.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Reads JSON fixture files from the test classpath (src/test/resources/...)
 * and either returns them raw or deserializes them into a POJO via Jackson.
 *
 * Keeping request bodies as JSON files instead of Java constructors means
 * new test data doesn't require touching any Java code - just drop a new
 * .json file under testdata/ and point a Gherkin step at it.
 */
public class JsonFileReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** Deserializes a classpath JSON file into an instance of the given POJO class. */
    public static <T> T readAsObject(String classpathResource, Class<T> clazz) {
        try (InputStream input = openStream(classpathResource)) {
            return objectMapper.readValue(input, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON file as " + clazz.getSimpleName()
                    + ": " + classpathResource, e);
        }
    }

    /** Returns the raw JSON text of a classpath file, e.g. to send as-is or to run JSON path checks against. */
    public static String readAsString(String classpathResource) {
        try (InputStream input = openStream(classpathResource)) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + classpathResource, e);
        }
    }

    private static InputStream openStream(String classpathResource) {
        InputStream input = JsonFileReader.class.getClassLoader().getResourceAsStream(classpathResource);
        if (input == null) {
            throw new IllegalArgumentException(
                    "JSON file not found on classpath (expected under src/test/resources): " + classpathResource);
        }
        return input;
    }
}
