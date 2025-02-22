package com.angenao.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.angenao.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "9021000144654681";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCUDytGYUp52Ehb0v0ysEEOkE+dI6pUbLX3yXW0BCS09y/fU8eUcIHd3UHEQfxwqHxikbVQTzoLUk1QzddB8069foEn5n2VZaXo31UUamTqAo2ScR6Mos0YC6WJ1KTpItsnsNTiKtBthBzMlRx9BJMYhmIzFAn5VA2yvp+snMvYjvtKkTU215XMXywpPQ4QsoSDydzfC4U9kMXbntymlGdpa0xgpDOaZUHCYBLH8cGZTGNN/lDBWpp5Qw8/9Wfke4j5y0pyOy59s76fpYJV0BFBhZMiHfZ+kqqI1tZsZQGx+/nlNqjVR5XhGkTFvhkVHct2NlT7XGP7WRzkIo4aI2exAgMBAAECggEAOMCrxNmMM7JM4i248lIPTebRLZm7M4tMIQPvAybHbzqE9iTmDqclkIa8K99jRUfADgRuLK+Bv0835a5RQa1i1E4GceXu43JIK0BYG3BA6FY8MBL+M8g/t2TXekKxR9Adk1Q79cmVhTUr2NubROyY+cChKZUX0aN/cLGTARZFKOfoZzCwEDyb8I0eu4muGXrC38yHmsCJAFa+ZqzAkZs0CA9VL3Cua4c8dv2u4UACm9Ce2y3X49RohuHQpCW2+9EnOaMALC24Nf/zX5o88Jcg8exxmPJGuI7UqtllfsijoRA3m+KuZG27xCrJa78I6Cn9VK1W9Un+gICw9aqOIS4OAQKBgQDIkaLU5BY2eiozGG7TLqBM8ckuuym9wapRb6eFCkZPxsboGOow1o3rso+aKrIWk5fZaAWgb2YSTNXVHwKDh0zHn9UbuHoRT3ULMa7Ou4WeVN9eosL1C6GQvO9UnS+PTO1gHIHBhPRgJDeFU35KH0UsjxwoRqjQYwMDhU5sVjX2UQKBgQC8+nIzRWN2pVy76jWTzij1hNJuSu5jOrkmUO3CsMfLB4F52JFsOedV6rLJeltgwSE8XAiTR7Ctw5TdDCVgFWXqv4JrGoF4IAb8EsA6FKOu5/z2kzAV/r34ysr6151d/v0ghis0TZ+L2Z4omTQc8pN2NF8OoHP9JSD1z78MqcQjYQKBgEgXMLPcl69VEb3z1kHJzIxbi7hkRYiTB6jlvQywuL0L8YqTCfor5C8Hq136YrCCtF3MadlUsRd6zpEf6ENmak6GQI8KK7X1HVi8rtxXwps6Xl8hmXmCiLwBbPS42tNjDx8wn+Ly6vNzkRE3BXmW68IsXi6L1zmvnPGzBJEkG5lBAoGBAIY0nhGHP6GswfS52Z7EFBZ4YzDsietycrahVpijDuBCzlKH2I6xeHZtnNa0zFwkC8Rf378odHKSqQ9qZsibqatSof4J7tQueoddkaItsiium9lIoiecp7Ed45TMtvdf9Me6QMh+L8YTn9xZIMdGBi819iPfE0A9s/CdvqHxlldBAoGAfZLZrgWJyMBn5ZrqfmjGFpw/OzeFU0lRINY/Z7Jsz/GzxPBnJTll6Oi2eoex+hANYby06+9jppgBJy7Qt8vlb4SfYD+4sRUKDJZrWYhvY07cWBzN2J+/eWk2SmeEFENjVssj+4bFJZs8WDOurFU/Fvppwq5Hb5Nl+oCUzHp5/Es=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key =  "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTHWlY6BKDN3aLOBrCS5N9a1nwTGbv7cf0UGUUYquJzQ7poE4Yd+2b6EAPZqUeH/ul37pLw/m93xwFJrhHJUrfwIGXsvUQ6bhJFirIZA+m4YBJOZzJax5Z7QeGbflluDZ4XSv+JlikG08iwgqYMKt0Hc63ewwPwcSWpfbQ7jsZL03ENXsORwe5icBWJY9RsrBLPkTwi9gkz1MWkwvmI3UvTag13r8xF86jkoJOOihjsJguHV7tjeXZ0Vn2rJ9PbM728NnTv5ju+5SuN5ebJ/C6o1xvgDkSyfOzoV2aA4a6wRD6ssE1QTofnT4k1bOUVBosJwJezoafWFEyZlbaaegQIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url= "http://100.112.214.114:9000/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://order.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                +"\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
