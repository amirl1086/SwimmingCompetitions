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

typealias JSON = [String: Any]

class Service {
    
    static let shared = Service()
    
    private init() {}
    
    
    func connectToServer(path: String, method: HTTPMethod, params: [String: AnyObject], completion: @escaping (responseData) -> Void) {
        
        var alert: UIAlertView = UIAlertView(title: activityMessage(path: path), message: "אנא המתן...", delegate: nil, cancelButtonTitle: nil);
        let loadingIndicator: UIActivityIndicatorView = UIActivityIndicatorView(frame: CGRect(x: 50, y: 10, width: 37, height: 37)) as UIActivityIndicatorView
        loadingIndicator.center = UIViewController().view.center;
        loadingIndicator.hidesWhenStopped = true
        loadingIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
        loadingIndicator.startAnimating();
        alert.setValue(loadingIndicator, forKey: "accessoryView")
        loadingIndicator.startAnimating()
        alert.show();
        
        
        guard let url = URL(string: "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net/\(path)") else {
                        alert.dismiss(withClickedButtonIndex: -1, animated: true)
                        return
            
        }
        Alamofire.request(url, method: method, parameters: params).responseJSON { (response) in
            
            alert.dismiss(withClickedButtonIndex: -1, animated: true)
            switch(response.result) {
            case .success(let json):
                    print(json)
                    break;
            case .failure(let error):
                    print(error)
                    
                    if error._code == -1009 {
                        
                    }
                    if error._code == NSURLErrorTimedOut {
                        return
                }
            }
            guard let json = response.result.value as? JSON else{return}
            do {
                //print(json)
                let getData = try responseData(json: json)
                completion(getData)
            } catch {}
            
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
