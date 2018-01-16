package cn.syc.wechat.QRCode;

import cn.syc.wechat.WechatConfig;
import com.github.wxpay.sdk.WXPay;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "WechatQRCodeServlet")
public class WechatQRCodeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
业务流程说明：

（1）商户后台系统根据用户选购的商品生成订单。

（2）用户确认支付后调用微信支付【统一下单API】生成预支付交易；

（3）微信支付系统收到请求后生成预支付交易单，并返回交易会话的二维码链接code_url。

（4）商户后台系统根据返回的code_url生成二维码。

（5）用户打开微信“扫一扫”扫描二维码，微信客户端将扫码内容发送到微信支付系统。

（6）微信支付系统收到客户端请求，验证链接有效性后发起用户支付，要求用户授权。

（7）用户在微信客户端输入密码，确认支付后，微信客户端提交授权。

（8）微信支付系统根据用户授权完成支付交易。

（9）微信支付系统完成支付交易后给微信客户端返回交易结果，并将交易结果通过短信、微信消息提示用户。微信客户端展示支付交易结果页面。

（10）微信支付系统通过发送异步消息通知商户后台系统支付结果。商户后台系统需回复接收情况，通知微信后台系统不再发送该单的支付通知。

（11）未收到支付通知的情况，商户后台系统调用【查询订单API】。

（12）商户确认订单已支付后给用户发货。


微信公众平台：
在关注者与公众号产生消息交互后，公众号可获得关注者的OpenID（加密后的微信号，每个用户对每个公众号的OpenID是唯一的。对于不同公众号，同一用户的openid不同）。

公众号可根据以下接口来获取用户的openid，如需获取用户的昵称、头像、性别、所在城市、语言和关注时间，则需要用户授权。

参考信息：http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html

开发者如果需要将同一个用户在不同公众号下的openid统一为一个id来记录，可以参考以下接口：
参考信息：http://mp.weixin.qq.com/wiki/14/bb5031008f1494a59c6f71fa0f319c66.html



微信开放平台：
移动应用获取用户openid可使用以下接口：
https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN

网站应用获取用户openid可使用以下接口：
https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN
*/

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        //1. 创建订单
        String orderNo = "orderNo" + System.currentTimeMillis();
        String productId = "123";

        // 统一下单

        WechatConfig config = new WechatConfig();
        WXPay wxPay = new WXPay(config);

        Boolean isSuc = false;
        StringBuffer json = new StringBuffer();
        String RETURN_CODE = "return_code";
        String RESULT_CODE = "result_code";


        /*
商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
商品详情	detail	否	String(8192)	 	商品详细描述，对于使用单品优惠的商户，改字段必须按照规范上传，详见“单品优惠参数说明”
商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。详见商户订单号
总金额	total_fee	是	Int	888	订单总金额，单位为分，详见支付金额
终端IP	spbill_create_ip	是	String(16)	123.12.12.123	用户端实际ip
通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。

交易类型	trade_type	是	String(16)	JSAPI	取值如下：JSAPI，NATIVE，APP等，说明详见参数规定
JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里

商品ID	product_id	否	String(32)	12235413214070356458058	trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义。

*/
        // 在服务器创建订单后，封装微信统一下单参数
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "这是一次付款测试");
        // 服务器订单号，唯一
        data.put("out_trade_no", orderNo);
        data.put("trade_type", "NATIVE");
        data.put("product_id", productId);
        data.put("total_fee", "1");
        data.put("spbill_create_ip", "127.0.0.1");
        // 付款成功后，通知服务器地址
        data.put("notify_url", "http://www.example.com/wxpay/notify");

        String codeUrl = new String();
        try {
            // 统一下单，成功后，校验签名
            // 该方法同时会 添加 appid、mch_id、nonce_str、sign_type、sign
            Map<String, String> resp = wxPay.unifiedOrder(data);
            System.out.println(resp);

/*
在return_code 和result_code都为SUCCESS的时候有返回
字段名	变量名	必填	类型	示例值	描述
交易类型	trade_type	是	String(16)	JSAPI	交易类型，取值为：JSAPI，NATIVE，APP等，说明详见参数规定
预支付交易会话标识	prepay_id	是	String(64)	wx201410272009395522657a690389285100	微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
二维码链接	code_url	否	String(64)	URl：weixin：//wxpay/s/An4baqw	trade_type为NATIVE时有返回，用于生成二维码，展示给用户进行扫码支付
*/
            // 校验下单是否成功，后续需要再次签名
            if ("SUCCESS".equals(resp.get(RETURN_CODE)) && "SUCCESS".equals(resp.get(RESULT_CODE))){
                isSuc = true;
                if (resp.containsKey("code_url")) {
                    codeUrl = resp.get("code_url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServletOutputStream stream = response.getOutputStream();

        if (isSuc) {
            // 创建二维码
            int size = 200;
            String format = "png";

            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);
            try {
                BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE, size, size,hints);
                MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
            } catch (WriterException e) {
                e.printStackTrace();
                stream.print(e.getMessage());
            }
        }else{
            stream.print("统一下单失败");
        }

        stream.flush();
        stream.close();
    }
}
