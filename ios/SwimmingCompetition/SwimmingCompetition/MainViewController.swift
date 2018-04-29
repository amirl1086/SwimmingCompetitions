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
        
        //self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
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

