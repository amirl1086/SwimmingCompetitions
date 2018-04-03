//
//  ViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import Firebase
import SwiftyJSON
class MainViewController: UIViewController {

    var user: User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        let param = ["uid":user.uid] as [String:AnyObject]
        Service.shared.connectToServer(path: "getPersonalResults", method: .post, params: param) { (response) in
            print(response)
        }
        
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let nextView = segue.destination as! CompetitionsViewController
        let user = self.user
        nextView.user = user
    }
    
    //===== The buttons of the menu =====//
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
    //====================================//
  
}

