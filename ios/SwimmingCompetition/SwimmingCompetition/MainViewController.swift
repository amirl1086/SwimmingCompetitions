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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Remove back button from main controller
        self.navigationItem.leftBarButtonItem = nil
        
        //Add sign out button
        let addButton = UIBarButtonItem(title: "התנתק", style: .done, target: self, action: #selector(signOut))
        self.navigationItem.leftBarButtonItem = addButton
        
        //Set background
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")
        self.view.insertSubview(imageView, at: 0)
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        //Send data to the next controller
        let nextView = segue.destination as! CompetitionsViewController
        let user = self.currentUser
        nextView.currenUser = user
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
            competitionsView.currenUser = self.currentUser
            self.navigationController?.pushViewController(competitionsView, animated: true)
        }
    }
    @IBAction func settings(_ sender: UIButton) {
    }
    @IBAction func files(_ sender: UIButton) {
        
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
        if let mainView = sb.instantiateViewController(withIdentifier: "loginID") as? LoginViewController {
            self.navigationController?.viewControllers = [mainView]
        }
    }
    //====================================//
  
}

