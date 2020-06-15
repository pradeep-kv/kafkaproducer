package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.model.FixMsgModel;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
public class KafkaMsgProducerController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, FixMsgModel> kafkaFixTemplate;

    @RequestMapping("/test")
    public String testMsg(){
        return "Working";
    }

    @RequestMapping("/sendMsg/{msg}")
    public void sendMsg(@PathVariable String msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("testTopic", msg);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Failed to send msg  - " + msg + " - with the following error " + ex.toString());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Msg sent Successfully - " + msg + " - with the following result - " + result.toString());
            }
        });
    }

    @RequestMapping("/sendFixMsg/{msg}")
    public void sendFixMsg(@PathVariable String msg) {
        FixMsgModel fixMsgModel = new FixMsgModel();
        fixMsgModel.setFixMsgName(msg);
        fixMsgModel.setFixMsgType(Long.toString(new Date().getTime()));
        fixMsgModel.setCurrentDate(new Date().getTime());
        ListenableFuture<SendResult<String, FixMsgModel>> future = kafkaFixTemplate.send("msgTopic", fixMsgModel);

        future.addCallback(new ListenableFutureCallback<SendResult<String, FixMsgModel>>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Failed to send msg  - " + fixMsgModel.toString() + " - with the following error " + ex.toString());
            }

            @Override
            public void onSuccess(SendResult<String, FixMsgModel> result) {
                System.out.println("Msg sent Successfully - " + fixMsgModel.toString() + " - with the following result - " + result.toString());
            }
        });
    }

    @RequestMapping("sendBulkMsg/{msgCount}")
    public void sendBulkMsg(@PathVariable int msgCount){
        if(msgCount <= 0){
            return;
        }
        for (int i = 0; i< msgCount; i++){
            this.sendFixMsg(Integer.toString(i));
        }
        return;

    }
}
