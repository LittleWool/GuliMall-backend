package com.angenao.gulimall.authserver.vo;

import lombok.Data;

/**
 * @Description: 社交用户信息
 * @author: zhangshuaiyin
 * @createTime: 2021-04-22 19:07
 **/
@Data
public class SocialUser {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private String id;

    private String login;

    private String name;





}
