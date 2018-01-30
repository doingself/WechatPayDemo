package cn.syc.wechat;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.*;
import java.security.cert.CertPath;

public class WechatConfig implements WXPayConfig{

    private byte[] certData;

    private static WechatConfig instance;

    public static WechatConfig getInstance() {
        if (instance == null){
            synchronized (WechatConfig.class){
                if (instance == null){
                    instance = new WechatConfig();
                }
            }
        }
        return instance;
    }

    /**
     * 退款使用证书
     */
    private WechatConfig(){
        // FIXME: 读取证书, 保存在 byte 中
        String certPath = this.getClass().getClassLoader().getResource("cer.txt").getPath();
        File file = new File(certPath);

        InputStream certStream = null;
        try {
            certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
            certStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * // 商户号
     * String mch_id = "1344462901";
     * String appid = "wxdc8d57db2a0da07c";
     * // 微信支付需要用到的密钥
     * String appsecret = "446c4b3017662240518c69c8444f2fab";
     * // 微信支付需要用到的partnerkey
     * String partnerkey = "rigourtechrigourmedia13161046875";
     * */
    public String getAppID() {
        // appId
        //return "wx**************";
        return "wxdc8d57db2a0da07c";
    }

    public String getMchID() {
        // 商户号
        //return "11111111111";
        return "1344462901";
    }

    public String getKey() {
        // 微信支付需要用到的partnerkey
        //return "asdfasdfasdfasdfasdf";
        return "rigourtechrigourmedia13161046875";
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
