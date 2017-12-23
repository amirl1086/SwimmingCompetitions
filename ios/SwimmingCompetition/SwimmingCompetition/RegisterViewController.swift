//
//  RegisterViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import Firebase

class RegisterViewController: UIViewController {
    
    //the inputs for register
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
       
        navigationItem.backBarButtonItem = UIBarButtonItem(title: "dddd", style: .plain, target: nil, action: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //Button to go back to the login page
    @IBAction func goToLogin(_ sender: AnyObject) {
        self.performSegue(withIdentifier: "goToLogin", sender: self)
    }
    
    //Button to register and create the user
    @IBAction func confirmRegister(_ sender: AnyObject) {
        Auth.auth().createUser(withEmail: emailTextField.text!, password: passwordTextField.text!, completion: {(user, error) in
            //If there is an error
            if error != nil {
                print(error!)
            }
            //If succeed - go to main page
            else {
                self.performSegue(withIdentifier: "goToMain", sender: self)
                
            }
            
        })
    }
    
}
