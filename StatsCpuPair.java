package com.hubery.testiostat;

public class StatsCpuPair {

    public StatsCpu prev;
    public StatsCpu curr;
    public long itv;

    public StatsCpuPair() {
        prev = new StatsCpu();
        curr = new StatsCpu();
    }

    public static class StatsCpu{

        public long[] vector = new long[12];

        public StatsCpu() {
            init();
        }

        private void init(){
            for (int i = 0; i < vector.length; i++) {
                vector[i] = 0l;
            }
        }

    }

}
