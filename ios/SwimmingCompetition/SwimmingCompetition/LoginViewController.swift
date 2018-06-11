//
//  LoginViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import Firebase
import Alamofire
import SwiftyJSON
import Google
import GoogleSignIn
import SwiftSpinner

class LoginViewController: UIViewController, UITextFieldDelegate, GIDSignInDelegate, GIDSignInUIDelegate {
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        print("hhhhh")
    }
    
    @IBOutlet weak var logo: UIImageView!
    
    //The input email and password for login
    @IBOutlet weak var emailTextFiled: UITextField!
    @IBOutlet weak var passwordTextFiled: UITextField!
    var activeTextField: UITextField!
    
    @IBOutlet var scrollView: UIScrollView!
    @IBOutlet var loginButton: RoundButton!
    var signInWithGooogleButton: GIDSignInButton!
    
    var mainView = MainViewController()
    var user: User!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
  
        super.viewDidLoad()
        
        emailTextFiled.delegate = self
        passwordTextFiled.delegate = self
       
        activeTextField = emailTextFiled
     
        /* Set the logo image */
        self.logo.image = UIImage(named: "logo.png")
        
        /* Set the background */
        backgroundView = UIImageView(frame: self.view.bounds)
        backgroundView.image = UIImage(named: "waterpool_bottom.jpg")//if its in images.xcassets
        self.view.insertSubview(backgroundView, at: 0)
        
        /* Observer for keyboard */
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
        
        /* Sign out the google account if the login controller appear */
        GIDSignIn.sharedInstance().signOut()
        
        /* Set the sign in with google method and create the button */
        /*GIDSignIn.sharedInstance().uiDelegate = self
        signInWithGooogleButton = GIDSignInButton(frame: CGRect(origin: CGPoint(x: 0, y: self.loginButton.frame.origin.y + self.loginButton.frame.height + 20), size: CGSize(width: 100, height: 50)))
        scrollView.addSubview(signInWithGooogleButton)*/
        scrollView.isScrollEnabled = true
        scrollView.isUserInteractionEnabled = true
        
    }
    
    override func viewDidLayoutSubviews() {
        backgroundView.frame = self.view.bounds
        //signInWithGooogleButton.center.x = self.view.center.x
        emailTextFiled.bottomLineBorder()
        passwordTextFiled.bottomLineBorder()
    }
 
    /* For remove the observer of the keayboard */
    deinit {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToMain" {
            let nextView = segue.destination as! MainViewController
            let user = self.user
            nextView.currentUser = user
        }
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    /* Function to pick up the view when the keyboard hiding the text fields */
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
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        activeTextField = textField
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    @IBAction func googleButton(_ sender: Any) {
        //GIDSignIn.sharedInstance().delegate = self
        GIDSignIn.sharedInstance().uiDelegate = self
        GIDSignIn.sharedInstance().signIn()
    }
    
    /* The confirm button for login */
    @IBAction func loginButton(_ sender: AnyObject) {
        self.view.endEditing(true)
        
        
        Auth.auth().signIn(withEmail: emailTextFiled.text!, password: passwordTextFiled.text!) { (user, error) in
            
            if (error != nil) {
                let alertError = Service.shared.errorMessage(data: error! as NSError)
                self.present(alertError, animated: true, completion: nil)
            }
            else {
                user?.getIDToken(completion: { (token, error) in
                    if (error != nil) {
                        self.present(Alert().confirmAlert(title: "בעיה בהתחברות", message: ""), animated: true, completion: nil)
                    } else {
                        let parameters = [
                            "idToken": token!
                            ] as [String: AnyObject]
                        
                        
                        Service.shared.connectToServer(path: "logIn", method: .post, params: parameters) {
                            response in
                            if response.succeed {
                                
                                UserDefaults.standard.set(true, forKey: "loggedIn")
                                UserDefaults.standard.synchronize()
                                if let mainView = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                                    mainView.currentUser = User(json: response.data)
                                    self.navigationController?.viewControllers = [mainView]
                                }
                            }
                            else {
                                self.present(Alert().confirmAlert(title: "בעיה בהתחברות", message: ""), animated: true, completion: nil)
                            }
                            
                        }
                    }
                })
            }
            
        }
       
    }
    
    
    @IBAction func forgotPasswordButton(_ sender: UIButton) {
        let alert = UIAlertController(title: "שכחתי סיסמא", message: "", preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "כתובת אימייל"
            textField.textAlignment = .center
        }
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            
            if alert.textFields![0].text! != "" {
                SwiftSpinner.show("אנא המתן")
                Auth.auth().sendPasswordReset(withEmail: alert.textFields![0].text!, completion: { (error) in
                    SwiftSpinner.hide()
                    if error != nil {
                        alert.message = "וודא/י שכתובת האימייל נכונה"
                        self.present(alert, animated: true, completion: nil)
                    } else {
                        alert.dismiss(animated: true, completion: nil)
                        self.present(Alert().confirmAlert(title: "", message: "נשלחה הודעה לכתובת האימייל"), animated: true, completion: nil)
                    }
                })
            }
            
        }))
        alert.addAction(UIAlertAction(title: "ביטול", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
  
    
}

