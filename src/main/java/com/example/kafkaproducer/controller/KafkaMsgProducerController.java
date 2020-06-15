package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.configuration.ProducerAdminClient;
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

    @Autowired
    private ProducerAdminClient producerAdminClient;

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

    @RequestMapping("/sendFixMsg/{msg}/{type}")
    public void sendFixMsg(@PathVariable String msg, @PathVariable String type) {
        FixMsgModel fixMsgModel = new FixMsgModel();
        fixMsgModel.setFixMsgName(msg);
        fixMsgModel.setFixMsgType(Long.toString(new Date().getTime()));
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

    @RequestMapping("/deleteTopic/{topicName}")
    public void deleteTopic(@PathVariable String topicName) throws ExecutionException, InterruptedException {
        AdminClient client = producerAdminClient.getAdminClient();
        ListTopicsResult listTopicsResult = client.listTopics();
        Set<String> listOfTopics = listTopicsResult.names().get();
        System.out.println(listOfTopics.toString());
        if(listOfTopics.contains(topicName)){
            Set<String> set =  new HashSet<>();
            set.add(topicName);
            client.deleteTopics(set);
        }
    }
}