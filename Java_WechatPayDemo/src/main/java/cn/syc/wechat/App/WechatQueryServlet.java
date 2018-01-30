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

@WebServlet(name = "WechatQueryServlet")
public class WechatQueryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // 通过系统订单号查询订单
        String orderNo = request.getParameter("no");

        WechatConfig config = WechatConfig.getInstance();
        WXPay wxPay = new WXPay(config);

        /*
应用APPID	appid	是	String(32)	wxd678efh567hg6787	微信开放平台审核通过的应用APPID
商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
二选一
    微信订单号	transaction_id		String(32)	1009660380201506130728806387	微信的订单号，优先使用
    商户订单号	out_trade_no	String(32)	20150806125346	商户系统内部的订单号，当没提供transaction_id时需要传这个。
随机字符串	nonce_str	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	随机字符串，不长于32位。推荐随机数生成算法
签名	sign	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS
*/
        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", orderNo);

        PrintWriter out = response.getWriter();
        try {
            // 订单查询
            // https://api.mch.weixin.qq.com/pay/orderquery
            // 该方法同时会 添加 appid、mch_id、nonce_str、sign_type、sign
            Map<String, String> resp = wxPay.orderQuery(data);

            // TODO: 解析订单信息并展示

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
