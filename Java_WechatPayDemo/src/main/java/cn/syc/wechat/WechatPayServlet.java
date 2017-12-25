package cn.syc.wechat;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "WechatPayServlet")
public class WechatPayServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String total = request.getParameter("total");

        WechatConfig config = new WechatConfig();
        WXPay wxPay = new WXPay(config);

        Boolean isSuc = false;
        StringBuffer json = new StringBuffer();
        String RETURN_CODE = "return_code";

        if (total == null || total.length() == 0){
            total = "1";
        }


        /*
商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
商品详情	detail	否	String(8192)	 	商品详细描述，对于使用单品优惠的商户，改字段必须按照规范上传，详见“单品优惠参数说明”
商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。详见商户订单号
总金额	total_fee	是	Int	888	订单总金额，单位为分，详见支付金额
终端IP	spbill_create_ip	是	String(16)	123.12.12.123	用户端实际ip
通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
交易类型	trade_type	是	String(16)	APP	支付类型
*/
        // 在服务器创建订单后，封装微信统一下单参数
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "这是一次付款测试");
        // 服务器订单号，唯一
        data.put("out_trade_no", "test201712121510001");
        data.put("total_fee", total);
        // 手机客户端ip
        data.put("spbill_create_ip", "127.0.0.1");
        // 付款成功后，通知服务器地址
        data.put("notify_url", "http://www.example.com/wxpay/notify");
        data.put("trade_type", "APP");

        try {
            // 统一下单，成功后，校验签名
            // 该方法同时会 添加 appid、mch_id、nonce_str、sign_type、sign
            Map<String, String> resp = wxPay.unifiedOrder(data);

            // 校验下单是否成功，后续需要再次签名
            if (resp.containsKey(RETURN_CODE)) {
                String return_code = resp.get(RETURN_CODE);
                if (return_code.equals("SUCCESS")) {
                    isSuc = true;
                }
            }

            if (isSuc){
                // 统一下单成功

                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                // 再次签名，用于调起客户端
                Map<String, String> signMap = new HashMap<String, String>();
                signMap.put("appid",resp.get("appid"));
                signMap.put("noncestr",resp.get("nonce_str"));
                signMap.put("package","Sign=WXPay");
                signMap.put("partnerid",resp.get("mch_id"));
                signMap.put("prepayid",resp.get("prepay_id"));
                signMap.put("timestamp",timestamp);
                String newSign = WXPayUtil.generateSignature(signMap,config.getKey());

                // FIXME: 可以封装为Map 并转换 Json，此次封装为json String
                json.append("{\"appid\":\"");
                json.append(resp.get("appid"));
                json.append("\",\"noncestr\":\"");
                json.append(resp.get("nonce_str"));
                json.append("\",\"package\":\"");
                json.append("Sign=WXPay");
                json.append("\",\"partnerid\":\"");
                json.append(resp.get("mch_id"));
                json.append("\",\"prepayid\":\"");
                json.append(resp.get("prepay_id"));
                json.append("\",\"timestamp\":");
                json.append(timestamp);
                json.append(",\"sign\":\"");
                json.append(newSign);
                json.append("\"}");

                System.out.println(resp);
                System.out.println(json.toString());

            }else{
                // 统一下单失败
                json.append("{\"return_code\":\"");
                json.append(resp.get("return_code"));
                json.append("\"}");

                System.out.println(json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
        out.close();
    }
}
