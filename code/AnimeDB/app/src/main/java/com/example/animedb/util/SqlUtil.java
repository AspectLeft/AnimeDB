package com.example.animedb.util;

/**
 * Created by Fang on 2017/5/21.
 *
 */

public class SqlUtil {
    public static String sqltext(String s) {
        return s.replace("'", "''");
    }
}
