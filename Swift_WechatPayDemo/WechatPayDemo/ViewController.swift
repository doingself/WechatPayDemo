//
//  ViewController.swift
//  WechatPayDemo
//
//  Created by rigour on 2017/12/13.
//  Copyright © 2017年 test. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    private var txtField: UITextField!
    private var txtView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        var y: CGFloat = 80
        var x: CGFloat = 20
        txtField = UITextField(frame: CGRect(x: x, y: y, width: self.view.bounds.size.width-40, height: 35))
        txtField.placeholder = "输入服务器地址"
        txtField.borderStyle = .roundedRect
        txtField.text = "http://192.168.1.106:8080/"
        self.view.addSubview(txtField)
        
        y += 35 + 20
        let pay = getBtn(title: "支付", frame: CGRect(x: x, y: y, width: 100, height: 35))
        pay.addTarget(self, action: #selector(self.payAction(sender:)), for: UIControlEvents.touchUpInside)
        x += 100 + 30
        let query = getBtn(title: "查询", frame: CGRect(x: x, y: y, width: 100, height: 35))
        query.addTarget(self, action: #selector(self.queryAction(sender:)), for: UIControlEvents.touchUpInside)
        
        y += 35 + 20
        x = 20
        let refund = getBtn(title: "退款", frame: CGRect(x: x, y: y, width: 100, height: 35))
        refund.addTarget(self, action: #selector(self.refundAction(sender:)), for: UIControlEvents.touchUpInside)
        x += 100 + 30
        let refundQuery = getBtn(title: "退款查询", frame: CGRect(x: x, y: y, width: 100, height: 35))
        refundQuery.addTarget(self, action: #selector(self.refundQuery(sender:)), for: UIControlEvents.touchUpInside)
        
        y += 35 + 20
        txtView = UITextView(frame: CGRect(x: 20, y: y, width: self.view.bounds.size.width-40, height: self.view.bounds.height-y-20))
        txtView.layer.borderWidth = 1
        txtView.delegate = self
        self.view.addSubview(txtView)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        txtField.resignFirstResponder()
        txtView.resignFirstResponder()
    }
    
    // MARK: action
    @objc func payAction(sender: Any?){
        guard let server = self.txtField.text else{ return }
        let orderNo = "test2018" + String(arc4random())
        self.txtView.text = orderNo
        let path = server + "pay?total=1&orderNo=" + orderNo
        sessionDataTaskRequestResume(path: path) { (dict: [String: Any]) in
            if let sign = dict["sign"] as? String{
                //调起微信支付
                let req = PayReq()
                req.partnerId = dict["partnerid"] as! String
                req.prepayId  = dict["prepayid"] as! String
                req.nonceStr  = dict["noncestr"] as! String
                req.timeStamp = dict["timestamp"] as! UInt32
                req.package   = dict["package"] as! String
                req.sign      = sign
                DispatchQueue.main.async(execute: {
                    WXApi.send(req)
                })
            }else{
                let data = try! JSONSerialization.data(withJSONObject: dict, options: JSONSerialization.WritingOptions.prettyPrinted)
                let str = String(data: data, encoding: String.Encoding.utf8)
                DispatchQueue.main.async(execute: {
                    self.txtView.text = str
                })
            }
        }
    }
    @objc func queryAction(sender: Any?){
        guard let server = self.txtField.text else{ return }
        guard let no = txtView.text else{
            return
        }
        let path = server + "query?no=" + no
        sessionDataTaskRequestResume(path: path) { (dict:[String: Any]) in
            let data = try! JSONSerialization.data(withJSONObject: dict, options: JSONSerialization.WritingOptions.prettyPrinted)
            let str = String(data: data, encoding: String.Encoding.utf8)
            DispatchQueue.main.async(execute: {
                self.txtView.text = str
            })
        }
    }
    @objc func refundAction(sender: Any?){
        guard let server = self.txtField.text else{ return }
        guard let no = txtView.text else{
            return
        }
        let path = server + "refund?no=" + no
        sessionDataTaskRequestResume(path: path) { (dict:[String: Any]) in
            let data = try! JSONSerialization.data(withJSONObject: dict, options: JSONSerialization.WritingOptions.prettyPrinted)
            let str = String(data: data, encoding: String.Encoding.utf8)
            DispatchQueue.main.async {
                self.txtView.text = str
            }
        }
    }
    @objc func refundQuery(sender: Any?){
        guard let server = self.txtField.text else{ return }
        guard let no = txtView.text else{
            return
        }
        let path = server + "refundQuery?no=" + no
        sessionDataTaskRequestResume(path: path) { (dict:[String: Any]) in
            let data = try! JSONSerialization.data(withJSONObject: dict, options: JSONSerialization.WritingOptions.prettyPrinted)
            let str = String(data: data, encoding: String.Encoding.utf8)
            DispatchQueue.main.async(execute: {
                self.txtView.text = str
            })
        }
    }
    
    // MARK: private func
    private func getBtn(title: String, frame: CGRect) -> UIButton{
        let btn = UIButton(frame: frame)
        btn.layer.borderWidth = 1
        btn.layer.masksToBounds = true
        btn.layer.cornerRadius = 4
        btn.setTitle(title, for: UIControlState.normal)
        btn.setTitleColor(UIColor.black, for: UIControlState.normal)
        btn.setTitleColor(UIColor.lightGray, for: UIControlState.highlighted)
        self.view.addSubview(btn)
        return btn
    }
    
    private func sessionDataTaskRequestResume(path: String, completed: @escaping (_ dict: [String:Any])->Void){
        //1.创建请求路径
        //let path = "http://192.168.1.106:8080/pay?total=1"
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
                do{
                    let any = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.mutableContainers)
                    guard let dict = any as? [String: Any] else{
                        return
                    }
                    print(dict)
                    
                    completed(dict)
                } catch {
                    let str: String = String(data: data!, encoding: String.Encoding.utf8)!
                    DispatchQueue.main.async(execute: {
                        self.txtView.text = " 请求失败 -> " + path + str
                    })
                }
            }
            else{
                print("请求数据失败")
                DispatchQueue.main.async(execute: {
                    self.txtView.text = "请求失败 -> " + path
                })
            }
        }
        //5.开始执行任务
        task.resume()
    }
}

extension ViewController: UITextViewDelegate{
    
}
