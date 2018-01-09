//
//  ViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import Firebase

class MainViewController: UIViewController {

    var user: User!
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
    
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func competitions(_ sender: UIButton) {
    }
    @IBAction func personalScores(_ sender: UIButton) {
    }
    @IBAction func statistics(_ sender: UIButton) {
    }
    @IBAction func realTimeShow(_ sender: UIButton) {
    }
    @IBAction func settings(_ sender: UIButton) {
    }
    @IBAction func files(_ sender: UIButton) {
    }
    
    

}


