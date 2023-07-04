package com.bendcap.enkel.compiler.test;

public class Client {
    public void start() {
        String var1 = "someString";
        System.out.println(var1 + " to upper case : " + var1.toUpperCase());
        System.out.println("Client: Calling my own 'Library' class:");
        Library var2 = new Library();
        int var3 = var2.add(5, 2);
        System.out.println("Client: Result returned from 'Library.add' = " + var3);
    }

    public Client() {
    }

    public static void main(String[] var0) {
        (new Client()).start();
    }
}
