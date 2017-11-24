package com.hubery.testiostat;

public class TraceData {

    private String dname;
    private long ctime;
    private double tps;
    private double readkbps;
    private double writekbps;
    private long readkbsize;
    private long writekbsize;

    public TraceData() {
    }

    public TraceData(String dname, long ctime, double tps, double readkbps, double writekbps, long readkbsize, long writekbsize) {
        this.dname = dname;
        this.ctime = ctime;
        this.tps = tps;
        this.readkbps = readkbps;
        this.writekbps = writekbps;
        this.readkbsize = readkbsize;
        this.writekbsize = writekbsize;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public double getReadkbps() {
        return readkbps;
    }

    public void setReadkbps(double readkbps) {
        this.readkbps = readkbps;
    }

    public double getWritekbps() {
        return writekbps;
    }

    public void setWritekbps(double writekbps) {
        this.writekbps = writekbps;
    }

    public long getReadkbsize() {
        return readkbsize;
    }

    public void setReadkbsize(long readkbsize) {
        this.readkbsize = readkbsize;
    }

    public long getWritekbsize() {
        return writekbsize;
    }

    public void setWritekbsize(long writekbsize) {
        this.writekbsize = writekbsize;
    }

    @Override
    public String toString() {
        return "TraceData{" +
                "dname='" + dname + '\'' +
                ", ctime=" + ctime +
                ", tps=" + tps +
                ", readkbps=" + readkbps +
                ", writekbps=" + writekbps +
                ", readkbsize=" + readkbsize +
                ", writekbsize=" + writekbsize +
                '}';
    }


}
