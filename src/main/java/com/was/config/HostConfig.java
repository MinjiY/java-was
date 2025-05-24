package com.was.config;

import com.google.gson.annotations.SerializedName;
import java.nio.file.Path;
import java.util.Map;

public class HostConfig {
    @SerializedName("httpRoot")
    public Path httpRoot;
    public Map<Integer, String> errorPage;
    @Override
    public String toString() {
        return "HostConfig{httpRoot=" + httpRoot + ", errorPages=" + errorPage + '}';
    }

    public Path getHttpRoot() {
        return httpRoot;
    }
    public Map<Integer, String> getErrorPage() {
        return errorPage;
    }
}
