package com.mcxgroup.business.mq;

import com.alibaba.fastjson.JSON;
import com.mcxgroup.business.req.ConfirmOrderDoReq;
import com.mcxgroup.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;
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

    @Resource
    private ConfirmOrderService confirmOrderService;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        ConfirmOrderDoReq req = JSON.parseObject(new String(body), ConfirmOrderDoReq.class);
        MDC.put("LOG_ID",req.getLogId());
        log.info("ROCKETMQ收到消息：{}",new String(body));
        confirmOrderService.doConfirm(req);
    }
}
