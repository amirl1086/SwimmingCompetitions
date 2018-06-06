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
    
    var senderView = UIViewController()
    
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
        
        if email.text == "" || birthDate.text == "" {
            
            self.present(Alert().confirmAlert(title: "", message: "חובה למלא את השדות"), animated: true, completion: nil)
        } else {
            
            if ((self.senderView as? CompetitionDetailsViewController) != nil) {
                addExistingUserToCompetition()
                
            } else if ((self.senderView as? MyChildrenViewController) != nil)  {
                addChild()
            }
        }
      
        
    }
    @IBAction func cancelButton(_ sender: Any) {
        removeAnimate()
    }
    
    func addChild() {
        let parameters = [
            "uid": self.currentUser.uid as AnyObject,
            "email": self.email.text as AnyObject,
            "birthDate": self.birthDate.text as AnyObject
            ] as [String:AnyObject]
        Service.shared.connectToServer(path: "addChildToParent", method: .post, params: parameters) { (response) in
            if response.succeed {
                let passData = self.senderView as! MyChildrenViewController
                let user = User(json: response.data)
                passData.currentUser.children.append(user)
                passData.tableView.reloadData()
                self.present(Alert().confirmAlert(title: "", message: "הוספה בוצעה בהצלחה"), animated: true, completion: {
                    self.removeAnimate()
                })
            } else {
                self.present(Alert().confirmAlert(title: "ההרשמה לא בוצעה", message: "וודא שהזנת פרטים נכונים"), animated: true, completion: nil)
            }
        }
    }
    
    func addExistingUserToCompetition() {
        let currentCompetition = (self.senderView as! CompetitionDetailsViewController).currentCompetition
        let parameters = [
            "competitionId": currentCompetition?.getId() as AnyObject,
            "uid": self.currentUser.uid as AnyObject,
            "email": self.email.text as AnyObject,
            "birthDate": self.birthDate.text as AnyObject
            ] as [String:AnyObject]
        if !validateBirthDate(birthDate: self.birthDate.text!, fromAge: Int((currentCompetition?.fromAge)!)!, toAge: Int((currentCompetition?.toAge)!)!) {
            self.present(Alert().confirmAlert(title: "", message: "גיל המשתמש אינו מתאים לתחרות"), animated: true, completion: nil)
        } else {
            Service.shared.connectToServer(path: "addExistingUserToCompetition", method: .post, params: parameters) { (response) in
                if response.succeed {
                    self.present(Alert().confirmAlert(title: "", message: "הרשמה בוצעה בהצלחה"), animated: true, completion: {
                        self.removeAnimate()
                    })
                    
                } else {
                    self.present(Alert().confirmAlert(title: "ההרשמה לא בוצעה", message: "וודא שהזנת פרטים נכונים"), animated: true, completion: nil)
                }
            }
        }
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

    func validateBirthDate(birthDate:String, fromAge: Int, toAge: Int) -> Bool {
        print(birthDate)
        print(DateConvert().getHowOld(date: birthDate)!)
        print(fromAge)
        print(toAge)
        if DateConvert().getHowOld(date: birthDate)! < fromAge || DateConvert().getHowOld(date: birthDate)! > toAge {
            return false
        }
        return true
    }

}
