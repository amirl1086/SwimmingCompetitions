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
        // Do any additional setup after loading the view, typically from a nib.
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //pass the type to the next controller
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let passData = segue.destination as! RegisterViewController
        passData.userType = type
        
    }
    
    //===== Buttons for select register type =====//
    @IBAction func userButton(_ sender: Any) {
        type = "user"
        self.performSegue(withIdentifier: "goToRegister", sender: self)
    }
    
    @IBAction func parentButton(_ sender: Any) {
        type = "parent"
        self.performSegue(withIdentifier: "goToRegister", sender: self)
    }
    //=============================================//
   
    
}
