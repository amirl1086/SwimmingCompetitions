//
//  MainViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 18/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func logoutButton(_ sender: Any) {
        self.performSegue(withIdentifier: "goToLogin", sender: self)
    }
    
}
