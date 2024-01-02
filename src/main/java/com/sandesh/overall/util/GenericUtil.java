package com.sandesh.overall.util;

import lombok.SneakyThrows;

public class GenericUtil {

    @SneakyThrows
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }
}
