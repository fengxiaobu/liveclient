package com.dykj;

import com.dykj.live.task.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class LiveClientApplication {
    private static Logger log = LoggerFactory.getLogger(QuartzService.class);

    public static void main(String[] args) {
        SpringApplication.run(LiveClientApplication.class, args);
        log.info(">>>>>>>>>>>>>>>>>>>>>>客户端启动成功<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
