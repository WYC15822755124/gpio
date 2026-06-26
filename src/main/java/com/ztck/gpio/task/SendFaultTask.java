package com.ztck.gpio.task;


import com.ztck.gpio.service.GpioService;
import com.ztck.gpio.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
@EnableAsync
public class SendFaultTask {

    @Autowired
    private GpioService gpioService;

    private Integer runFlag = 0;

    @Autowired
    RedisUtil redisUtil;

    @Value("${sysconfig.serialPortName}")
    private List<String> serialPortName;

    @Value("${sysconfig.relayName}")
    private List<String> relayName;

    /**
     * 串口和继电器5秒检测一次
     */
    @Scheduled(fixedDelay = 5000)
    public void conditionMonitoring() {
        AtomicInteger serialPortFault = new AtomicInteger();
        AtomicInteger relayFault = new AtomicInteger();

        Map<Object, Object> channelAlarm = redisUtil.getHashEntries("channelAlarm");

        if (channelAlarm != null && !channelAlarm.keySet().isEmpty()) {
            channelAlarm.forEach((key, value) -> {
                if (serialPortName.contains(value + "")) {
                    serialPortFault.getAndIncrement();
                } else if (relayName.contains(value + "")) {
                    relayFault.getAndIncrement();
                }
            });
        }

        if (serialPortFault.get() > 0) {
            gpioService.setGpio(3, 1);
        } else {
            gpioService.setGpio(3, 0);
        }

        if (relayFault.get() > 0) {
            gpioService.setGpio(5, 1);
        } else {
            gpioService.setGpio(5, 0);
        }

        // 运行检测
        if (runFlag == 0) {
            runFlag = 1;
        } else if (runFlag == 1) {
            runFlag = 0;
        }
        gpioService.setGpio(1, runFlag);
    }


}
