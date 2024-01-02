package com.sandesh.overall.service;

public class CaptureOutputService {

    public static void checkAuth() {
        if (Boolean.getBoolean("auth.required")) {
            System.out.println("AUTH: Required");
        }
    }
}
