package com.yunjin.tfscreen.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @Author: Yangql
 * @Date: 2023/4/25 14:04
 * @Description:
 */
public class MqsConsumer {

    private static final String CONFIG_CONSUMER_FILE_NAME = "mqs.sdk.consumer.properties";

    private KafkaConsumer<Object, Object> consumer;

    public MqsConsumer(String path) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        consumer = new KafkaConsumer<Object, Object>(props);
    }

    public MqsConsumer() {
        Properties props;
        try {
            props = loadFromClasspath(CONFIG_CONSUMER_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        consumer = new KafkaConsumer<>(props);
    }

    public void consume(List topics) {
        consumer.subscribe(topics);
    }

    public void seek(String topic, Integer partitionNum, Long offset) {
        TopicPartition partition = new TopicPartition(topic, partitionNum);
        consumer.assign(Collections.singletonList(partition));
        consumer.seek(partition, offset);
    }

    public ConsumerRecords<Object, Object> poll(long timeout) {
        return consumer.poll(timeout);
    }

    public void close() {
        consumer.close();
    }

    /**
     * get classloader from thread context if no classloader found in thread
     * context return the classloader which has loaded this class
     *
     * @return classloader
     */
    public static ClassLoader getCurrentClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = MqsConsumer.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * 从 classpath 加载配置信息
     *
     * @param configFileName 配置文件名称
     * @return 配置信息
     * @throws IOException
     */
    public static Properties loadFromClasspath(String configFileName) throws IOException {
        ClassLoader classLoader = getCurrentClassLoader();
        Properties config = new Properties();
        List<URL> properties = new ArrayList<URL>();
        Enumeration<URL> propertyResources = classLoader
                .getResources(configFileName);
        while (propertyResources.hasMoreElements()) {
            properties.add(propertyResources.nextElement());
        }
        for (URL url : properties) {
            InputStream is = null;
            try {
                is = url.openStream();
                config.load(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return config;
    }
}