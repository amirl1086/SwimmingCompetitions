//
//  LoginViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import Firebase

class LoginViewController: UIViewController {
    
    //The input email and password for login
    @IBOutlet weak var emailTextFiled: UITextField!
    @IBOutlet weak var passwordTextFiled: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func loginButton(_ sender: AnyObject) {
        //Check the validation of the authentication and sign in to the main page
        Auth.auth().signIn(withEmail: emailTextFiled.text!, password: passwordTextFiled.text!, completion: {(user, error) in
            //If the login failed
            if error != nil {
                print(error!)
            }
            //If the login succeed
            else {
                self.performSegue(withIdentifier: "goToMain", sender: self)
            }
        })

    }
    
}
