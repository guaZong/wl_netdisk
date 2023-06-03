package com.sk.netdisk.service.impl;


import java.util.ArrayList;
import java.util.HashMap;

public class main {

    public static void main(String[] args) {
        String str="[{{([])}}{}[]()]";
        System.out.println(judge(str));
    }


    public static  boolean judge(String str){
        HashMap<Character,Character> map=new HashMap<>();
        map.put('{','}');
        map.put('(',')');
        map.put('[',']');
        ArrayList<Character> arrayList=new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (map.containsKey(c)) {
                arrayList.add(c);
            } else if(arrayList.size()!=0){
                Character rightValue = map.get(arrayList.get(arrayList.size() - 1));
                if (rightValue == c) {
                    arrayList.remove(arrayList.size() - 1);
                } else {
                    return false;
                }
            }else{
                return false;
            }
        }
        return arrayList.size()==0;
    }
}

