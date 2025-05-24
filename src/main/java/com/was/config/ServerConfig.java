package com.was.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class ServerConfig{
    public int port;
    public int threads;
    public Map<String, HostConfig> virtualHosts;
    private static ServerConfig INSTANCE;

    public ServerConfig(int port,
                        Path documentRoot,
                        int threads,
                        Map<String, HostConfig> virtualHosts) {

        this.port         = port;
        this.threads      = threads;
        // 외부 수정 방지용 불변 래퍼
        this.virtualHosts = virtualHosts == null
                ? Map.of()
                : Collections.unmodifiableMap(virtualHosts);
    }

    // ServerConfig 싱글톤관리
    public static ServerConfig getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ServerConfig has not been initialized. " +
                    "Call ServerConfig.init(path) first.");
        }
        return INSTANCE;
    }

    // ServerConfig instance main에서 한번만 로딩
    public static void load(Path jsonFile) throws IOException {
        if(INSTANCE != null){
            throw new IllegalStateException("ServerConfig is already initialized.");
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Path.class, new PathAdapter())
                .create();

        try (BufferedReader reader = Files.newBufferedReader(jsonFile)) {
            INSTANCE = gson.fromJson(reader, ServerConfig.class);
        }
    }

    public int getPort() {
        return port;
    }

    public int getThreads() {
        return threads;
    }

    public Map<String, HostConfig> getVirtualHosts() {
        return virtualHosts;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "port=" + port +
                ", threads=" + threads +
                ", virtualHosts=" + virtualHosts +
                '}';
    }
}