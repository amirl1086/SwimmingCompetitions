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

class LoginViewController: UIViewController, UITextFieldDelegate {
    
    @IBOutlet weak var logo: UIImageView!
    //The input email and password for login
    @IBOutlet weak var emailTextFiled: UITextField!
    @IBOutlet weak var passwordTextFiled: UITextField!
    @IBOutlet weak var errorLabel: UILabel!
    @IBOutlet weak var spinner: UIActivityIndicatorView!
    var mainView = MainViewController()
    var user: User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.logo.image = UIImage(named: "ios2.png")
        emailTextFiled.delegate = self
        passwordTextFiled.delegate = self
        errorLabel.isHidden = true
        
        activateSpinner(isActivate: false)
        
        // Do any additional setup after loading the view, typically from a nib.
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        
        
        
    }
  
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToMain" {
            let nextView = segue.destination as! MainViewController
            let user = self.user
            nextView.user = user
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
   
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == emailTextFiled {
            passwordTextFiled.becomeFirstResponder()
        } else if textField == passwordTextFiled {
            textField.resignFirstResponder()
            loginButton(self)
        }
        return true
    }
    
    @IBAction func emailTextFieldBeginEditing(_ sender: Any) {
        self.errorLabel.isHidden = true
    }
   
    @IBAction func loginButton(_ sender: AnyObject) {
        self.errorLabel.isHidden = true
        activateSpinner(isActivate: true)
        let parameters = [
            "email": emailTextFiled.text!,
            "password": passwordTextFiled.text!
        ]
        

        Service.shared.connectToServer(path: "logIn", method: .post, params: parameters) {
            response in
            
            if response.succeed {
                self.activateSpinner(isActivate: false)
                self.user = User(json: response.data)
                self.performSegue(withIdentifier: "goToMain", sender: self)
            }
            else {
                self.spinner.isHidden = true
                let message = response.data["message"] as? String
                self.errorLabel.text = message
                self.errorLabel.isHidden = false
            }
            
        }
    }
    
    func activateSpinner(isActivate: Bool) {
        if isActivate {
            self.spinner.isHidden = false
            spinner.startAnimating()
        }
        else {
            self.spinner.isHidden = true
            self.spinner.stopAnimating()
        }
        
    }
    
  
    
}
