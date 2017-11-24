package com.hubery.testiostat;

public class StatsDevData {

    public long rdSectors;
    public long wrSectors;
    public long rdOps;
    public long wrOps;

    @Override
    public String toString() {
        return "StatsDevData{" +
                "rdSectors=" + rdSectors +
                ", wrSectors=" + wrSectors +
                ", rdOps=" + rdOps +
                ", wrOps=" + wrOps +
                '}';
    }

}
