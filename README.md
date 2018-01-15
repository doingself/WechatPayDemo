# WechatPayDemo 微信支付 服务端+IOS客户端

![image](https://github.com/doingself/WechatPayDemo/blob/master/images/image.png)

Demo 中没有业务系统, 所以付款时业务系统的订单号在 IOS 端生成, 保存在 UITextView 中, 方便订单查询及退款

## 服务端 JAVA

使用 Idea + maven 构建
集成 服务端 SDK, 实现统一下单并封装手机支付的参数

### maven 配置
```
  <dependencies>

  	<dependency> ... </dependency>

 	<!-- 微信支付 -->
    <dependency>
      <groupId>com.github.wxpay</groupId>
      <artifactId>WXPay-SDK-Java</artifactId>
      <version>0.0.4</version>
    </dependency>

  </dependencies>

  <repositories>
    <!-- 微信支付 -->
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
```

### 接口

demo 中都是 servlet get 请求

+ 支付 http://localhost:8080/pay?no=orderNo123
+ 支付结果通知 http://localhost:8080/notificate
+ 订单查询 http://localhost:8080/query?no=orderNo123

+ 退款 http://localhost:8080/refund?no=orderNo123
+ 退款通知 http://localhost:8080/refundNotificate
+ 退款查询 http://localhost:8080/refundQuery?no=orderNo123

## IOS 客户的

配置 URL Type

集成 wxSDK , 使用 SDK App Id 初始化

从服务器 `http://192.168.1.106:8080/pay` 获取签名及支付参数, 调起本地微信进行支付(已经安装微信)

```
//调起微信支付
let req = PayReq()
req.partnerId = dict["partnerid"] as! String
req.prepayId  = dict["prepayid"] as! String
req.nonceStr  = dict["noncestr"] as! String
req.timeStamp = dict["timestamp"] as! UInt32
req.package   = dict["package"] as! String
req.sign      = dict["sign"] as! String
DispatchQueue.main.async(execute: {
    WXApi.send(req)
})
```

