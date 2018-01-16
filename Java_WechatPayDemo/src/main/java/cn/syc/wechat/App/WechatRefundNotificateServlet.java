package cn.syc.wechat.App;

import cn.syc.wechat.WechatConfig;
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

@WebServlet(name = "WechatRefundNotificateServlet")
public class WechatRefundNotificateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        // 参考 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_16&index=10

        String resultStr = new String();

 /*
开通该功能需要在商户平台-交易中心-退款配置中配置notify_url。
如果链接无法访问，商户将无法接收到微信通知。
通知url必须为直接可访问的url，不能携带参数。示例：notify_url：“https://pay.weixin.qq.com/wxpay/pay.action”
*/
        // 支付结果通知的xml格式数据
        String inputLine;
        StringBuilder notityXml = new StringBuilder();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        //微信给返回的东西
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notityXml.append(inputLine);
            }
            request.getReader().close();
        } catch (Exception e) {
            e.printStackTrace();

            resultStr = e.getMessage();
        }

        WechatConfig config = WechatConfig.getInstance();
        WXPay wxPay = new WXPay(config);

        try {
            // 转换成map
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notityXml.toString());

            // 校验签名
            if (wxPay.isPayResultNotifySignatureValid(notifyMap)) {
                // 签名正确

                //订单金额校验
                //业务处理

                // 所有校验通过,业务处理完成,通知微信
                Map<String, String > result = new HashMap<String, String>();
                result.put("return_code","SUCCESS");
                result.put("return_msg","OK");
                resultStr = WXPayUtil.mapToXml(result);
            }
            else {
                // 签名错误，如果数据里没有sign字段，也认为是签名错误
            }
        }catch (Exception ex){
            ex.printStackTrace();
            resultStr = ex.getMessage();
        }

        PrintWriter out = response.getWriter();
        out.print(resultStr);
        out.flush();
        out.close();
    }
}
