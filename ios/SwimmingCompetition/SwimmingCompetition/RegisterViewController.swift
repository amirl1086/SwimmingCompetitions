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
    
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var birthDate: UITextField!
    @IBOutlet weak var gender: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var passwordConfirmation: UITextField!
    @IBOutlet weak var productNumber: UITextField!
    var userType = String()
    @IBOutlet weak var confirmButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        
        changeRegisterView()
        print(userType)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func changeRegisterView() {
        if userType == "parent" {
            
            (firstName.frame.origin, email.frame.origin) = (email.frame.origin, firstName.frame.origin)
            (lastName.frame.origin, password.frame.origin) = (password.frame.origin, lastName.frame.origin)
            (birthDate.frame.origin, passwordConfirmation.frame.origin) = (passwordConfirmation.frame.origin, birthDate.frame.origin)
            (gender.frame.origin, productNumber.frame.origin) = (productNumber.frame.origin, gender.frame.origin)
            
            firstName.isHidden = true
            lastName.isHidden = true
            birthDate.isHidden = true
            gender.isHidden = true
            confirmButton.frame.origin = CGPoint(x: 150, y: 300)
            
        }
        
    }
    
    //Button to register and create the user
    @IBAction func confirmRegister(_ sender: AnyObject) {
       
        let parameters = [
            "firstName": firstName.text!,
            "lastName": lastName.text!,
            "birthDate": birthDate.text!,
            "gender": gender.text!,
            "email": email.text!,
            "password": password.text!,
            "passwordConfirmation": passwordConfirmation.text!,
            "type": userType
        ]
        
        Service.shared.connectToServer(path: "addNewUser", method: .post, params: parameters) { (response) in
            print(response)
        }
        
       /* Service().connectToServer(path: "addNewUser", method: .post, params: parameters) { (response, success) in
            print(response)
        }*/
       
    }
    
}
