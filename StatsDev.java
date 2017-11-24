package com.hubery.testiostat;

public class StatsDev {

    public String dname;
    public StatsDevData prevData;
    public StatsDevData currData;

    public StatsDev() {
        prevData = new StatsDevData();
        currData = new StatsDevData();
    }

    public StatsDev(String dname, StatsDevData prevData, StatsDevData currData) {
        this.dname = dname;
        this.prevData = prevData;
        this.currData = currData;
    }


}
