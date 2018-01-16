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
+ 支付结果通知 http://localhost:8080/notificate (**无法测试, 需要外网支持** )
+ 订单查询 http://localhost:8080/query?no=orderNo123
+ 退款 http://localhost:8080/refund?no=orderNo123
+ 退款通知 http://localhost:8080/refundNotificate (**无法测试, 需要外网支持** )
+ 退款查询 http://localhost:8080/refundQuery?no=orderNo123
+ 创建二维码 http://localhost:8080/qrcode

## PC Web 端

使用微信扫描web 端生成的二维码进行付款

```
  <!-- google zxing 二维码-->
  <dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.0.0</version>
  </dependency>
  <dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.0.0</version>
  </dependency>
```

使用 servlet 显示创建的二维码
```
ServletOutputStream stream = response.getOutputStream();
int size = 200;
String format = "png";

// 创建二维码
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
stream.flush();
stream.close();
```

## IOS 客户端

配置 URL Type

集成 wxSDK , 使用 App Id 初始化

从服务器 `http://localhost:8080/pay?no=orderNo123` 获取签名及支付参数, 调起本地微信进行支付(已经安装微信)

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

### 使用 session 进行ios get 请求

具体可以参考 []()

```
//1.创建请求路径
//拼接参数(GET请求参数需要以"?"连接拼接到请求地址的后面，多个参数用"&"隔开，参数形式：参数名=参数值)
let path = "http://192.168.1.106:8080/pay?no=orderNo123"
//转换成url
let url = URL(string: path)


//2.创建请求对象
//NSURLRequest类型的请求对象的请求方式一定是GET(默认GET且不能被改变)
let request = URLRequest(url: url!)


//3.根据会话模式创建session(创建默认会话模式)
//方式1：一般不采用
//let session = NSURLSession(configuration: NSURLSessionConfiguration.defaultSessionConfiguration())
//方式2：快速创建默认会话模式的session
let session = URLSession.shared


//4.创建任务
//参数1：需要发送的请求对象
//参数2：服务返回数据的时候需要执行的对应的闭包
//闭包参数1：服务器返回给客户端的数据
//闭包参数2：服务器响应信息
//闭包参数3：错误信息
let task = session.dataTask(with: request) { (data: Data?, response: URLResponse!, err: Error?) in
    //注意：当前这个闭包是在子线程中执行的，如果想要在这儿执行UI操作必须通过线程间的通信回到主线程
    //解析json
    //参数options：.MutableContainers(json最外层是数组或者字典选这个选项)
    if data != nil && data?.isEmpty == false{
        do{
            let any = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.mutableContainers)
            guard let dict = any as? [String: Any] else{
                return
            }
            print(dict)

            // doing somethind ...

        } catch {
            let str: String = String(data: data!, encoding: String.Encoding.utf8)!
            print("解析 JSON 失败" + str)
        }
    }
    else{
        print("请求数据失败")
    }
}


//5.开始执行任务
task.resume()

```