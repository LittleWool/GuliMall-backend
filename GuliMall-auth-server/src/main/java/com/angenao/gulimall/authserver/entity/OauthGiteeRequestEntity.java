package com.angenao.gulimall.authserver.entity;

import lombok.Data;

/**
 * @ClassName: OauthGiteeRequestEntity
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/12 8:35
 * @Version: 1.0
 **/
@Data
public class OauthGiteeRequestEntity {


    String grant_type="authorization_code";
    String code;
    String client_id;
    String redirect_uri;
    String client_secret;
}
