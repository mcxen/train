package com.mcxgroup.business.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @ClassName ConfirmOrderConsumer
 * @Description 消息队列的消费类，用于接受消息
 * @Author McXen@2023/11/24
 **/
@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "default",topic = "CONFIRM_ORDER")
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        log.info("ROCKETMQ收到消息：{}",new String(body));
    }
}
