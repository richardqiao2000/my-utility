package io.richardqiao.practice.algorithms;

import java.util.*;

/**
 倒水问题，已知两个被子的容积，通过倒水的方法找出目标容积的水
 例如：
 bottle1 size: 10, bottle2 size: 21, target: 22
 fill bottle2(0,21)
 pour water from bottle2 to bottle1(10,11)
 dump bottle1(0,11)
 pour water from bottle2 to bottle1(10,1)
 dump bottle1(0,1)
 pour water from bottle2 to bottle1(1,0)
 fill bottle2(1,21)
*/
class PourWater {

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
//        set.add("0,0");
        LinkedList<String> res = steps(
                0, 17,
                0, 13, 30,
                new LinkedList<>(), "Start", set, new HashMap<>());
        if(res == null) {
            System.out.println("No Answer!");
            return;
        }
        for(String step: res) {
            System.out.println(step);
        }
    }

    /*
     13, 17
     fill 13 -> 13 to 17 -> fill 13 -> 13 to 17 -> 9 left in 13
     actions: 1. fill bottle
              2. move bottleX to bottleY
              3. dump bollte
     check condition: (water in bottleX or bottleY) == 9

    */
    static String action1 = "fill bottle1";
    static String action2 = "fill bottle2";
    static String action3 = "dump bottle1";
    static String action4 = "dump bottle2";
    static String action5 = "pour water from bottle1 to bottle2";
    static String action6 = "pour water from bottle2 to bottle1";
    private static LinkedList<String> steps(
            int bottle1, int size1,
            int bottle2, int size2, int target,
            LinkedList<String> list, String action, Set<String> visited, Map<String, LinkedList<String>> map){
        if(bottle1 == target || bottle2 == target || bottle1 + bottle2 == target){
            return new LinkedList<>(list);
        }
        // solve action and change waters
        if(action.equals(action1)){
            if(bottle1 == size1) return null;
            bottle1 = size1;
        }else if(action.equals(action2)){
            if(bottle2 == size2) return null;
            bottle2 = size2;
        }else if(action.equals(action3)){
            if(bottle1 == 0) return null;
            bottle1 = 0;
        }else if(action.equals(action4)){
            if(bottle2 == 0) return null;
            bottle2 = 0;
        }else if(action.equals(action5)){
            if(bottle1 == 0 || bottle2 == size2) return null;
            int tmpBottle1 = bottle1 - (size2 - bottle2);
            if(tmpBottle1 < 0) tmpBottle1 = 0;
            int tmpBottle2 = bottle2 + bottle1;
            if(tmpBottle2 > size2) tmpBottle2 = size2;
            bottle1 = tmpBottle1;
            bottle2 = tmpBottle2;
        }else if(action.equals(action6)){
            if(bottle2 == 0 || bottle1 == size1) return null;
            int tmpBottle2 = bottle2 - (size1 - bottle1);
            if(tmpBottle2 < 0) tmpBottle2 = 0;
            int tmpBottle1 = bottle1 + bottle2;
            if(tmpBottle1 > size1) tmpBottle1 = size1;
            bottle1 = tmpBottle1;
            bottle2 = tmpBottle2;
        }
        String key = bottle1 + "," + bottle2;
        if(map.containsKey(key)){
            return map.get(key);
        }
        if(visited.contains(key)) return null;
        visited.add(key);
        list.add(action + "(" + bottle1 + "," + bottle2 + ")->" + target);
        // dfs to different actions
        LinkedList<String> res = steps(bottle1, size1, bottle2, size2, target, list, action1, visited, map);
        LinkedList<String> res2 = steps(bottle1, size1, bottle2, size2, target, list, action2, visited, map);
        if(res == null || res2 != null && res2.size() < res.size()){
            res = res2;
        }
        res2 = steps(bottle1, size1, bottle2, size2, target, list, action3, visited, map);
        if(res == null || res2 != null && res2.size() < res.size()){
            res = res2;
        }
        res2 = steps(bottle1, size1, bottle2, size2, target, list, action4, visited, map);
        if(res == null || res2 != null && res2.size() < res.size()){
            res = res2;
        }
        res2 = steps(bottle1, size1, bottle2, size2, target, list, action5, visited, map);
        if(res == null || res2 != null && res2.size() < res.size()){
            res = res2;
        }
        res2 = steps(bottle1, size1, bottle2, size2, target, list, action6, visited, map);
        if(res == null || res2 != null && res2.size() < res.size()){
            res = res2;
        }
        list.remove(list.size() - 1);
        visited.remove(key);
        if(res != null){
            map.put(key, res);
        }
        return res;
    }

}
