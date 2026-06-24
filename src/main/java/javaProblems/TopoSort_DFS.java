package javaProblems;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TopoSort_DFS {

    public static void main(String[] args) {

        int[][] edges = {{5,0},{4,0},{5,2},{2,3},{3,1},{4,1}};
        int n = 6;
        List<Integer> res = topoSort(edges ,n);
        System.out.println(res);

    }

    private static List<Integer> topoSort(int[][] edges, int n) {

        List<Integer> res = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();

        List<List<Integer>> adjList = new ArrayList<>();

        for(int i =0  ; i<n  ; i++){
            adjList.add(new ArrayList<>());
        }

        for(int[] edge : edges){
            adjList.get(edge[0]).add(edge[1]);
        }

        boolean[] visit = new boolean[n] ;

        for(int i =0 ; i<n ; i++){
            if(!visit[i]){
                dfs(stack , i , adjList ,visit);
            }
        }

        while (!stack.isEmpty()){
            res.add(stack.pop());
        }

        return res;

    }

    private static void dfs(Stack<Integer> stack, int node, List<List<Integer>> adjList, boolean[] visit) {

        visit[node] = true;

        for(int adjNode : adjList.get(node)){
            if(!visit[adjNode]) {
                dfs(stack, adjNode, adjList, visit);
            }
        }

        stack.add(node);

    }

}
