package javaProblems;

class Main {

    public static void main(String[] args) {
        System.out.println((new Main()).kthCharacter(10));
    }

    public char kthCharacter(int k) {
        return (char) (countBits(k-1) + ('a'));
    }

    private int countBits(int i) {
        int count = 0;

        while (i != 0) {
            i = i & (i - 1);
            count++;
        }

        return count;
    }

}