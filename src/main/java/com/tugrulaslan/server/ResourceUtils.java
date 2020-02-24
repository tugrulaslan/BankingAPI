package com.tugrulaslan.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ResourceUtils {

    private static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();

    public static String read(String resourceName) throws IOException {
        ClassLoader classLoader = ResourceUtils.class.getClassLoader();
        if (classLoader.getResource(resourceName) == null) {
            throw new IOException(String.format("Resource not found '%s'", resourceName));
        }
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append(SYSTEM_LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}