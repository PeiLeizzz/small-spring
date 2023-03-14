package com.peilei.springframework.test.converter;

import com.peilei.springframework.context.annotation.Autowired;
import com.peilei.springframework.context.annotation.Value;
import com.peilei.springframework.stereotype.Component;
import com.peilei.springframework.test.aop.IUserService;

import java.util.Random;

@Component("userService")
public class ConvertUserService implements IUserService {
    @Value("${card}")
    private Integer cardId;

    @Autowired
    private ConvertUserDao userDao;

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userDao.queryUserName("10001") + ", " + cardId;
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

    public Integer getCardId() {
        return cardId;
    }
}
