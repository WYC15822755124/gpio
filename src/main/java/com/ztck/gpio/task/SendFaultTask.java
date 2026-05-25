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

@Component
@EnableScheduling
@EnableAsync
public class SendFaultTask {

    @Autowired
    private GpioService gpioService;

    private Integer runFlag = 0;

    @Autowired
    RedisUtil redisUtil;

    @Value("${sysconfig.serialPortId}")
    private List<String> serialPortId;

    @Value("${sysconfig.relayId}")
    private List<String> relayId;

    /**
     * 串口和继电器5秒检测一次
     */
    @Scheduled(fixedDelay = 5000)
    public void conditionMonitoring() {
        int serialPortFault = 0;
        int relayFault = 0;
        // 串口故障
        if (serialPortId != null && !serialPortId.isEmpty()) {
            for (String channelId : serialPortId) {
                String alarm = redisUtil.getMap("channelAlarm", channelId) + "";
                if (!alarm.equals("null")) {
                    serialPortFault = 1;
                    break;
                }
            }
            gpioService.setGpio(3, serialPortFault);
        }

        // 继电器
        if (relayId != null && !relayId.isEmpty()) {
            for (String channelId : relayId) {
                String alarm = redisUtil.getMap("channelAlarm", channelId) + "";
                if (!alarm.equals("null")) {
                    relayFault = 1;
                    break;
                }
            }
            gpioService.setGpio(5, relayFault);
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
