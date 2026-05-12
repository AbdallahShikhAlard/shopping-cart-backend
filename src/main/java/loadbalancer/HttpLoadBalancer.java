package com.shoppingCartBackend.shoppingCartBackend.loadbalancer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class HttpLoadBalancer {
    private final List<String> backendServers;
    private final ReentrantLock lock = new ReentrantLock();
    private int nextServerIndex = 0;

    @Autowired
    private RestTemplate restTemplate;

    public HttpLoadBalancer() {
        backendServers = new ArrayList<>();

        backendServers.add("http://localhost:8081");
        backendServers.add("http://localhost:8082");
        System.out.println("HTTP Load Balancer initialized with " + backendServers.size() + " servers.");
    }

    public ResponseEntity<String> forwardRequest(String endpoint) {
        String targetServer;
        lock.lock();
        try {
            targetServer = backendServers.get(nextServerIndex);
            nextServerIndex = (nextServerIndex + 1) % backendServers.size();
        } finally {
            lock.unlock();
        }

        String fullUrl = targetServer + endpoint;
        System.out.println("[LoadBalancer] Forwarding to: " + fullUrl);
        return restTemplate.getForEntity(fullUrl, String.class);
    }
}