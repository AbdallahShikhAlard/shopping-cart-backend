package com.shoppingCartBackend.shoppingCartBackend.controller;

import com.shoppingCartBackend.shoppingCartBackend.loadbalancer.HttpLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {

    @Autowired
    private HttpLoadBalancer loadBalancer;


    @PostMapping("/simulate/{count}")
    public ResponseEntity<Map<String, Object>> simulateLoad(@PathVariable int count) {
        for (int i = 0; i < count; i++) {
            loadBalancer.forwardRequest("/api/public/health");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully distributed " + count + " requests via Load Balancer.");
        return ResponseEntity.ok(response);
    }
}