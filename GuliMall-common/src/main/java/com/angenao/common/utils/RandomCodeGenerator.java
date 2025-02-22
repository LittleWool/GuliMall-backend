package com.angenao.common.utils;

/**
 * @ClassName: RandomGenerator
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/10 17:04
 * @Version: 1.0
 **/

import java.util.Random;

public  class RandomCodeGenerator {
    public static void main(String[] args) {
        // 调用方法生成四位随机数字验证码
        String randomCode = generateRandomCode();
        System.out.println("生成的四位随机数字验证码是: " + randomCode);
    }

    public static String generateRandomCode() {
        Random random = new Random();
        // 生成一个四位随机数字
        int code = random.nextInt(9000) + 1000; // 保证数字在1000到9999之间
        return String.valueOf(code);
    }
}