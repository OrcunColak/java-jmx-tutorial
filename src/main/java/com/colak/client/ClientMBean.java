package com.colak.client;

import com.colak.server.ServerJMXMBean;
import lombok.extern.slf4j.Slf4j;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.concurrent.TimeUnit;

@Slf4j
class ClientMBean {
    private static final int PORT = 10000;

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int threadCount = 2;
        String systemName = "new-system-name";

        JMXConnector jmxConnector = createJmxConnector(host);
        if (jmxConnector == null) {
            return;
        }

        // Create listener
        ClientListener listener = new ClientListener();

        // Get an MBeanServerConnection
        MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

        // Construct the ObjectName for the ServerJMX MBean
        ObjectName mbeanName = new ObjectName("com.example:type=ServerJMX");

        ServerJMXMBean mbeanProxy = JMX.newMBeanProxy(mbeanServerConnection, mbeanName, ServerJMXMBean.class, true);

        // Add notification listener on ServerJMX MBean
        mbeanServerConnection.addNotificationListener(mbeanName, listener, null, null);

        mbeanProxy.setThreadCount(threadCount);

        // Sleep for 2 seconds to have time to receive the notification
        log.info("Waiting for notification...");
        TimeUnit.SECONDS.sleep(2);


        // Get CacheSize attribute in ServerJMX MBean
        log.info("SystemName old = {}", mbeanProxy.getSystemName());
        mbeanProxy.setSystemName(systemName);
        // Get CacheSize attribute in ServerJMX MBean
        log.info("SystemName new = {}", mbeanProxy.getSystemName());

        mbeanProxy.stop();

        // Close MBeanServer connection
        log.info("Close the connection to the server");
        jmxConnector.close();
    }

    private static JMXConnector createJmxConnector(String host) {
        JMXConnector jmxConnector = null;
        try {
            log.info("Create an RMI connector client and connect it to the RMI connector server");
            String url = STR."service:jmx:rmi:///jndi/rmi://\{host}:\{PORT}/jmxrmi";
            JMXServiceURL serviceUrl = new JMXServiceURL(url);
            jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
            log.info(url);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return jmxConnector;
    }
}
