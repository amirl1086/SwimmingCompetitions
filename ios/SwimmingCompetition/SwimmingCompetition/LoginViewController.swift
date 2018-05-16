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
    
    var mainView = MainViewController()
    var user: User!
    
    override func viewDidLoad() {
  
        super.viewDidLoad()
        
        emailTextFiled.delegate = self
        passwordTextFiled.delegate = self
        activeTextField = emailTextFiled
     
        //Set the logo image
        self.logo.image = UIImage(named: "logo.png")
        
        //Set the background
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "waterpool_bottom.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
        
        GIDSignIn.sharedInstance().signOut()
        
        //Set the sign in with google method and create the button
        GIDSignIn.sharedInstance().uiDelegate = self
        let signInWithGooogleButton = GIDSignInButton(frame: CGRect(origin: CGPoint(x: 0, y: 0), size: CGSize(width: 100, height: 50)))
        signInWithGooogleButton.center = view.center
        view.addSubview(signInWithGooogleButton)
        
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
            print(keyboardRect.height)
            if ((self.view.frame.height - keyboardRect.height) <= (activeTextField.frame.origin.y+activeTextField.frame.height)) {
                self.view.frame.origin.y = -keyboardRect.height
                
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
   
    @IBAction func loginButton(_ sender: AnyObject) {
       
        self.view.endEditing(true)
        var alert: UIAlertView = UIAlertView(title: "מתחבר", message: "אנא המתן...", delegate: nil, cancelButtonTitle: nil);
        
        
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
