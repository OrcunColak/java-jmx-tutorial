package com.colak.server;

public interface ServerJMXMBean {

    void setThreadCount(int noOfThreads);

    int getThreadCount();

    void setSystemName(String systemName);

    String getSystemName();

    boolean isRunning();

    void stop();
}
