package com.xmo.demo.java.alg.lc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class P126WordLadder2 {

    public static void main(String[] args) {
        Solution s = new Solution();

        String beginWord = "hit";
        String endWord = "cog";
        String[] wordArray = { "hot", "dot", "dog", "lot", "log", "cog" };
        List<List<String>> ladders = s.findLadders(beginWord, endWord, Arrays.asList(wordArray));

    }
}

class Solution {
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        Map<String, Integer> nodeValueMap = buildNodeValueMap(wordList);
        nodeValueMap.put(beginWord, 0);
        Map<String, List<String>> preNodeMap = buildPreNodeMap(wordList);
        Queue<String> queue = new LinkedList<String>();
        queue.add(beginWord);
        while (!queue.isEmpty()) {
            String currentNode = queue.poll();
            int currentValue = nodeValueMap.get(currentNode);
            for (String nextNode : wordList) {
                if (editDistance(currentNode, nextNode) == 1) {
                    int nodeValue = nodeValueMap.get(nextNode);
                    if (nodeValue == Integer.MAX_VALUE) {
                        queue.add(nextNode);
                    }
                    if (currentValue + 1 < nodeValue) {
                        nodeValueMap.put(nextNode, currentValue + 1);
                        List<String> preList = preNodeMap.get(nextNode);
                        preList.clear();
                        preList.add(currentNode);
                    } else if (currentValue + 1 == nodeValue) {
                        List<String> preList = preNodeMap.get(nextNode);
                        preList.add(currentNode);
                    }
                }
            }
        }
        List<List<String>> paths = new ArrayList<List<String>>();
        if (preNodeMap.get(endWord) == null) {
            return paths;
        } else {           
            List<String> prefix = new ArrayList<String>();
            prefix.add(endWord);
            buildPath(prefix, preNodeMap, endWord, beginWord, paths);
            return paths;
        }
    }

    public void printPath(List<String> path) {
        System.out.println("Path------");
        for (String node : path) {
            System.out.print(node);
            System.out.print(" -> ");
        }
        System.out.println("\n");
    }

    public void buildPath(List<String> prefix, Map<String, List<String>> preNodeMap, String currentNode,
            String endNode, List<List<String>> paths) {
        if (prefix.get(prefix.size() - 1).equals(endNode)) {
            List<String> path = new ArrayList<String>();
            for (String node : prefix) {
                path.add(0, node);
            }
            // printPath(path);
            paths.add(path);
            return;
        }
        List<String> preNodeList = preNodeMap.get(currentNode);
        for (String preNode : preNodeList) {
            prefix.add(preNode);
            buildPath(prefix, preNodeMap, preNode, endNode, paths);
            prefix.remove(preNode);
        }
    }

    public int editDistance(String first, String second) {
        int distance = 0;
        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    public Map<String, Integer> buildNodeValueMap(List<String> wordList) {
        // int wordListSize = wordList.size()+100;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String word : wordList) {
            map.put(word, Integer.MAX_VALUE);
        }
        return map;
    }

    public Map<String, List<String>> buildPreNodeMap(List<String> wordList) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String word : wordList) {
            map.put(word, new ArrayList<String>());
        }
        return map;
    }
}