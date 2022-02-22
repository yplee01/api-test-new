package com.example1.springh2test;

public class TestClass2 {
    private static TestClass2 instance;

    private TestClass2() {
    }
    public static TestClass2 getInstance() {
        if(instance == null) {
            instance = new TestClass2();
        }
       return instance;
    }


}
