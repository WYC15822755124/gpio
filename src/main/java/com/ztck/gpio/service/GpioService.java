package com.ztck.gpio.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class GpioService {

    // testgpio 可执行文件路径（放在项目根目录）
    private static final String TEST_GPIO_PATH = "./testgpio";

    /**
     * 设置 GPIO 端口电平
     *
     * @param pin   1~8
     * @param level 0=低电平 1=高电平
     * @return 成功/失败
     */
    public void setGpio(int pin, int level) {
        // 校验参数
        if (pin < 1 || pin > 8 || (level != 0 && level != 1)) {
            return;
        }

        // 拼接命令：./testgpio -p 端口 -s 值
        String cmd = TEST_GPIO_PATH + " -p " + pin + " -s " + level;

        try {
            Process process = Runtime.getRuntime().exec(cmd);
//            int exitCode = process.waitFor();

//            // 打印命令输出（可选）
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println("[GPIO] " + line);
//            }
//
//            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
//            return false;
        }
    }
}
