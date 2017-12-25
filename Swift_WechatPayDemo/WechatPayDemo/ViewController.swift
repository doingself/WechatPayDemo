//
//  ViewController.swift
//  WechatPayDemo
//
//  Created by rigour on 2017/12/13.
//  Copyright © 2017年 test. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        let btn = UIButton(frame: CGRect(x: 50, y: 100, width: 100, height: 100))
        btn.layer.borderWidth = 1
        btn.layer.masksToBounds = true
        btn.layer.cornerRadius = 10
        btn.setTitle("wechat pay", for: UIControlState.normal)
        btn.setTitleColor(UIColor.black, for: UIControlState.normal)
        btn.setTitleColor(UIColor.lightGray, for: UIControlState.highlighted)
        btn.addTarget(self, action: #selector(self.btnAction(sender:)), for: UIControlEvents.touchUpInside)
        self.view.addSubview(btn)
    }

    @objc func btnAction(sender: Any?){
        sessionget()
    }
    func sessionget(){
        //1.创建请求路径
        let path = "http://192.168.1.106:8080/pay?total=1"
        //拼接参数(GET请求参数需要以"?"连接拼接到请求地址的后面，多个参数用"&"隔开，参数形式：参数名=参数值)
        //转换成url(统一资源定位符)
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
                let any = try! JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.mutableContainers)
                guard let dict = any as? [String: Any] else{
                    return
                }
                print(dict)
                
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
            }
            else{
                print("请求数据失败")
            }
        }
        //5.开始执行任务
        task.resume()
    }
    
    
    
    
    
    func sessionget2(){
        //1.创建请求路径
        let path = "http://192.168.1.106:8080/pay?total=1"
        //拼接参数(GET请求参数需要以"?"连接拼接到请求地址的后面，多个参数用"&"隔开，参数形式：参数名=参数值)
        //转换成url(统一资源定位符)
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
            
            //            print(response)
            //            print("能接受到")
            //            print(NSThread.currentThread())
            
            //解析json
            //参数options：.MutableContainers(json最外层是数组或者字典选这个选项)
            if data != nil{
                let dict = try! JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.mutableContainers)
                print(dict)
            }
            else{
                print("请求数据失败")
            }
        }
        //5.开始执行任务
        task.resume()
    }
    
//    func sessionpost(){
//        //1.创建请求地址
//        let url = NSURL(string: "http://0.0.0.0:tuicool@api.tuicool.com/api/signup/register_by_email.json")
//
//        //2.创建求情对象
//        //POST请求的请求对象必须使用NSMutableURLRequest创建，因为使用NSURLRequest不能更改请求方式，但是NSMutableURLRequest可以
//        let request = NSMutableURLRequest(URL: url!)
//        //设置请求方式(默认GET)
//        request.HTTPMethod = "POST" //设置请求方式为POST请求
//        //设置请求体(POST请求的参数是放到请求体中的)
//        //a.拼接字符串
//        //参数1=参数值1&参数2=参数2...
//        let sendStr = "email=10165910@163.com&name=鱼摆摆&password=123456789"
//        //b.将字符串转换成二进制
//        //参数：编码方式
//        let sendData = sendStr.dataUsingEncoding(NSUTF8StringEncoding)
//        request.HTTPBody = sendData
//
//        //3.创建session
//        //快速创建一个默认会话模式的session
//        let session = NSURLSession.sharedSession()
//
//        //4.创建任务
//        let task = session.dataTaskWithRequest(request) { (data, response, error) in
//            print(response)
//
//            if data != nil{
//                let ret = try! NSJSONSerialization.JSONObjectWithData(data!, options: .MutableContainers)
//                print(ret)
//            }
//            else{
//                print("请求失败")
//            }
//        }
//
//        //5.开始执行任务
//        task.resume()
//
//    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

