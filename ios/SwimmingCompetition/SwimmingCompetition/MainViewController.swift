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
import GoogleSignIn
class MainViewController: UIViewController {

    var currentUser: User!
    var menu_vc: MenuViewController!
    @IBOutlet weak var mainLabel: UILabel!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.setHidesBackButton(true, animated: true)
        mainLabel.text = "שלום \(currentUser.firstName)"
        
        initMenuBar()
       
        //Remove back button from main controller
        self.navigationItem.leftBarButtonItem = nil
        
        //Set background
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")
        self.view.insertSubview(self.backgroundView, at: 0)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }
    
    /* create the side menu bar */
    func initMenuBar() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.menu_vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "menuId") as! MenuViewController
        menu_vc.currentUser = self.currentUser
        self.menu_vc.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
    }
    
    /* show theside menu bar */
    @objc func showMenu() {
        
        let rightButton = UIBarButtonItem(image: UIImage(named: "cancel.png"), style: .plain, target: self, action: #selector(cancelMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.addChildViewController(self.menu_vc)
        self.menu_vc.view.frame = self.view.frame
        self.view.addSubview(self.menu_vc.view)
        self.menu_vc.didMove(toParentViewController: self)
    }
    
    /* cancel side menu bar */
    @objc func cancelMenu() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        
        self.menu_vc.view.removeFromSuperview()
    }
    
    /* go to results view */
    @IBAction func scoreButton(_ sender: UIButton) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let competitionsView = sb.instantiateViewController(withIdentifier: "competitionsId") as? CompetitionsViewController {
            competitionsView.currentUser = self.currentUser
            competitionsView.controllerType = "results"
            self.navigationController?.viewControllers = [competitionsView]
        }
    }
    
    /* go to competitions view */
    @IBAction func competitionsButton(_ sender: Any) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let competitionsView = sb.instantiateViewController(withIdentifier: "competitionsId") as? CompetitionsViewController {
            competitionsView.currentUser = self.currentUser
            self.navigationController?.viewControllers = [competitionsView]
        }
    }
    
    /* go to settings view */
    @IBAction func settingsButton(_ sender: Any) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let settingsView = sb.instantiateViewController(withIdentifier: "settingsId") as? SettingsViewController {
            settingsView.currentUser = self.currentUser
            self.navigationController?.viewControllers = [settingsView]
        }
    }
    
    /* go to real time competition view */
    @IBAction func realTimeButton(_ sender: Any) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let resultsView = sb.instantiateViewController(withIdentifier: "resultsId") as? PersonalResultsViewController {
            resultsView.currentUser = self.currentUser
            resultsView.controllerType = "realTime"
            self.navigationController?.viewControllers = [resultsView]
        }
    }
    
}
