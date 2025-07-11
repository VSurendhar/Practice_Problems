package javaProblems;

import java.util.Arrays;
import java.util.PriorityQueue;

class Main {

    public static void main(String[] args) {

        int[] nums = {0,3};
        int[][] queries = {{0,1},{0,0},{0,1},{0,1},{0,0}};
        Main main = new Main();
        System.out.println(main.maxRemoval(nums, queries));

    }

    public int maxRemoval(int[] nums, int[][] queries) {

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        pq.addAll(Arrays.asList(queries));

        int i = 0;
        int polls = 0;

        while (i < nums.length) {
            if (pq.isEmpty()) {
                break;
            }
            if (nums[i] == 0) {
                i++;
                continue;
            }
            int[] query = pq.poll();
            if (query[0] > i) {
                break;
            }
            boolean madeChange = false;
            for (int j = query[0]; j <= query[1]; j++) {
                if (nums[j] != 0) {
                    madeChange = true;
                    nums[j]--;
                }
            }
            if (madeChange) {
                polls++;
            }
            if (nums[i] == 0) {
                i++;
            }
        }

        if (i != nums.length) {
            return -1;
        }

        return nums.length - polls;

    }


}