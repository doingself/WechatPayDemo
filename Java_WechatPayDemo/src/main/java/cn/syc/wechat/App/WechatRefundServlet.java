package cn.syc.wechat.App;

import cn.syc.wechat.WechatConfig;
import com.github.wxpay.sdk.WXPay;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "WechatRefundServlet")
public class WechatRefundServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String orderNo = request.getParameter("no");
        // 一笔退款失败后重新提交，请不要更换退款单号，请使用原商户退款单号。
        String orderRefundNo = "testRefundNo" + System.currentTimeMillis();
        String totalFee = "1";
        String refundFee = "1";

        WechatConfig config = new WechatConfig("cer file");
        WXPay wxPay = new WXPay(config);

        /*
公众账号ID	appid	是	String(32)	wx8888888888888888	微信分配的公众账号ID（企业号corpid即为此appId）
商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
签名类型	sign_type	否	String(32)	HMAC-SHA256	签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
二选一
    微信订单号	transaction_id		String(32)	1217752501201407033233368018	微信生成的订单号，在支付通知中有返回
    商户订单号	out_trade_no	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
商户退款单号	out_refund_no	是	String(64)	1217752501201407033233368018	商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
订单金额	total_fee	是	Int	100	订单总金额，单位为分，只能为整数，详见支付金额
退款金额	refund_fee	是	Int	100	退款总金额，订单总金额，单位为分，只能为整数，详见支付金额
货币种类	refund_fee_type	否	String(8)	CNY	货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
退款原因	refund_desc	否	String(80)	商品已售完	若商户传入，会在下发给用户的退款消息中体现退款原因
退款资金来源	refund_account	否	String(30)	REFUND_SOURCE_RECHARGE_FUNDS
*/
        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", orderNo);
        data.put("out_refund_no", orderRefundNo);
        data.put("total_fee", totalFee);
        data.put("refund_fee", refundFee);

        String RETURN_CODE = "return_code";
        Boolean isSuc = false;

        PrintWriter out = response.getWriter();
        try {
            // 订单 退款
            // https://api.mch.weixin.qq.com/secapi/pay/refund
            // 该方法同时会 添加 appid、mch_id、nonce_str、sign_type、sign
            Map<String, String> resp = wxPay.refund(data);
            if (resp.containsKey(RETURN_CODE) ){
                String return_code = resp.get(RETURN_CODE);
                if ("SUCCESS".equals(return_code)){
                    isSuc = true;
                }
            }

            if (isSuc){
                // 退款申请 提交成功
            }else{
                // 退款申请 提交失败
            }

            System.out.println(resp);
            out.print(resp);

        } catch (Exception e) {
            e.printStackTrace();

            out.print(e.getMessage());
        }
        out.flush();
        out.close();
    }
}
