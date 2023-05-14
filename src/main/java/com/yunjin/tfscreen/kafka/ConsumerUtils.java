package com.yunjin.tfscreen.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

/**
 * @Author: Yangql
 * @Date: 2023/4/26 10:31
 * @Description: 消费工具类
 */
@Slf4j

@Component
public class ConsumerUtils {
    private static final String[] topics = {"zhwl-dbhtcq-shuju", "zhwl-dgpcgq-shuju", "zhwl-dqwz-shuju", "zhwl-krwcgq-shuju", "zhwl-qys-shuju"};


    public static ConsumerRecords<Object, Object> consume() {
        MqsConsumer consumer = new MqsConsumer();
        consumer.consume(Arrays.asList(topics));
        try {
            ConsumerRecords<Object, Object> records = consumer.poll(1000);
            log.info("the numbers of topic: {}", records.count());
            return records;
        } catch (Exception e) {
            log.error("消费消息异常", e);
            return null;
        } finally {
            consumer.close();
        }
    }

}