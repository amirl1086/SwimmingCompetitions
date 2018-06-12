//
//  AppDelegate.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit
import CoreData
import Firebase
import GoogleSignIn

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, GIDSignInDelegate {
    
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        
        /* Firebase */
        FirebaseApp.configure()
        
        /* Sign in with google */
        GIDSignIn.sharedInstance().clientID = "197819058733-uua0nj6o4j2cjuuk3dlbt75anavj59cl.apps.googleusercontent.com"
        GIDSignIn.sharedInstance().delegate = self
       
        /* Start navigation controller that checking if the user already sign in */
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.makeKeyAndVisible()
        window?.rootViewController = NavigationController()
        
        return true
    }
    
    /* Function for sign in with google */
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        
        if let error = error {
            print("\(error.localizedDescription)")
        } else {
            let googleUser = user
            
            guard let auth = user.authentication else {return}
            let credential = GoogleAuthProvider.credential(withIDToken: auth.idToken, accessToken: auth.accessToken)
            
            /* Send credential to get the firebase token */
            Service.shared.firebaseAuthCredential(credential: credential) { (getToken) in
                let parameters = [
                    "idToken": getToken
                    ] as [String: AnyObject]
                /* Send request to get the user */
                Service.shared.connectToServer(path: "getUser", method: .post, params: ["currentUserUid": "\(Auth.auth().currentUser!.uid)" as AnyObject]) { (response) in
                    Service.shared.start = false
                    let sb = UIStoryboard(name: "Main", bundle: nil)
                 
                    /* If null - the user not exist. Send request for login that create the user */
                    if response.data["uid"] == nil {
                        Service.shared.connectToServer(path: "logIn", method: .post, params: parameters, completion: { (response) in
                            if response.succeed {
                                print(response.data)
                                if let registerView = sb.instantiateViewController(withIdentifier: "registerTypeId") as? RegisterTypeViewController {
                                    registerView.googleUser = googleUser
                                    if (response.data["token"] as? String) != nil {
                                        registerView.token = response.data["token"] as! String
                                        
                                    }
                                    let root = self.window!.rootViewController as! UINavigationController
                                    root.pushViewController(registerView, animated: true)
                                }
                            }
                        })
                      
                      /* If the user exist and the type is empty - the user need to complete the register process */
                    } else if response.data["type"] as! String == "" {
                        if let registerView = sb.instantiateViewController(withIdentifier: "registerTypeId") as? RegisterTypeViewController {
                            registerView.googleUser = googleUser
                            print("tokkkkkkennnnn")
                            print(response.data["token"] as! String)
                            if (response.data["token"] as? String) != nil {
                                registerView.token = response.data["token"] as! String
                                
                            }
                            let root = self.window!.rootViewController as! UINavigationController
                            root.pushViewController(registerView, animated: true)
                        }
                      /* Get the user's details ang go to the main controller */
                    } else {
                        if let mainView = sb.instantiateViewController(withIdentifier: "mainId") as? MainViewController {
                            UserDefaults.standard.set(true, forKey: "loggedIn")
                            UserDefaults.standard.synchronize()
                            mainView.currentUser = User(json: response.data)
                            let root = self.window!.rootViewController as! UINavigationController
                            root.pushViewController(mainView, animated: true)
                        }
                    }
                }
            }
        }
    }
    
    
    //new
    func sign(_ signIn: GIDSignIn!, didDisconnectWith user: GIDGoogleUser!,
              withError error: Error!) {
        // Perform any operations when the user disconnects from app here.
        // ...
    }
    
    func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
        
        return true
    }
    
    //new
    func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any] = [:]) -> Bool {
        return GIDSignIn.sharedInstance().handle(url,
                                                 sourceApplication: options[UIApplicationOpenURLOptionsKey.sourceApplication] as? String,
                                                 annotation: options[UIApplicationOpenURLOptionsKey.annotation])
    }
    
    

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
        self.saveContext()
    }

    // MARK: - Core Data stack

    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
        */
        let container = NSPersistentContainer(name: "SwimmingCompetition")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                 
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()

    // MARK: - Core Data Saving support

    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }

}

