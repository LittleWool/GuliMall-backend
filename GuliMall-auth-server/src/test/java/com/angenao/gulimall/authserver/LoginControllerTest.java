package com.angenao.gulimall.authserver;

import com.angenao.gulimall.authserver.controller.LoginController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: LoginControllerTest
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/11 9:02
 * @Version: 1.0
 **/

@SpringBootTest
@RunWith(SpringRunner.class)
public class LoginControllerTest {

    @Autowired
    LoginController loginController;

    @Test
    public void loginTest() {
        loginController.sendcode("15515258978");
    }
}
