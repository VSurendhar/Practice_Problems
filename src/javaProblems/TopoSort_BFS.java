package javaProblems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TopoSort_BFS {

    public static void main(String[] args) {

        int[][] edges = {{5,0},{4,0},{5,2},{2,3},{3,1},{4,1}};
        int n = 6;
        List<Integer> res = topoSort(edges ,n);
        System.out.println(res);

    }

    private static List<Integer> topoSort(int[][] edges, int n) {

        int[] inDegree = new int[n];
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> res = new ArrayList<>();
        List<List<Integer>> adj = new ArrayList<>();

        for(int[] edge : edges){
            inDegree[edge[1]]++;
        }

        for(int i = 0 ; i < n ; i++){
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges){
            adj.get(edge[0]).add(edge[1]);
        }

        for(int i = 0 ; i<inDegree.length ; i++){
            if (inDegree[i] == 0){
                queue.add(i);
            }
        }

        while (!queue.isEmpty()){

            int node = queue.poll();
            res.add(node);

            for(int adjNode : adj.get(node)){
                inDegree[adjNode]--;
                if(inDegree[adjNode] == 0){
                    queue.add(adjNode);
                }
            }

        }

        return res;
    }

}
