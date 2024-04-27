package com.vadmax.savemedia.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Experiment {
    public static void main(String[] args) {
        TestCmd testCmd = new TestCmd();
//        testCmd.executeCommand((new ArrayList<>(Arrays.asList("--print", "filename", "https://www.youtube.com/watch?v=1rzwbjRxfkQ"))));
        testCmd.executeCommand((new ArrayList<>(List.of())));

    }
}
