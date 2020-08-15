package com.ly.study;

import com.ly.study.rateLimit.strategy.RateLimitContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudyApplicationTests {

    @Autowired
    private RateLimitContext context;

    @Test
    public void rateLimit() throws InterruptedException {
        for (int i = 0; i <12 ; i++) {
            context.execute("leakyBucketStrategy");
        }
    }

}
