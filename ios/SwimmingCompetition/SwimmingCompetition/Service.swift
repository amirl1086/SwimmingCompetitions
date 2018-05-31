//
//  Service.swift
//  SwimmingCompetition
//
//  Created by Aviel on 27/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import Foundation
import Firebase
import Alamofire
import SwiftyJSON
import GoogleSignIn

typealias JSON = [String: Any]

class Service {
    
    static let shared = Service()
    
    
    private init() {}
    
    func firebaseAuthCredential(credential: AuthCredential, completion: @escaping (String) -> Void) {
       
        Auth.auth().signIn(with: credential) { (user, error) in
            if error != nil {
                print("error: \(error!)")
            } else {
                user?.getIDToken(completion: { (token, error) in
                    if error != nil {
                        
                    }
                    else {
                        completion(token!)
                    }
                })
            }
        }
        
    }
    
    func connectToServer(path: String, method: HTTPMethod, params: [String: AnyObject], completion: @escaping (responseData) -> Void) {
        
        let alert: UIAlertView = UIAlertView(title: activityMessage(path: path), message: "אנא המתן...", delegate: nil, cancelButtonTitle: nil);
        let loadingIndicator: UIActivityIndicatorView = UIActivityIndicatorView(frame: CGRect(x: 50, y: 10, width: 37, height: 37)) as UIActivityIndicatorView
        loadingIndicator.center = UIViewController().view.center;
        loadingIndicator.hidesWhenStopped = true
        loadingIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
        loadingIndicator.startAnimating();
        alert.setValue(loadingIndicator, forKey: "accessoryView")
        loadingIndicator.startAnimating()
        alert.show();
      
       /* let alert: UIAlertController = UIAlertController(title: activityMessage(path: path), message: "אנא המתן...", preferredStyle: .alert)
        alert.addTextField { (text) in
            text.isHidden = true
        }
        UIApplication.top()?.present(alert, animated: true, completion: nil)
        */
        guard let url = URL(string: "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net/\(path)") else {
                        alert.dismiss(withClickedButtonIndex: -1, animated: true)
            //alert.dismiss(animated: true, completion: nil)
                        return
            
        }
        Alamofire.request(url, method: method, parameters: params).responseJSON { (response) in
        //alert.dismiss(animated: true, completion: nil)
            alert.dismiss(withClickedButtonIndex: -1, animated: true)
            
            switch(response.result) {
            case .success(let json):
                guard let json = json as? JSON else{return}
                do {
                    let getData = try responseData(json: json)
                    completion(getData)
                } catch {}
                
                break;
                
            case .failure(let error):
                    print(error)
                    let alert = self.systemErrorMessage(data: error._code)
                    //UIApplication.top()?.present(alert, animated: true, completion: nil)
                    break;
            }
        }
    }
    
    func activityMessage(path: String) -> String {
        var message = ""
        switch(path) {
        case "logIn":
            message = "מתחבר";
            break;
        case "getCompetitions":
            message = "טוען תחרויות";
            break;
        case "addNewUser":
            message = "שומר משתמש";
            break;
        case "setNewCompetition":
            message = "שומר תחרות";
            break;
        case "initCompetitionForIterations":
            message = "מאתחל תחרות";
            break;
        case "setCompetitionResults":
            message = "מחפש מתחרים";
            break;
        case "joinToCompetition":
            message = "מבצע הרשמה לתחרות";
            break;
        case "cancelRegistration":
            message = "מבטל הרשמה לתחרות";
            break;
        default:
            message = "";
            break;
        }
        return message
    }
    
    func systemErrorMessage(data: Int) -> UIAlertController {
        var title = ""
        var message = ""
        
        switch(data) {
        case -1009:
            title = "בעיה בחיבור הרשת";
            message = "בדוק שהינך מחובר לרשת ואתחל את האפליקציה";
            break;
        default:
            title = "שגיאת מערכת";
            message = "אתחל את האפליקציה ונסה שוב";
        }
        
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
            
        }))
        return alert
    }
    
    func errorMessage(data: NSError) -> UIAlertController {
        var title = ""
        var message = ""
       
        switch(data.code ) {
            case 17011:
                title = "משתמש לא קיים";
                message = "בדוק שכתובת המייל שהזנת נכונה";
                break;
            case 17009:
                title = "סיסמה לא נכונה";
                message = "וודא שהזנת סיסמה נכונה(לפחות 6 תוים)";
                break;
            case 17008:
                title = "כתובת אימייל לא חוקית";
                message = "נא להזין כתובת חוקית";
                break;
            default:
                title = "שגיאה";
                message = "error";
        }

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        return alert
    }
    
    func signOut() {
        //Sign out from firebase user
        do {
            try Auth.auth().signOut()
        } catch {}
        
        //Sign out from google acount
        GIDSignIn.sharedInstance().signOut()
        UserDefaults.standard.set(false, forKey: "loggedIn")
        UserDefaults.standard.synchronize()
    }
}


struct responseData {
    var data:JSON
    let succeed:Bool
    
    init(json: JSON) throws {
        let result = json["data"] as? JSON
        let success = json["success"] as? Bool
        self.data = [:]
        if result != nil {
            self.data = result!
        }
        
        self.succeed = success!
    }
}

extension UIApplication {
    static func top(base: UIViewController? = UIApplication.shared.delegate?.window??.rootViewController) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return top(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController {
            return top(base:tab.selectedViewController)
        }
        if let presented = base?.presentedViewController {
            return top(base:presented)
        }
        return base
    }
}
