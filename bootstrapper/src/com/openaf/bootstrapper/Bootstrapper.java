package com.openaf.bootstrapper;

public class Bootstrapper {
    public static void main(String[] args) {
        System.out.println("WE HAVE STARTED");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
