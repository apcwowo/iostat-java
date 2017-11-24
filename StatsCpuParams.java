package com.hubery.testiostat;

import sun.dc.pr.PRError;

public enum StatsCpuParams {

    STATS_CPU_USER(0),
    STATS_CPU_NICE(1),
    STATS_CPU_SYSTEM(2),
    STATS_CPU_IDLE(3),
    STATS_CPU_IOWAIT(4),
    STATS_CPU_IRQ(5),
    STATS_CPU_SOFTIRQ(6),
    STATS_CPU_STEAL(7),
    STATS_CPU_GUEST(8),
    GLOBAL_UPTIME(9),
    SMP_UPTIME(10),
    N_STATS_CPU(11);

    private int code = 0;

    StatsCpuParams(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
