//
//  RegisterViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import Firebase
import GoogleSignIn

class RegisterViewController: UIViewController, UITextFieldDelegate {
    
    //===== Text fields =====//
    @IBOutlet var gender: UISegmentedControl!
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var birthDate: UITextField!
    @IBOutlet weak var phoneNumber: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var passwordConfirmation: UITextField!
    @IBOutlet weak var productNumber: UITextField!
    var activeTextField: UITextField!
    //========================//
    @IBOutlet weak var confirmButton: UIButton!
    
    let datePicker = UIDatePicker()
    var dateToSend = ""
    
    var userType = String()
    var isGoogleRegister = false
    var googleUser: GIDGoogleUser! = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        firstName.delegate = self
        lastName.delegate = self
        birthDate.delegate = self
        phoneNumber.delegate = self
        email.delegate = self
        password.delegate = self
        passwordConfirmation.delegate = self
        productNumber.delegate = self
        productNumber.isHidden = true
        
        activeTextField = firstName
        
        if isGoogleRegister {
            self.firstName.text = googleUser.profile.givenName!
            self.lastName.text = googleUser.profile.familyName!
            self.email.text = googleUser.profile.email!
            self.email.isEnabled = false
        }
        
        // Do any additional setup after loading the view, typically from a nib.
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        toolBar()
        //go to for change the register view
        //changeRegisterView()
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
        guard let keyboardRect = (notification.userInfo![UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue else {
            return
        }
        if notification.name == Notification.Name.UIKeyboardWillShow ||
            notification.name == Notification.Name.UIKeyboardWillChangeFrame {
            if ((view.frame.height - keyboardRect.height) <= (activeTextField.frame.origin.y+activeTextField.frame.height)) {
                view.frame.origin.y = -keyboardRect.height
            
            } else {
                view.frame.origin.y = 0
            }
            
        } else {
            view.frame.origin.y = 0
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //function for change the register view accorting to user type
    func changeRegisterView() {
        if userType == "parent" {
            
            (firstName.frame.origin, email.frame.origin) = (email.frame.origin, firstName.frame.origin)
            (lastName.frame.origin, password.frame.origin) = (password.frame.origin, lastName.frame.origin)
            (birthDate.frame.origin, passwordConfirmation.frame.origin) = (passwordConfirmation.frame.origin, birthDate.frame.origin)
            (phoneNumber.frame.origin, productNumber.frame.origin) = (productNumber.frame.origin, phoneNumber.frame.origin)
            
            firstName.isHidden = true
            lastName.isHidden = true
            birthDate.isHidden = true
            phoneNumber.isHidden = true
            confirmButton.frame.origin = CGPoint(x: 150, y: 300)
            
        }
    }
    
    //Button to register and create the user
    @IBAction func confirmRegister(_ sender: AnyObject) {
        
        var genderToSend = ""
        if gender.titleForSegment(at: gender.selectedSegmentIndex)! == "זכר" {
            genderToSend = "male"
        }
        else {
            genderToSend = "female"
        }
        
        let parameters = [
            "uid": Auth.auth().currentUser != nil ? Auth.auth().currentUser!.uid : "",
            "firstName": firstName.text!,
            "lastName": lastName.text!,
            "birthDate": birthDate.text!,
            "phoneNumber": phoneNumber.text!,
            "gender": genderToSend,
            "email": email.text!,
            "password": password.text != nil ? password.text! : "",
            "passwordConfirmation": passwordConfirmation.text != nil ? passwordConfirmation.text! : "",
            "type": userType
            ] as [String: AnyObject]
        
        if isGoogleRegister {
            if firstName.text == "" || lastName.text == "" || birthDate.text == "" || phoneNumber.text == "" || email.text == "" {
                let alert = UIAlertController(title: nil, message: "חובה למלא את כל השדות!", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                }))
                self.present(alert, animated: true, completion: nil)
            }
            else {
                Service.shared.connectToServer(path: "updateFirebaseUser", method: .post, params: parameters) { (response) in
                    
                }
            }
        } else {
            if firstName.text == "" || lastName.text == "" || birthDate.text == "" || phoneNumber.text == "" || email.text == "" || password.text == "" || passwordConfirmation.text == ""{
                let alert = UIAlertController(title: nil, message: "חובה למלא את כל השדות!", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                }))
                self.present(alert, animated: true, completion: nil)
            }
            else if password.text != passwordConfirmation.text {
                let alert = UIAlertController(title: nil, message: "הסיסמאות אינן תואמות", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                }))
                self.present(alert, animated: true, completion: nil)
            }
            else {
                
                
                Service.shared.connectToServer(path: "addNewUser", method: .post, params: parameters) { (response) in
                    
                    if response.succeed {
                        let alert = UIAlertController(title: nil, message: "נרשמת למערכת בהצלחה", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
                            alert.dismiss(animated: true, completion: nil)
                            _ = self.navigationController?.popViewController(animated: true)
                        }))
                        self.present(alert, animated: true, completion: nil)
                        
                    }
                    else {
                        var message = ""
                        switch(response.data["code"] as! String) {
                        case "auth/email-already-exists":
                            message = "כתובת אימייל כבר קיימת"
                            break;
                        case "auth/internal-error":
                            message = "מספר הטלפון לא תקין"
                            break;
                        case "auth/invalid-password":
                            message = "סיסמא חייבת להכיל לפחות 6 תוים"
                            break;
                        case "auth/invalid-email":
                            message = "כתובת אימייל לא חוקית"
                            break;
                        default:
                            message = "שגיאה"
                            break;
                        }
                        let alert = UIAlertController(title: "שגיאה", message: message, preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                            alert.dismiss(animated: true, completion: nil)
                        }))
                        self.present(alert, animated: true, completion: nil)
                    }
                }
                
            }
        }
        
        
    }
    @IBAction func dateTextBegin(_ sender: Any) {
        birthDate.inputView = datePicker
    }
    
    func toolBar() {
        datePicker.datePickerMode = .date
        datePicker.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
        
        let toolBar = UIToolbar()
        toolBar.sizeToFit()
        let doneButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(self.doneClicked))
        
        toolBar.setItems([doneButton], animated: false)
        
        birthDate.inputAccessoryView = toolBar
        
    }
    
    @objc func doneClicked() {
        if birthDate.inputView == datePicker {
            let formatDate = DateFormatter()
            formatDate.dateFormat = "dd/MM/YYYY"
            
            birthDate.text = formatDate.string(from: datePicker.date)
            
            formatDate.dateFormat = "dd/MM/yyyy HH:mm"
            
            
            dateToSend = formatDate.string(from: datePicker.date)
        }
        
        view.endEditing(true)
    }
    
}
