package com.peilei.springframework.test.anno;

import com.peilei.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AnnoUserDao {
    private static Map<String, String> hashMap = new HashMap<>();

    static {
        hashMap.put("10001", "peilei");
        hashMap.put("10002", "other");
        hashMap.put("10003", "another");
    }

    public String queryUserName(String uId) {
        return hashMap.get(uId);
    }
}
