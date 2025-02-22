package com.angenao.gulimall.thirdpart;


import com.aliyun.oss.OSSClient;
import com.angenao.gulimall.thirdpart.component.SmsComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GuuliMallThirdPartApplicationTests {


    @Resource
    OSSClient ossClient;

    @Test
    public void upLoad() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("D:\\Snipaste_2024-10-30_18-53-02.png");
        String bucketName="gulimall-angenao";
        ossClient.putObject(bucketName,"超兽武装",inputStream);
        System.out.println("上传完成");
    }

    @Autowired
    SmsComponent smsComponent;

    @Test
    public  void sendSms(){
        smsComponent.sendSmsCode("15515258978","abcd",5);
    }

}
