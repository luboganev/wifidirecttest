package com.example.wifidirecttest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface MessageLog {
    void logMessage(@Nullable String tag, @NonNull String message);
}
