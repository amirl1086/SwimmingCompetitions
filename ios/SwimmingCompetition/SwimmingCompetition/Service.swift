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
import SwiftSpinner


typealias JSON = [String: Any]

class Service {
    
    static let shared = Service()
    var start = false
    
    private init() {}
    
    
    
    func connectToServer(path: String, method: HTTPMethod, params: [String: AnyObject], completion: @escaping (responseData) -> Void) {
        
        if !self.start {
            SwiftSpinner.show(activityMessage(path: path))
        }
        self.start = false
        
        guard let url = URL(string: "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net/\(path)") else {
            SwiftSpinner.hide()
            return
        }
        Alamofire.request(url, method: method, parameters: params).responseJSON { (response) in
            
            switch(response.result) {
            case .success(let json):
                guard let json = json as? JSON else{return}
                do {
                    let getData = try responseData(json: json)
                    completion(getData)
                } catch {}
                SwiftSpinner.hide()
                break;
                
            case .failure(let error):
                    SwiftSpinner.show(duration: 4.0, title: self.systemErrorMessage(data: error._code), animated: false)
                    
                    break;
            }
        }
    }

    /* activity messages for the spinner activity while connecting to the server */
    func activityMessage(path: String) -> String {
        var message = ""
        switch(path) {
        case "logIn":
            message = "מתחבר";
            break;
        case "getUser":
            message = "מתחבר";
            break;
        case "updateFirebaseUser":
            message = "שומר נתונים";
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
            message = "שומר נתונים";
            break;
        case "joinToCompetition":
            message = "מבצע הרשמה לתחרות";
            break;
        case "cancelRegistration":
            message = "מבטל הרשמה לתחרות";
            break;
        default:
            message = "אנא המתן";
            break;
        }
        return message
    }
    
    /* get system errors */
    func systemErrorMessage(data: Int) -> String {
        var message = ""
        
        switch(data) {
        case -1009:
            message = "בעיה בחיבור הרשת";
            break;
        default:
            message = "שגיאת מערכת";
            break;
        }
        
        return message
    }
    
    /* get firebase error */
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
                message = "";
        }

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        return alert
    }
    
    /* get the firebase user token */
    func firebaseAuthCredential(credential: AuthCredential, completion: @escaping (String) -> Void) {
        
        Auth.auth().signIn(with: credential) { (user, error) in
            if error != nil {
                //print("error: \(error!)")
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

    /* sign out from firebase user */
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

/* struct for response data - data as json, succeed as bool */
struct responseData {
    let jsonAll:JSON
    var data:JSON
    let succeed:Bool
    
    init(json: JSON) throws {
        if (json["data"] as? NSMutableArray) != nil {
            //print(json["data"]!)
        }
        let jsonData = json
        let result = json["data"] as? JSON
        let success = json["success"] as? Bool
        self.data = [:]
        if result != nil {
            self.data = result!
        }
        self.jsonAll = jsonData
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
