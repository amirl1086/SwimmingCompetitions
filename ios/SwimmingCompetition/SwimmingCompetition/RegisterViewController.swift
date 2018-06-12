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
import SwiftSpinner

protocol userProtocol {
    func dataChanged(user: User)
}

class RegisterViewController: UIViewController, UITextFieldDelegate {
    
    var currentUser: User!
    var delegate: userProtocol?
    
    
    @IBOutlet var scrollView: UIScrollView!
    
    //===== Text fields =====//
    @IBOutlet weak var genderTitle: UILabel!
    @IBOutlet var gender: UISegmentedControl!
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var birthDate: UITextField!
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
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        scrollView.isScrollEnabled = true
        scrollView.isUserInteractionEnabled = true
        firstName.delegate = self
        lastName.delegate = self
        birthDate.delegate = self
        email.delegate = self
        password.delegate = self
        passwordConfirmation.delegate = self
        productNumber.delegate = self
        
        activeTextField = firstName
        
        if isGoogleRegister {
            self.firstName.text = googleUser.profile.givenName != nil ? googleUser.profile.givenName! : ""
            self.lastName.text = googleUser.profile.familyName != nil ? googleUser.profile.familyName! : ""
            self.email.text = googleUser.profile.email != nil ? googleUser.profile.email! : ""
            self.email.isEnabled = false
            self.email.textColor = UIColor.darkGray
            self.password.isHidden = true
            self.passwordConfirmation.isHidden = true
        }
        
        if currentUser != nil {
            self.title = "פרטים אישיים"
            self.userType = self.currentUser.type
            cancelEditDetails()
        }
        
        changeRegisterView()
        
        // Do any additional setup after loading the view, typically from a nib.
        backgroundView = UIImageView(frame: self.view.bounds)
        backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(backgroundView, at: 0)
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
    
    override func viewDidLayoutSubviews() {
        
        backgroundView.frame = self.view.bounds
        
        firstName.bottomLineBorder()
        lastName.bottomLineBorder()
        birthDate.bottomLineBorder()
        email.bottomLineBorder()
        password.bottomLineBorder()
        passwordConfirmation.bottomLineBorder()
        productNumber.bottomLineBorder()

    }
    
