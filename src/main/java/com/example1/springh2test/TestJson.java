package com.example1.springh2test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TestJson {
    Integer n1;
    Integer n2;
    String line;

    @Override
    public String toString() {
        return "TestJson{" +
                "n1=" + n1 +
                ", n2=" + n2 +
                ", line='" + line + '\'' +
                '}';
    }
}
