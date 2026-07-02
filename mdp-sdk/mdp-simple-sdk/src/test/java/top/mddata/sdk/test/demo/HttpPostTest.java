package top.mddata.sdk.test.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import junit.framework.TestCase;
import okhttp3.internal.http2.Header;
import top.mddata.sdk.core.client.OpenHttp;
import top.mddata.sdk.core.sign.SignUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 测试 Http 请求
 */
public class HttpPostTest extends TestCase {
    static OpenHttp httpTool = new OpenHttp();

    String url = "http://localhost:23456/api";
    String appKey = "ruoyi-vue-sso";
    /**
     * 应用秘钥，用于获取 accessToken
     */
    protected String appSecret = "0Tvtm2xrTWG7n2azkM4FPyHwS6c3NxjQjYKh";
    // 平台提供的私钥
    String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC/VS3h9FdV+k7YS0xwPZTGOuk0SQ4dWdFoxJ0cA3s63Rk0AjbU92zHj088pa56x0/xrlDQcZtI0cbuas7nU/bOx0eZ+0LY17EqPXmSLB1AdG8I9IEbaB+jzwPpznDI15MjZRaUpl4yr7syh1k2Ugzz8+pSUWzbb+urmmfqvn8POhYYSUrm4Zk5l+vrHE/u3bunogan3MK3eTPyx3Lt4jbPN6Y+znDcoawbTb/qZL75+YFFaCHndf/A7DJlBLlfMVxPQahUr5IInWjmECRNpRqLl5rOLQEsUWy1MiLBpAsesJxYzyejIZIuPnobEMfWkWDkgbwymIgWMzYvc7Q6hAhnAgMBAAECggEAJXHsKt6BASidqaMC8Kx8o1cAMOVjR8c+PnzMKqFbyqdeuVj9lixeM6gOX9YlEY5UTP5KfqDdPSEhB6QLniZGlS1XDAGqkXmVCKlDU6Iij2y6FkyTv+Ne3dYz89wdIpFeEH1GMA1nPhA6WKc4hHMGafAAmd+pqEB9JPZxA/YIM9hZtgk+aBUSgRVBxFPfaVXdaRjRmnCtW6hdee/GePH6R0X/ieFjIlFd+BVWsbSr3N8BKq18kWsh+pGzZXsR47aVEwwJwRrbFWjABjH0IS4wEuMQCi5B8VCEq0nb2Iz06y0IJ1Yaex5OPC7n0yjAJwgKDEsOsRIYIIOmkCv3+AM5QQKBgQDjUwY25HDT4jKBh56yKSAbZk9L9vRK6PCHwZ2yXQ0pyTPmpsqqk6VdbHmPZzs09CmSwLg/GxBdK21E/3OFJIjqLQ/+pqj/HTy7cSTqBEDNqNPE4FPz1UaWGOI8jm/Q8q+fxK0K/yGP/grWBohWKLKqZiBE7Bgd9z/DtC91ymKgsQKBgQDXd+FO0tPguQCu5dVIj7R4PLpYRAspXAMU0EFBm/XrbT1Ll4RpuBHl07AWWDjgapcWH8yF2WBq8WVCmZy2lrObfzkIJSGs0k/X1OyeTQShLFAXnF4POYh2pj3LFj8s9MHO8ah7ix1m2qH6BjsD3dlDKUqc1idzh5U3kJKaF5tAlwKBgQCYIZDwHXNQqXlpbCyNSK5/B7obuXqFw1xtTerOWi2cAFXmj0rkWwj4+8ZibRCXgKtt1eG4AdGyuIRY/6f8u5WROnUQ09IXYSaqvq6Ymh4QRGLsx8AHV3z0qFSHeD9mk3NrNcEksddxOO9hil+lYXkoRk5kMah2LWiT/Tsh1j6pEQKBgQC2O4mvJNhWA6H0SiYtDH1SA+qGpGXcQRnKDKhkWQeQaf+hYzB2SVu5yWPwQgU4qG3IJHTR75uAV1GRFmJYevTE2sDdhqoIhIdKv6av6+uydMv4bCORNNOZpdg1X0dnOkqAQBqDApGHX/oGgCaBiqwqBU45f1Y2e8FUEU4sTTLdWQKBgHFcgjZ5VZcS+1m+pVuHPxQx7anYodjknKCE/sB7Cw3pC0CsV0Etwn1Yg3MM6oAicJpqURYUyXKqn1L43xGiqlEn0CWUphGzf12Q9+W90QAPsItDfUXuJVnDOeOqUQZd9ryDiX/Gdf4rj4E2JjlYqP+/n4dI7g9dGDhWMRjhrWtk";

