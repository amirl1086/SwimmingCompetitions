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
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initMenuBar()
        
        //Remove back button from main controller
        self.navigationItem.leftBarButtonItem = nil
        
        //Add sign out button
        let addButton = UIBarButtonItem(title: "התנתק", style: .done, target: self, action: #selector(signOut))
        self.navigationItem.leftBarButtonItem = addButton
        
        //Set background
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")
        self.view.insertSubview(self.backgroundView, at: 0)
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "abstract_swimming_pool.jpg")!)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        //Send data to the next controller
        if segue.identifier == "goToCompetitions" {
            let nextView = segue.destination as! CompetitionsViewController
            let user = self.currentUser
            nextView.currentUser = user
        }
        if segue.identifier == "goToSettings" {
            let nextView = segue.destination as! SettingsViewController
            let user = self.currentUser
            nextView.currentUser = user
        }
        
    }
    
    func initMenuBar() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.menu_vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "menuId") as! MenuViewController
        menu_vc.currentUser = self.currentUser
        self.menu_vc.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
    }
    
    @objc func showMenu() {
        
        let rightButton = UIBarButtonItem(image: UIImage(named: "cancel.png"), style: .plain, target: self, action: #selector(cancelMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.addChildViewController(self.menu_vc)
        self.menu_vc.view.frame = self.view.frame
        self.view.addSubview(self.menu_vc.view)
        self.menu_vc.didMove(toParentViewController: self)
    }
    
    @objc func cancelMenu() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        
        self.menu_vc.view.removeFromSuperview()
    }
    
    
    
    
    //===== The buttons of the menu =====//
    @IBAction func competitions(_ sender: UIButton) {
    }
    @IBAction func personalScores(_ sender: UIButton) {
        Service.shared.connectToServer(path: "getPersonalResults", method: .post, params: ["uid": currentUser.uid as AnyObject]) { (response) in
            print("**********personal result***********")
            print(response)
            print("************************************")
        }
    }
    @IBAction func statistics(_ sender: UIButton) {
    }
    @IBAction func realTimeShow(_ sender: UIButton) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let competitionsView = sb.instantiateViewController(withIdentifier: "competitionsId") as? CompetitionsViewController {
            competitionsView.controllerType = "realTime"
            competitionsView.currentUser = self.currentUser
            self.navigationController?.pushViewController(competitionsView, animated: true)
        }
    }
    @IBAction func settings(_ sender: UIButton) {
    }
    @IBAction func files(_ sender: UIButton) {
        Service.shared.connectToServer(path: "addChildToParent", method: .post, params: ["email": "ref@mad.com" as AnyObject, "birthDate": "13/4/2006" as AnyObject]) { (response) in
            print(response)
        }
    }
    
    //Function for sign out
    @objc func signOut() {
        //Sign out from firebase user
        do {
            try Auth.auth().signOut()
        } catch {}
        
        //Sign out from google acount
        GIDSignIn.sharedInstance().signOut()
        UserDefaults.standard.set(false, forKey: "loggedIn")
        UserDefaults.standard.synchronize()
        
        //Go back to login controller
        self.dismiss(animated: true, completion: nil)
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let loginView = sb.instantiateViewController(withIdentifier: "loginID") as? LoginViewController {
            self.navigationController?.viewControllers = [loginView]
        }
    }
    //====================================//
  
}
