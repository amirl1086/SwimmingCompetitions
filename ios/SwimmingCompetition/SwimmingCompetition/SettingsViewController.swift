//
//  SettingsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 21/05/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Firebase

class SettingsViewController: UIViewController {
    
    var menu_vc: MenuViewController!
    var currentUser: User!

    override func viewDidLoad() {
        super.viewDidLoad()
        initMenuBar()
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @IBAction func editPersonalDetails(_ sender: Any) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let registerView = sb.instantiateViewController(withIdentifier: "registerId") as? RegisterViewController {
            registerView.currentUser = self.currentUser
            self.navigationController?.pushViewController(registerView, animated: true)
        }
    }
    
    @IBAction func changePassword(_ sender: Any) {
        let alert = UIAlertController(title: "שינוי סיסמא", message: "", preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "סיסמא נוכחית"
        }
        alert.addTextField { (textField) in
            textField.placeholder = "סיסמא חדשה (לפחות 6 תוים)"
        }
        alert.addTextField { (textField) in
            textField.placeholder = "אימות סיסמא חדשה"
        }
        alert.addAction(UIAlertAction(title: "ביטול", style: .cancel, handler: { (action) in
            alert.dismiss(animated: false, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            if alert.textFields![1].text != alert.textFields![2].text {
                alert.message = "הסיסמאות החדשות אינן תואמות"
                self.present(alert, animated: true, completion: nil)
            } else {
                Auth.auth().signIn(withEmail: self.currentUser.email, password: alert.textFields![0].text!, completion: { (user, error) in
                    if error != nil {
                        alert.message = "סיסמא נוכחית אינה נכונה"
                        self.present(alert, animated: true, completion: nil)
                    } else {
                        Auth.auth().currentUser?.updatePassword(to: alert.textFields![1].text!, completion: { (error) in
                            print(error!)
                        })
                    }
                })
            }
        }))
        self.present(alert, animated: true, completion: nil)
        //Auth.auth().currentUser?.updatePassword(to: <#T##String#>, completion: <#T##UserProfileChangeCallback?##UserProfileChangeCallback?##(Error?) -> Void#>)
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
    
}