    public static String postJson(String url, Map<String, String> params, Header... headers) {
        try {
            Map<String, String> headerMap = new LinkedHashMap<>();
            for (Header header : headers) {
                headerMap.put(header.name.utf8(), header.value.utf8());
            }
            return httpTool.requestJson(url, JSON.toJSONString(params), headerMap);
        } catch (IOException e) {
            throw new RuntimeException("网络请求异常", e);
        }
    }

    protected static String buildUrlQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                sb.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().substring(1);
    }

    protected static String buildParamQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString().substring(1);
    }

    public void testToken() throws Exception {
        getToken();
    }

    private String getToken() throws Exception {
        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("appKey", appKey);
        params.put("method", "accessToken.get");
        params.put("version", "1.0");
        params.put("format", "json");
        params.put("charset", "utf-8");
        params.put("signType", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // 业务参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("appKey", appKey);
        bizContent.put("appSecret", appSecret);

        params.put("bizContent", JSON.toJSONString(bizContent));
        String content = SignUtil.getSignContent(params);

        String sign = SignUtil.rsaSign(content, privateKey, params.get("charset"), params.get("signType"));
        params.put("sign", sign);

        System.out.println("----------- 请求信息 -----------");
        System.out.println("请求参数：" + buildParamQuery(params));
        System.out.println("商户秘钥：" + privateKey);
        System.out.println("待签名内容：" + content);
        System.out.println("签名(sign)：" + sign);
        System.out.println("URL参数：" + buildUrlQuery(params));

        System.out.println("----------- 返回结果 -----------");
        String responseData = postJson(url, params);// 发送请求
        System.out.println(responseData);

        JSONObject jsonObject = JSON.parseObject(responseData);
        JSONObject data = jsonObject.getJSONObject("data");
        return data.getString("accessToken");
    }


    /**
     参数	            类型	    是否必填	    最大长度	    描述	            示例值
     appKey	        String	是	        32	    平台分配给开发者的应用ID	2014072300007148
     method	        String	是	        128	    接口名称	alipay.trade.fastpay.refund.query
     version	        String	是	        3	    调用的接口版本，固定为：1.0	1.0
     format	        String	否	        40	    仅支持JSON	JSON
     charset	        String	是	        10	    请求使用的编码格式，如utf-8,gbk,gb2312等	utf-8
     signType	    String	是	        10	    商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2	RSA2
     sign	        String	是	        344	    商户请求参数的签名串，详见签名	详见示例
     timestamp	    String	是	        19	    发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"	2014-07-24 03:07:50
     accessToken	String	否	        512	    访问令牌，通过 accessToken.get 接口获取
     bizContent	    String	是		请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
     */
    // 这个请求会路由到story服务
    public void testGetJson() throws Exception {

        String token = getToken();

        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("appKey", appKey);
        params.put("method", "user.getById");
        params.put("format", "json");
        params.put("charset", "utf-8");
        params.put("signType", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("version", "1.0");
        params.put("accessToken", token);

        // 业务参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("id", "45946774154457094");

        params.put("bizContent", JSON.toJSONString(bizContent));

        System.out.println("----------- 请求信息 -----------");
        System.out.println("请求参数：" + buildParamQuery(params));
        System.out.println("商户秘钥：" + privateKey);
        System.out.println("URL参数：" + buildUrlQuery(params));

        System.out.println("----------- 返回结果 -----------");
        String responseData = postJson(url, params);// 发送请求
        System.out.println(responseData);
    }

    // 输出返回xml格式
    public void testGetXml() throws Exception {

        String token = getToken();
        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("appKey", appKey);
        params.put("method", "user.getById");
        params.put("version", "1.0");
        params.put("format", "xml");
        params.put("charset", "utf-8");
        params.put("signType", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        params.put("accessToken", token);

        // 业务参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("id", "45946774154457094");

        params.put("bizContent", JSON.toJSONString(bizContent));

        System.out.println("----------- 请求信息 -----------");
        System.out.println("请求参数：" + buildParamQuery(params));
        System.out.println("商户秘钥：" + privateKey);
        System.out.println("URL参数：" + buildUrlQuery(params));

        System.out.println("----------- 返回结果 -----------");
        String responseData = postJson(url, params);// 发送请求
        System.out.println(responseData);
    }

}
