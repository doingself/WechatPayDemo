# WechatPayDemo 微信支付 服务端+IOS客户端

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

## IOS 客户的

集成 wxSDK , 从服务器 `http://192.168.1.106:8080/pay?total=1` 获取签名及支付参数, 调起本地微信进行支付(已经装微信)

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

