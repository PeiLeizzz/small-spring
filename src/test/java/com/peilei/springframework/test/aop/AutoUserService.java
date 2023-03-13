package com.peilei.springframework.test.aop;

import com.peilei.springframework.stereotype.Component;

import java.util.Random;

@Component("userService")
public class AutoUserService implements IUserService {
    private String token;

    @Override
    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "peilei, 100001, beijing";
    }

    @Override
    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "register: " + userName + " success!";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserService#token = { " + token + " }";
    }
}
