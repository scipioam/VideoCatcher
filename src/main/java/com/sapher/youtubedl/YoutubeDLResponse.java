package com.sapher.youtubedl;

import java.util.Map;

/**
 * YoutubeDL response
 */
public class YoutubeDLResponse {

    private final Map<String, String> options;
    private final String command;
    private final int exitCode;
    private final String out;
    private final String err;
    private final String directory;
    private final int elapsedTime;
    private final int id;

    public YoutubeDLResponse(String command, Map<String, String> options, String directory, int exitCode, int elapsedTime, String out, String err, int id) {
        this.command = command;
        this.options = options;
        this.directory = directory;
        this.elapsedTime = elapsedTime;
        this.exitCode = exitCode;
        this.out = out;
        this.err = err;
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOut() {
        return out;
    }

    public String getErr() {
        return err;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getDirectory() {
        return directory;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getId() {
        return id;
    }
}
