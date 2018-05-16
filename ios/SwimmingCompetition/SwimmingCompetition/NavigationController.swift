//
//  NavigationController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 13/05/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Firebase

class NavigationController: UINavigationController {
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        let gifImage = UIImage.gif(name: "stll-swimming-logo")
        let imageView = UIImageView(image: gifImage)
        imageView.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        imageView.center = view.center
        view.addSubview(imageView)
        
        if (Auth.auth().currentUser != nil && UserDefaults.standard.bool(forKey: "loggedIn")) {
            UserDefaults.standard.set(true, forKey: "loggedIn")
            UserDefaults.standard.synchronize()
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let loginView = sb.instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                Service.shared.connectToServer(path: "getUser", method: .post, params: ["currentUserUid": "\(Auth.auth().currentUser!.uid)" as AnyObject]) { (response) in
                    if response.succeed && response.data != nil {
                        imageView.removeFromSuperview()
                        loginView.currentUser = User(json: response.data)
                        self.pushViewController(loginView, animated: true)
                    }
                }
                
            }
            
        } else {
            UserDefaults.standard.set(false, forKey: "loggedIn")
            UserDefaults.standard.synchronize()
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let loginView = sb.instantiateViewController(withIdentifier: "loginID") as? LoginViewController {
                imageView.removeFromSuperview()
                self.pushViewController(loginView, animated: true)
            }
            
           
        }
    }
    
    @objc func showLoginController() {
        let loginController = LoginViewController()
        present(loginController, animated: true, completion: {})
    }
    
    @objc func showMainController() {
        let mainController = MainViewController()
        present(mainController, animated: true, completion: {})
    }
}
