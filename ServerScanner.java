package com.example.serverscan;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfoPinger;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

public class ServerScanner {
    public static class ScanResult {
        public String ip;
        public int port;
        public long ping;
        public String motd;
        public int players;
        public boolean online;
        // 更多字段
    }

    // 异步扫描入口
    public static void startScan(String ipRange, String portRange, String apiUrl, ScanGui gui) {
        List<String> ips = parseIpRange(ipRange);
        List<Integer> ports = parsePortRange(portRange);
        List<ScanResult> results = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 如果API不为空，则远程拉取服务器列表
        if (!apiUrl.isEmpty()) {
            ips.addAll(fetchRemoteServerIps(apiUrl));
        }

        // 扫描任务
        for (String ip : ips) {
            for (int port : ports) {
                executor.submit(() -> {
                    ScanResult res = pingServer(ip, port);
                    results.add(res);
                    MinecraftClient.getInstance().execute(() -> gui.showResults(results));
                });
            }
        }
        executor.shutdown();
    }

    public static ScanResult pingServer(String ip, int port) {
        ScanResult result = new ScanResult();
        result.ip = ip;
        result.port = port;
        ServerInfo info = new ServerInfo(ip, ip + ":" + port, false);
        ServerInfoPinger pinger = new ServerInfoPinger();
        try {
            pinger.tryPing(info, () -> {});
            Thread.sleep(1500); // 等待Ping
            result.online = info.ping > 0;
            result.ping = info.ping;
            result.motd = info.label.getString();
            result.players = info.playerCountLabel.getString().isEmpty() ? 0 : Integer.parseInt(info.playerCountLabel.getString().split("/")[0]);
        } catch (Exception e) {
            result.online = false;
        }
        return result;
    }

    public static List<String> parseIpRange(String range) {
        // 解析 192.168.1.1-192.168.1.100 或 mc.example.com
        List<String> res = new ArrayList<>();
        if (range.contains("-")) {
            String[] parts = range.split("-");
            try {
                InetAddress start = InetAddress.getByName(parts[0]);
                InetAddress end = InetAddress.getByName(parts[1]);
                byte[] startBytes = start.getAddress();
                byte[] endBytes = end.getAddress();
                for (int i = startBytes[3]; i <= endBytes[3]; i++) {
                    res.add(parts[0].substring(0, parts[0].lastIndexOf('.') + 1) + i);
                }
            } catch (Exception e) {}
        } else {
            res.add(range);
        }
        return res;
    }

    public static List<Integer> parsePortRange(String range) {
        List<Integer> res = new ArrayList<>();
        if (range.contains("-")) {
            String[] parts = range.split("-");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            for (int i = start; i <= end; i++) res.add(i);
        } else {
            res.add(Integer.parseInt(range));
        }
        return res;
    }

    public static List<String> fetchRemoteServerIps(String apiUrl) {
        List<String> list = new ArrayList<>();
        try {
            URL url = new URL(apiUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = reader.lines().reduce("", (a, b) -> a + b);
            reader.close();
            // 假设为 ["ip:port", ...] 格式
            if (json.startsWith("[")) {
                json = json.substring(1, json.length()-1);
                for (String s : json.split(",")) {
                    list.add(s.replace("\""," ").trim().split(":")[0]);
                }
            }
        } catch (Exception e) {}
        return list;
    }

    public static void importServers() {
        // 导入 config/servers.json
        // TODO: 读取文件并加入扫描列表
    }

    public static void exportResults() {
        // 导出到 config/scan_results.json
        // TODO: 写入文件
    }
}