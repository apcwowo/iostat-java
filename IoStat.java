package com.hubery.testiostat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IoStat {


    public static TraceData[] getTraceStat(int interval, int count) throws InterruptedException {

        GlobalsInfo G = initGlobalsInfo();

        StatsCpuPair stats = new StatsCpuPair();

        TraceData[] traceData = new TraceData[count];

        for (int i = 0; i < count + 1; i++) {

            stats.curr = getCpuStatistics(G);

            stats.itv = getInterval(
                    stats.prev.vector[StatsCpuParams.SMP_UPTIME.getCode()],
                    stats.curr.vector[StatsCpuParams.SMP_UPTIME.getCode()]
            );

            long rsize = 0, wsize = 0;
            double rps = 0.0, wps = 0.0, tps = 0.0;

            ArrayList<TraceData> tracelist = doDiskStatistics(stats.itv, G);

            if(i == 0){
                stats.prev = stats.curr;
                Thread.sleep(interval * 1000);
                continue;
            }

            for (TraceData sdata : tracelist) {
                //System.out.println("---> " + sdata);
                tps += sdata.getTps();
                rps += sdata.getReadkbps();
                wps += sdata.getWritekbps();
                rsize += sdata.getReadkbsize();
                wsize += sdata.getWritekbsize();
            }

            traceData[i-1] = new TraceData("total", System.currentTimeMillis(), tps, rps, wps, rsize, wsize);

            stats.prev = stats.curr;

            Thread.sleep(interval * 1000);
        }

       return traceData;

    }

    private static GlobalsInfo initGlobalsInfo(){

        GlobalsInfo G = new GlobalsInfo();
        G.clk_tck = getClockTicks();
        G.total_cpus = getCpuCount();
        if(G.total_cpus == 0){
            G.total_cpus = 1;
        }
        G.show_all = 1;
        G.unit = new GlobalsInfo.Unit("KB",2);
        G.tmtime = System.currentTimeMillis();
        G.devNameList = null;
        G.statsDevMap = new HashMap<String, StatsDev>();
        return G;

    }

    private static ArrayList<TraceData> doDiskStatistics(long itv, GlobalsInfo g){

        BufferedReader reader = null;
        ArrayList<TraceData> list = new ArrayList<TraceData>();

        long rd_sec_or_dummy , wr_sec_or_dummy;

        try {

            reader = new BufferedReader(new FileReader("/proc/diskstats"));
            String line;
            while ((line = reader.readLine()) != null){

                String[] sinfos = line.trim().split("\\s+");

                String deviceName = sinfos[2];

                if(deviceName.startsWith("sda") && deviceName.length() > 3 && Character.isDigit(deviceName.charAt(3))){
                    continue;
                }

                if(!g.statsDevMap.containsKey(deviceName)){
                    g.statsDevMap.put(deviceName, new StatsDev());
                }

                StatsDev statsDev = g.statsDevMap.get(deviceName);

                statsDev.currData.rdOps = Long.valueOf(sinfos[3]);
                rd_sec_or_dummy = Long.valueOf(sinfos[4]);
                statsDev.currData.rdSectors = Long.valueOf(sinfos[5]);
                wr_sec_or_dummy = Long.valueOf(sinfos[6]);
                statsDev.currData.wrOps = Long.valueOf(sinfos[7]);
                statsDev.currData.wrSectors = Long.valueOf(sinfos[9]);

                if(sinfos.length != 14){
                    statsDev.currData.rdSectors = rd_sec_or_dummy;
                    statsDev.currData.wrSectors = wr_sec_or_dummy;
                    statsDev.currData.wrOps = statsDev.currData.rdSectors;
                }

                if(g.devNameList != null
                        && g.show_all != 0
                        && statsDev.currData.rdOps == 0
                        && statsDev.currData.wrOps == 0){
                    System.out.println("continue showall .... " + deviceName);
                    continue;
                }

                //System.out.println(deviceName + ":" + statsDev.currData.toString());

                TraceData traceData = new TraceData();
                traceData.setDname(deviceName);
                traceData.setCtime(System.currentTimeMillis());
                traceData.setTps(
                        ((double) (statsDev.currData.rdOps + statsDev.currData.wrOps) -
                        (statsDev.prevData.rdOps + statsDev.prevData.wrOps)) / itv * 100);
                traceData.setReadkbps((double)(statsDev.currData.rdSectors - statsDev.prevData.rdSectors) / itv * 100 / g.unit.div);
                traceData.setWritekbps((double)(statsDev.currData.wrSectors - statsDev.prevData.wrSectors) / itv * 100 / g.unit.div);
                traceData.setReadkbsize((statsDev.currData.rdSectors - statsDev.prevData.rdSectors) / g.unit.div);
                traceData.setWritekbsize((statsDev.currData.wrSectors - statsDev.prevData.wrSectors) / g.unit.div);

                if(deviceName.equals("sda")){
                    System.out.println("last : " + statsDev.prevData.toString());
                    System.out.println("curr : " + statsDev.currData.toString());
                }


                statsDev.prevData.rdOps = statsDev.currData.rdOps;
                statsDev.prevData.wrOps = statsDev.currData.wrOps;
                statsDev.prevData.rdSectors = statsDev.currData.rdSectors;
                statsDev.prevData.wrSectors = statsDev.currData.wrSectors;

                g.statsDevMap.put(deviceName, statsDev);

                list.add(traceData);

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private static StatsCpuPair.StatsCpu getCpuStatistics(GlobalsInfo globalsInfo){

        StatsCpuPair.StatsCpu sc = new StatsCpuPair.StatsCpu();
        BufferedReader reader = null;
        char[] buf = new char[1024];
        try {
            reader = new BufferedReader(new FileReader("/proc/stat"));
            String line = "";
            while ((line = reader.readLine()) != null){
                if(!line.startsWith("cpu") || line.charAt(3) != ' '){
                    continue;
                }
                String[] cpuinfos = line.split("\\s+");
                for (int i = StatsCpuParams.STATS_CPU_USER.getCode() + 1; i < StatsCpuParams.STATS_CPU_GUEST.getCode(); i++){
                    String infp = cpuinfos[i].trim();
                    if(!infp.contains(" ") && !infp.contains("cpu")){
                        sc.vector[i] = Long.valueOf(infp);
                        if(i != StatsCpuParams.STATS_CPU_GUEST.getCode()){
                            sc.vector[StatsCpuParams.GLOBAL_UPTIME.getCode()] += sc.vector[i];
                        }
                    }
                }
                break; //只需要获取第一行，作为总cpu统计信息
            }

            if(isSmp(globalsInfo)){
                sc.vector[StatsCpuParams.SMP_UPTIME.getCode()] = getSmpUptime(globalsInfo);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sc;
    }

    private static long getInterval(long told, long tnew){
        long itv = tnew - told;
        return (itv == 0) ? 1 : itv;
    }

    private static long getSmpUptime(GlobalsInfo globalsInfo){

        BufferedReader reader = null;
        long sec = 0,dec = 0;
        try {
            reader = new BufferedReader(new FileReader("/proc/uptime"));
            String line = reader.readLine();
            String[] split = line.split(" ");
            String[] s = split[0].split("[.]");
            sec = Long.valueOf(s[0]);
            dec = Long.valueOf(s[1]);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sec * globalsInfo.clk_tck + dec * globalsInfo.clk_tck / 100;
    }

    private static int getCpuCount(){
        BufferedReader reader = null;
        int proc_nr = -1;
        try {
            reader = new BufferedReader(new FileReader("/proc/stat"));
            String line;
            while ((line = reader.readLine()) != null){
                if(!line.startsWith("cpu")){
                    if(proc_nr >= 0){
                        break;
                    }
                    continue;
                }
                if(line.indexOf(3) != ' '){
                    int num_proc = Character.getNumericValue(line.charAt(3));
                    if(num_proc > proc_nr){
                        proc_nr = num_proc;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return proc_nr + 1;
    }

    private static boolean isSmp(GlobalsInfo globalsInfo){
        return globalsInfo.total_cpus > 1;
    }

    private static long getClockTicks(){
        return 100;
    }


}
