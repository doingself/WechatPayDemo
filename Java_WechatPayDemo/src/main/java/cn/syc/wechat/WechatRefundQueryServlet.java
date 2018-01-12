package cn.syc.wechat;

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

@WebServlet(name = "WechatRefundQueryServlet")
public class WechatRefundQueryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String orderNo = request.getParameter("no");

        WechatConfig config = new WechatConfig();
        WXPay wxPay = new WXPay(config);

        /*
应用ID	appid	是	String(32)	wx8888888888888888	微信开放平台审核通过的应用APPID
商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
四选一
    微信订单号	transaction_id		String(32)	1217752501201407033233368018	微信订单号查询的优先级是： refund_id > out_refund_no > transaction_id > out_trade_no
    商户订单号	out_trade_no	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
    商户退款单号	out_refund_no	String(64)	1217752501201407033233368018	商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
    微信退款单号	refund_id	String(32)	1217752501201407033233368018	微信生成的退款单号，在申请退款接口有返回
偏移量	offset	否	Int	15
*/
        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", orderNo);

        PrintWriter out = response.getWriter();
        try {
            // 退款 查询
            // 接口链接：https://api.mch.weixin.qq.com/pay/refundquery
            // 该方法同时会 添加 appid、mch_id、nonce_str、sign_type、sign
            Map<String, String> resp = wxPay.refundQuery(data);
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
