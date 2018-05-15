//
//  MainViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 18/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit

class RegisterTypeViewController: UIViewController {
    
    //user type
    var type = String()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //The background image
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
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
