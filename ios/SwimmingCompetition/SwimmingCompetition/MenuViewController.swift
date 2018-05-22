//
//  MenuViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 22/05/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Firebase
import GoogleSignIn

class MenuViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    /* Labels array for the menu bar */
    let menuArray = ["ראשי","תחרויות","תוצאות","צפייה בזמן אמת","סטטיסטיקות","תמונות וסרטונים","הגדרות","התנתק"]
    
    var currentUser: User!
    
    @IBOutlet var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.delegate = self
        tableView.dataSource = self
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menuArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "menuCell", for: indexPath) as! MenuTableViewCell
        cell.label.text = menuArray[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.view.removeFromSuperview()
        if indexPath.row == 1 {
            if let next = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "competitionsId") as? CompetitionsViewController {
                next.currentUser = self.currentUser
                self.navigationController?.pushViewController(next, animated: true)
            }
        }
        if indexPath.row == (self.menuArray.count-1) {
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
    }

}
