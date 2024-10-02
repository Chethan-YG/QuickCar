package com.quickcar.rent.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TempStorageService {

    private final Map<String, String> tempStore = new ConcurrentHashMap<>();

    public void storeData(String key, String value) {
        tempStore.put(key, value);
    }

    public String getData(String key) {
        return tempStore.get(key);
    }

    public void removeData(String key) {
        tempStore.remove(key);
    }
}
