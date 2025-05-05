package org.wjx.zencoderweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EncoderInitializer implements CommandLineRunner {

    private final EncoderService encoderService;

    @Autowired
    public EncoderInitializer(EncoderService encoderService) {
        this.encoderService = encoderService;
    }

    @Override
    public void run(String... args) {
        System.out.println("初始化数据路径配置...");
        String pdir, ddir;
        if (args.length == 0) {
            pdir = "partitioners";
            ddir = "data";
        } else if (args.length == 1) {
            pdir = args[0];
            ddir = "data";
        } else {
            pdir = args[0];
            ddir = args[1];
        }
        // 可以在这里加载默认配置、验证环境等
        encoderService.initialize(pdir, ddir);
        System.out.println("初始化完毕!");
    }
}