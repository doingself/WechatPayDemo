package cn.syc.wechat;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertPath;

public class WechatConfig implements WXPayConfig{

    private byte[] certData;

    /**
     * 付款不需要证书
     */
    public WechatConfig(){

    }

    /**
     * 退款使用证书
     * @param certPath
     * @throws Exception
     */
    public WechatConfig(String certPath) throws Exception {
        //String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);

        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public String getAppID() {
        // appId
        return "wx**************";
    }

    public String getMchID() {
        // 商户号
        return "11111111111";
    }

    public String getKey() {
        // 微信支付需要用到的partnerkey
        return "asdfasdfasdfasdfasdf";
    }

    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}
