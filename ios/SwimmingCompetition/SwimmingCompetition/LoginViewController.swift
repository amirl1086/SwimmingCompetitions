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

class LoginViewController: UIViewController, UITextFieldDelegate, GIDSignInDelegate, GIDSignInUIDelegate {
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
    
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
    
    override func viewDidLoad() {
  
        super.viewDidLoad()
        
        emailTextFiled.delegate = self
        passwordTextFiled.delegate = self
        
       
        activeTextField = emailTextFiled
     
        /* Set the logo image */
        self.logo.image = UIImage(named: "logo.png")
        
        /* Set the background */
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "waterpool_bottom.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        
        /* Observer for keyboard */
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
        
        /* Sign out the google account if the login controller appear */
        GIDSignIn.sharedInstance().signOut()
        
        /* Set the sign in with google method and create the button */
        GIDSignIn.sharedInstance().uiDelegate = self
        signInWithGooogleButton = GIDSignInButton(frame: CGRect(origin: CGPoint(x: 0, y: self.loginButton.frame.origin.y + self.loginButton.frame.height + 20), size: CGSize(width: 100, height: 50)))
        scrollView.addSubview(signInWithGooogleButton)
        scrollView.isScrollEnabled = true
        scrollView.isUserInteractionEnabled = true
        
        
        //view.addSubview(signInWithGooogleButton)
        
    }
    
    
    
    override func viewDidLayoutSubviews() {

        signInWithGooogleButton.center.x = self.view.center.x
        emailTextFiled.bottomLineBorder()
       /* let border = CALayer()
        let lineWidth = CGFloat(0.8)
        border.borderColor = UIColor.black.cgColor
        border.frame = CGRect(x: 0, y: emailTextFiled.frame.height - lineWidth, width:  emailTextFiled.frame.width, height: emailTextFiled.frame.height)
        border.borderWidth = lineWidth
        emailTextFiled.layer.addSublayer(border)
        emailTextFiled.layer.masksToBounds = true*/
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
        /*guard let keyboardRect = (notification.userInfo![UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue else {
            return
        }
        if notification.name == Notification.Name.UIKeyboardWillShow ||
            notification.name == Notification.Name.UIKeyboardWillChangeFrame {
            print(keyboardRect.height)
            if ((self.scrollView.frame.height - keyboardRect.height) <= (activeTextField.frame.origin.y+activeTextField.frame.height)) {
                self.scrollView.frame.origin.y = -keyboardRect.height
                
            } else {
                scrollView.frame.origin.y = 0
            }
            
        } else {
            scrollView.frame.origin.y = 0
        }
        */
        
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        activeTextField = textField
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
   
    /* The confirm button for login */
    @IBAction func loginButton(_ sender: AnyObject) {
        self.view.endEditing(true)
        
        let alert: UIAlertView = UIAlertView(title: "מתחבר", message: "אנא המתן...", delegate: nil, cancelButtonTitle: nil);
        let loadingIndicator: UIActivityIndicatorView = UIActivityIndicatorView(frame: CGRect(x: 50, y: 10, width: 37, height: 37)) as UIActivityIndicatorView
        loadingIndicator.center = self.view.center;
        loadingIndicator.hidesWhenStopped = true
        loadingIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
        loadingIndicator.startAnimating();
        alert.setValue(loadingIndicator, forKey: "accessoryView")
        loadingIndicator.startAnimating()
        alert.show();
        
        Auth.auth().signIn(withEmail: emailTextFiled.text!, password: passwordTextFiled.text!) { (user, error) in
            
            alert.dismiss(withClickedButtonIndex: -1, animated: true)
            if (error != nil) {
                
                let alertError = Service.shared.errorMessage(data: error! as NSError)
                self.present(alertError, animated: true, completion: nil)
            }
            else {
                
                user?.getIDToken(completion: { (token, error) in
                    
                    if (error != nil) {
                        
                    }
                    
                    let parameters = [
                        "idToken": token!
                        ] as [String: AnyObject]
                    
                    
                    Service.shared.connectToServer(path: "logIn", method: .post, params: parameters) {
                        response in
                        //alert.dismiss(withClickedButtonIndex: -1, animated: true)
                        if response.succeed {
                            
                            self.user = User(json: response.data)
                            UserDefaults.standard.set(true, forKey: "loggedIn")
                            UserDefaults.standard.synchronize()
                            self.performSegue(withIdentifier: "goToMain", sender: self)
                        }
                        else {
                            
                        }
                        
                    }
                })
            }
            
        }
       
    }
    
    
    
  
    
}

