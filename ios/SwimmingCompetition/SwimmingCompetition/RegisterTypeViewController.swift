//
//  MainViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 18/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import GoogleSignIn

class RegisterTypeViewController: UIViewController {
    
    //user type
    var type = String()
    var googleUser: GIDGoogleUser! = nil
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //The background image
        backgroundView = UIImageView(frame: self.view.bounds)
        backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(backgroundView, at: 0)
    }
    
    override func viewDidLayoutSubviews() {
        backgroundView.frame = self.view.bounds
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //pass the type of user to the next controller
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToRegister" {
            let passData = segue.destination as! RegisterViewController
            passData.userType = type
            if (googleUser != nil) {
                passData.googleUser = self.googleUser
                passData.isGoogleRegister = true
            }
        }
        
        
    }
    
    //===== Buttons for select register type =====//
    @IBAction func userButton(_ sender: Any) {
        type = "student"
        self.performSegue(withIdentifier: "goToRegister", sender: self)
    }
    
    @IBAction func parentButton(_ sender: Any) {
        type = "parent"
        self.performSegue(withIdentifier: "goToRegister", sender: self)
    }
    //=============================================//
   
    
}
