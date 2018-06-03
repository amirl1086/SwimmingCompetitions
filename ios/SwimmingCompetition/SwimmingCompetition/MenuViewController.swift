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
    let menuArray = ["ראשי","תחרויות","תוצאות","צפייה בזמן אמת","סטטיסטיקות","תמונות וסרטונים","הילדים שלי","הגדרות","התנתק"]
    
    var currentUser: User!
    
    @IBOutlet var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.delegate = self
        tableView.dataSource = self
        tableView.backgroundColor = UIColor.blue.withAlphaComponent(0.9)
       
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
        cell.label.textColor = .white
        cell.menuIcon.image = UIImage(named: "swimmingIcon.png")
        cell.layer.backgroundColor = UIColor.clear.cgColor
        cell.backgroundColor = UIColor.clear
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.view.removeFromSuperview()
        self.dismiss(animated: true, completion: nil)
        let sb = UIStoryboard(name: "Main", bundle: nil)
        
        switch(self.menuArray[indexPath.row]) {
        case "ראשי":
            if let mainView = sb.instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                mainView.currentUser = self.currentUser
                self.navigationController?.viewControllers = [mainView]
            }
            break
        case "תחרויות":
            if let competitionsView = sb.instantiateViewController(withIdentifier: "competitionsId") as? CompetitionsViewController {
                competitionsView.currentUser = self.currentUser
                self.navigationController?.viewControllers = [competitionsView]
            }
            break
        case "תוצאות":
            /*if let resultsView = sb.instantiateViewController(withIdentifier: "resultsId") as? PersonalResultsViewController {
                //resultsView.currentUser = self.currentUser
                self.navigationController?.viewControllers = [resultsView]
            }*/
            break
        case "צפייה בזמן אמת":
            if let competitionsView = sb.instantiateViewController(withIdentifier: "competitionsId") as? CompetitionsViewController {
                competitionsView.controllerType = "realTime"
                competitionsView.currentUser = self.currentUser
                self.navigationController?.viewControllers = [competitionsView]
            }
            break
        case "הילדים שלי":
            if let myChildrenView = sb.instantiateViewController(withIdentifier: "myChildrenId") as? MyChildrenViewController {
                myChildrenView.currentUser = self.currentUser
                self.navigationController?.viewControllers = [myChildrenView]
            }
            break
        case "הגדרות":
            if let settingsView = sb.instantiateViewController(withIdentifier: "settingsId") as? SettingsViewController {
                settingsView.currentUser = self.currentUser
                self.navigationController?.viewControllers = [settingsView]
            }
            break
        case "התנתק":
            Service.shared.signOut()
            if let loginView = sb.instantiateViewController(withIdentifier: "loginID") as? LoginViewController {
                self.navigationController?.viewControllers = [loginView]
            }
            break
        default:
            break
        }
        
    }

}
