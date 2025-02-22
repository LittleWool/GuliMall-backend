package com.angenao.gulimall.authserver.to;

import lombok.Data;

/**
 * @ClassName: SocailUserTo
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/12 13:17
 * @Version: 1.0
 **/


@Data
public class SocialUserTo {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private String scope;
    private Long createdAt;
    private String id;
    private String login; //用户用户名
    private String name; //用户昵称

}