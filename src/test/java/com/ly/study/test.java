package com.ly.study;

import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName test
 * @Author liuyang
 * @Date 2020-01-11 16:01
 **/
public class test {

    public static void main(String[] args) {

        RateLimiter rateLimiter = RateLimiter.create(5);

        System.out.println(rateLimiter.getRate());

        for (int i = 0; i <10 ; i++) {
            new Thread(() -> {
                rateLimiter.acquire();
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }).start();
        }
    }



    void A(){

        B();
        C();
    }

    void B(){
    }

    void C(){
    }
}