    @objc func cancelEditDetails() {
        self.firstName.text = self.currentUser.firstName
        self.lastName.text = self.currentUser.lastName
        self.birthDate.text = self.currentUser.birthDate
        self.email.text = self.currentUser.email
        if currentUser.gender == "female" {
            self.gender.selectedSegmentIndex = 0
        } else {
            self.gender.selectedSegmentIndex = 1
        }
        
        self.email.isEnabled = false
        self.email.textColor = UIColor.darkGray
        self.firstName.isEnabled = false
        self.firstName.textColor = UIColor.darkGray
        self.lastName.isEnabled = false
        self.lastName.textColor = UIColor.darkGray
        self.birthDate.isEnabled = false
        self.birthDate.textColor = UIColor.darkGray
        self.gender.isEnabled = false
        self.gender.tintColor = UIColor.darkGray
        
        let editButton = UIBarButtonItem(title: "ערוך", style: .done, target: self, action: #selector(editDetails))
        self.navigationItem.rightBarButtonItem = editButton
        
        self.confirmButton.isHidden = true
        self.password.isHidden = true
        self.passwordConfirmation.isHidden = true
        self.productNumber.isHidden = true
    }
    
    @objc func editDetails() {
        
        let cancelEditButton = UIBarButtonItem(title: "בטל עריכה", style: .done, target: self, action: #selector(cancelEditDetails))
        self.navigationItem.rightBarButtonItem = cancelEditButton
        
        //self.email.isEnabled = true
        //self.email.textColor = UIColor.black
        self.firstName.isEnabled = true
        self.firstName.textColor = UIColor.black
        self.lastName.isEnabled = true
        self.lastName.textColor = UIColor.black
        self.birthDate.isEnabled = true
        self.birthDate.textColor = UIColor.black
        self.gender.isEnabled = true
        self.gender.tintColor = UIColor.blue
        
        self.confirmButton.setTitle("שמור שינויים", for: .normal)
        self.confirmButton.isHidden = false
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
        if userType == "parent" || userType == "coach" {
            self.genderTitle.isHidden = true
            self.gender.isHidden = true
            self.birthDate.isHidden = true
        }
    }
    
    //Button to register and create the user
    @IBAction func confirmRegister(_ sender: AnyObject) {
        
        var genderToSend = ""
        if gender.titleForSegment(at: gender.selectedSegmentIndex)! == "זכר" {
            genderToSend = "male"
        } else {
            genderToSend = "female"
        }
        
        var parameters = [
            "uid": Auth.auth().currentUser != nil ? Auth.auth().currentUser!.uid : "",
            "firstName": firstName.text!,
            "lastName": lastName.text!,
            "birthDate": birthDate.text != nil ? birthDate.text! : "",
            "gender": genderToSend,
            "email": email.text!,
            "password": password.text != nil ? password.text! : "",
            "passwordConfirmation": passwordConfirmation.text != nil ? passwordConfirmation.text! : "",
            "type": userType
            ] as [String: AnyObject]
        
        if isGoogleRegister {
            parameters["token"] = self.productNumber.text! as AnyObject
            if firstName.text == "" || lastName.text == "" || (birthDate.text == "" && self.userType == "student") || email.text == "" || productNumber.text == "" {
                self.present(Alert().confirmAlert(title: "", message: "נא למלא את כל השדות"), animated: true, completion: nil)
            } else {
                Service.shared.connectToServer(path: "updateFirebaseUser", method: .post, params: parameters) { (response) in
                    let sb = UIStoryboard(name: "Main", bundle: nil)
                    if response.succeed {
                        UserDefaults.standard.set(true, forKey: "loggedIn")
                        UserDefaults.standard.synchronize()
                        /*self.present(Alert().confirmAlert(title: "", message: "נרשמת למערכת בהצלחה"), animated: true, completion: nil)*/
                        if let mainView = sb.instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                            mainView.currentUser = User(json: response.data)
                            self.navigationController?.viewControllers = [mainView]
                        }
                    } else {
                        UserDefaults.standard.set(false, forKey: "loggedIn")
                        UserDefaults.standard.synchronize()
                        var title = ""
                        var message = ""
                        if ((response.data["message"] as? String) != nil) {
                            switch(response.data["message"] as! String) {
                            case "token_dont_match":
                                title = "מפתח מוצר לא נכון"
                                message = "נא לפנות לאחראי"
                                break
                            case "The email address is already in use by another account.":
                                message = "כתובת אימייל כבר קיימת"
                                break;
                            case "The password must be a string with at least 6 characters.":
                                message = "סיסמא חייבת להכיל לפחות 6 תוים"
                                break;
                            case "The email address is improperly formatted.":
                                message = "כתובת אימייל לא חוקית"
                                break;
                            default:
                                message = response.data["message"] as! String
                                break
                            }
                        }
                        self.present(Alert().confirmAlert(title: title, message: message), animated: true, completion: nil)
                        /*if let loginView = sb.instantiateViewController(withIdentifier: "loginID") as? LoginViewController {
                            self.navigationController?.viewControllers = [loginView]
                        }*/
                    }
                }
            }
        } else if self.currentUser != nil {
            parameters["type"] = self.currentUser.type as AnyObject
            if firstName.text == "" || lastName.text == "" || (birthDate.text == "" && self.userType == "student") {
                self.present(Alert().confirmAlert(title: "", message: "נא למלא את כל השדות"), animated: true, completion: nil)
            } else {
                Service.shared.connectToServer(path: "updateFirebaseUser", method: .post, params: parameters) { (response) in
                    if response.succeed {
                        self.present(Alert().confirmAlert(title: "", message: "הפרטים נשמרו בהצלחה"), animated: true, completion: nil)
                        let user = User(json: response.data)
                        self.currentUser = user
                        self.delegate?.dataChanged(user: self.currentUser)
                        self.cancelEditDetails()
                    } else {
                        self.present(Alert().confirmAlert(title: "שגיאה", message: "פרטים לא נשמרו"), animated: true, completion: nil)
                    }
                }
            }
            
        } else {
            parameters["token"] = self.productNumber.text! as AnyObject
            if firstName.text == "" || lastName.text == "" || (birthDate.text == "" && self.userType == "student") || email.text == "" || password.text == "" || passwordConfirmation.text == "" || productNumber.text == "" {
                self.present(Alert().confirmAlert(title: "", message: "נא למלא את כל השדות"), animated: true, completion: nil)
            }
            else if password.text != passwordConfirmation.text {
                self.present(Alert().confirmAlert(title: "", message: "הסיסמאות אינן תואמות"), animated: true, completion: nil)
            }
            else {
                Service.shared.connectToServer(path: "addNewUser", method: .post, params: parameters) { (response) in
                    
                    if response.succeed {
                        let alert = UIAlertController(title: nil, message: "נרשמת למערכת בהצלחה", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
                            alert.dismiss(animated: true, completion: nil)
                            SwiftSpinner.show("מתחבר")
                            Auth.auth().signIn(withEmail: self.email.text!, password: self.password.text!, completion: { (user, error) in
                                SwiftSpinner.hide()
                                let sb = UIStoryboard(name: "Main", bundle: nil)
                                if error != nil {
                                    UserDefaults.standard.set(false, forKey: "loggedIn")
                                    UserDefaults.standard.synchronize()
                                    if let loginView = sb.instantiateViewController(withIdentifier: "loginID") as? LoginViewController {
                                        self.navigationController?.viewControllers = [loginView]
                                    }
                                } else {
                                    UserDefaults.standard.set(true, forKey: "loggedIn")
                                    UserDefaults.standard.synchronize()
                                    
                                    if let mainView = sb.instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                                        mainView.currentUser = User(json: response.data)
                                        self.navigationController?.viewControllers = [mainView]
                                    }
                                }
                            })
                        }))
                        self.present(alert, animated: true, completion: nil)
                        
                    }
                    else {
                        var title = "שגיאה"
                        var message = ""
                        if ((response.data["message"] as? String) != nil) {
                            switch(response.data["message"] as! String) {
                            case "token_dont_match":
                                title = "מפתח מוצר לא נכון"
                                message = "נא לפנות לאחראי"
                                break
                            case "The email address is already in use by another account.":
                                message = "כתובת אימייל כבר קיימת"
                                break;
                            case "The password must be a string with at least 6 characters.":
                                message = "סיסמא חייבת להכיל לפחות 6 תוים"
                                break;
                            case "The email address is improperly formatted.":
                                message = "כתובת אימייל לא חוקית"
                                break;
                            default:
                                message = response.data["message"] as! String
                                break
                            }
                        }
                        self.present(Alert().confirmAlert(title: title, message: message), animated: true, completion: nil)
                        
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
