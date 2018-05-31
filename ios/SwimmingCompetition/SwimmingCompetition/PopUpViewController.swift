//
//  PopUpViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 30/05/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class PopUpViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var toolBar: UIToolbar!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var birthDate: UITextField!
    @IBOutlet weak var scrollView: UIScrollView!
    
    let datePicker = UIDatePicker()
    var activeTextField: UITextField!
    @IBOutlet weak var viewIn: UIView!
    
    var currentUser: User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        email.delegate = self
        birthDate.delegate = self
        
        activeTextField = email
       
        birthDate.inputView = datePicker
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
        showAnimate()
        
        toolBarPicker()
        // Do any additional setup after loading the view.
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        activeTextField = textField
    }
    
    @objc func keyboardWillChange(notification: Notification) {
        guard let userInfo = notification.userInfo,
            let frame = (userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue else{return}
        var contentInset = UIEdgeInsets.zero
        
        if notification.name == Notification.Name.UIKeyboardWillShow ||
            notification.name == Notification.Name.UIKeyboardWillChangeFrame {
            contentInset = UIEdgeInsets(top: 0, left: 0, bottom: frame.height, right: 0)
        }
        
        scrollView.contentInset = contentInset
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    @IBAction func confirmButton(_ sender: Any) {
        var alert = UIAlertController(title: nil, message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        if email.text == "" || birthDate.text == "" {
            alert.message = "חובה למלא את השדות"
            self.present(alert, animated: true, completion: nil)
        } else {
            let parameters = [
                "uid": self.currentUser.uid as AnyObject,
                "email": self.email.text as AnyObject,
                "birthDate": self.birthDate.text as AnyObject
                ] as [String:AnyObject]
            
            Service.shared.connectToServer(path: "addChildToParent", method: .post, params: parameters, completion: { (response) in
                
                if response.succeed {
                    var user = User(json: response.data)
                    user.uid = response.data["uid"] as! String
                    if !self.currentUser.children.contains(where: {$0.uid == user.uid}) {
                        self.currentUser.children.append(user)
                        let childrenView = (self.parent as! MyChildrenViewController)
                        childrenView.currentUser = self.currentUser
                        childrenView.tableView.reloadData()
                        self.removeAnimate()
                    }
                    
                    
                } else {
                    alert.title = "הוספה נכשלה"
                    alert.message = "וודא שהנתונים שהכנסת נכונים"
                    self.present(alert, animated: true, completion: nil)
                }
            })
        }
        
    }
    @IBAction func cancelButton(_ sender: Any) {
        removeAnimate()
    }
    
    func showAnimate() {
        self.view.transform = CGAffineTransform(scaleX: 1.3, y: 1.3)
        self.view.alpha = 0.0
        UIView.animate(withDuration: 0.25) {
            self.view.alpha = 1.0
            self.view.transform = CGAffineTransform(scaleX: 1.0, y: 1.0)
        }
    }
    
    func removeAnimate() {
        UIView.animate(withDuration: 0.25, animations: {
            self.view.transform = CGAffineTransform(scaleX: 1.3, y: 1.3)
            self.view.alpha = 0.0
        }) { (finished) in
            if finished {
                self.view.removeFromSuperview()
            }
        }
    }
    
    func toolBarPicker() {
        datePicker.datePickerMode = .date
        datePicker.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
        
        let toolBar = UIToolbar()
        toolBar.sizeToFit()
        let doneButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(self.doneClicked))
        
        toolBar.setItems([doneButton], animated: false)
        
        birthDate.inputAccessoryView = toolBar

    }
    
    @objc func doneClicked() {
       
        let formatDate = DateFormatter()
        formatDate.dateFormat = "dd/MM/YYYY"
        self.birthDate.text = formatDate.string(from: datePicker.date)
        
        view.endEditing(true)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
