package javaProblems;

import java.util.Arrays;

class Main {

    public static void main(String[] args) {

        int[] arr = {};

        Arrays.sort(arr);
        int i = 0;

        while (i < arr.length) {
            if (arr[i + 1] != arr[i]) {
                System.out.println(i + 1);
            }
        }

    }

}