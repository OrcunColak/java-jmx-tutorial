package com.colak.server;

import lombok.extern.slf4j.Slf4j;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

// -Dcom.sun.management.jmxremote.port=10000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

@Slf4j
class ServerMain {

    private static final int DEFAULT_NO_THREADS = 10;
    private static final String SYSTEM_NAME = "System";

    public static void main(String[] args) throws Exception {

        // Get the MBean server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // register the MBean
        ServerJMX mBean = new ServerJMX(DEFAULT_NO_THREADS, SYSTEM_NAME);
        ObjectName name = new ObjectName("com.example:type=ServerJMX");
        mbs.registerMBean(mBean, name);

        do {
            TimeUnit.SECONDS.sleep(3);
            log.info("Thread Count = {} System Name = {}", mBean.getThreadCount(), mBean.getSystemName());
        } while (mBean.isRunning());

        log.info("Server JMX stopped");
    }
}
