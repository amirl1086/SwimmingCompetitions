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
        
        Service.shared.start = true
        
        view.backgroundColor = .white
        let gifImage = UIImage.gif(name: "stll-swimming-logo")
        let imageView = UIImageView(image: gifImage)
        imageView.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        imageView.center = view.center
        view.addSubview(imageView)
        
        /* if the user already logged in - go to main view. else - go to login view */
        if (Auth.auth().currentUser != nil && UserDefaults.standard.bool(forKey: "loggedIn")) {
            
            UserDefaults.standard.set(true, forKey: "loggedIn")
            UserDefaults.standard.synchronize()
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let mainView = sb.instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                /* get the user by uid and go to main view */
                Service.shared.connectToServer(path: "getUser", method: .post, params: ["currentUserUid": "\(Auth.auth().currentUser!.uid)" as AnyObject]) { (response) in
                    if response.succeed {
                        imageView.removeFromSuperview()
                        mainView.currentUser = User(json: response.data)
                        self.pushViewController(mainView, animated: true)
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
