package com.hubery.testiostat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalsInfo {

    public int show_all;
    public int total_cpus;
    public long clk_tck;
    public long tmtime;
    public Unit unit;

    public ArrayList<String> devNameList;
    public HashMap<String, StatsDev> statsDevMap;

    public static class Unit{

        public String str;
        public int div;

        public Unit(String str, int div) {
            this.str = str;
            this.div = div;
        }

    }


}
