package com.wings.zilizili.utils;

/**
 * Created by wing on 2015/11/19.
 */
public class Solution {

    public int addDigits(int num) {
        int count = 0;
        if (num < 10) return num;
        while (num > 10) {
            num = num / 10;
            count += num / 10;
            if (num < 10 && count > 10) {
                num = count;
                count = 0;
            }
        }
        return count;
    }
}