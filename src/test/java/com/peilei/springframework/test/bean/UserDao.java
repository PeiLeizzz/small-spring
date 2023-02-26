package com.peilei.springframework.test.bean;

import java.util.HashMap;
import java.util.Map;

public class UserDao {
    private static Map<String, String> hashMap = new HashMap<>();

    public String queryUserName(String uid) {
        return hashMap.get(uid);
    }

    public void initDataMethod() {
        System.out.println("执行 UserDao.init-method");
        hashMap.put("10001", "t1");
        hashMap.put("10002", "t2");
        hashMap.put("10003", "t3");
    }

    public void destroyDataMethod() {
        System.out.println("执行 UserDao.destroy-method");
        hashMap.clear();
    }
}
