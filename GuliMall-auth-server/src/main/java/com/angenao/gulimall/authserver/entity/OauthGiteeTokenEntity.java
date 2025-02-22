package com.angenao.gulimall.authserver.entity;
import lombok.Data;
import lombok.ToString;

/**
 * Auto-generated: 2025-02-12 8:22:42
 *
 * @author www.pcjson.com
 * @website http://www.pcjson.com/json2java/
 */
@Data
@ToString
public class OauthGiteeTokenEntity {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
    private String scope;
    private long createdAt;


}