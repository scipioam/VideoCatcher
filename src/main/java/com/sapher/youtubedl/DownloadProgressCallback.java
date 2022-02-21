package com.sapher.youtubedl;

public interface DownloadProgressCallback {
    void onProgressUpdate(double progress, long etaInSeconds);
}
