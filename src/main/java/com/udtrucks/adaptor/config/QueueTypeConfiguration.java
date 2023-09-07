package com.udtrucks.adaptor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class QueueTypeConfiguration {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("QueueTypeConfiguration");
    private  Properties properties;
    public QueueTypeConfiguration() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        properties = new Properties();
        InputStream resourceStream = loader.getResourceAsStream("queue.properties");
        try{
            this.properties.load(resourceStream);
        } catch (IOException e) {
            LOGGER.error("Exception in Inbound adaptor because:\n{}", e.getMessage());
        }
        finally {
            assert resourceStream != null;
            resourceStream.close();
        }
    }

    public Object getQueueTypeConfig(String messageType){
        Properties mapProp = new Properties();
        properties.entrySet().stream().filter(k-> k.getKey().toString().contains(messageType)).collect(Collectors.toList()).stream().forEach(k->mapProp.setProperty(k.getKey().toString(), k.getValue().toString()));
        return  mapProp;
    }

    public class QueueProperties{
        public Map<Object,Object> queueProperties;

        public Map<Object, Object> getQueueProperties() {
            return queueProperties;
        }

        public void setQueueProperties(Map<Object, Object> queueProperties) {
            this.queueProperties = queueProperties;
        }
    }
}

