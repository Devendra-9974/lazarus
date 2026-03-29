package com.lazarus.util;

import java.util.*;

public class DataUtils {

    public static int decodeHex(String hex) {
        return Integer.parseInt(hex.replace("0x",""), 16);
    }

    public static String decrypt(String s, int age) {

    int shift = age % 26;
    StringBuilder res = new StringBuilder();

    for (char c : s.toCharArray()) {

        if (Character.isLetter(c)) {
            char base = Character.isUpperCase(c) ? 'A' : 'a';
            int val = (c - base - shift + 26) % 26;
            res.append((char)(base + val));
        } else {
            res.append(c);
        }
    }

    return res.toString();
}

    public static void fillMissing(List<Integer> list) {

        boolean allNull = true;
        for (Integer v : list) {
            if (v != null) {
                allNull = false;
                break;
            }
        }

        if (allNull) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, 95); // default fallback
            }
            return;
        }

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) == null) {

                int prev = i - 1, next = i + 1;

                while (prev >= 0 && list.get(prev) == null) prev--;
                while (next < list.size() && list.get(next) == null) next++;

                if (prev >= 0 && next < list.size()) {
                    list.set(i, (list.get(prev) + list.get(next)) / 2);
                } else if (prev >= 0) {
                    list.set(i, list.get(prev));
                } else if (next < list.size()) {
                    list.set(i, list.get(next));
                }
            }
        }
    }
}