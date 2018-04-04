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
        guard let url = URL(string: "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net/\(path)") else { return }
        Alamofire.request(url, method: method, parameters: params).responseJSON { (response) in
            print(response)
            guard let json = response.result.value as? JSON else{ return}
            do {
                print(json)
                let getData = try responseData(json: json)
                completion(getData)
            } catch {}
            
        }
    }
    
    func errorMessage(data: JSON) -> UIAlertController {
        var title = ""
        var message = ""
       
        switch(data["code"] as! String) {
            case "auth/user-not-found":
                title = "משתמש לא קיים";
                message = "בדוק שכתובת המייל שהזנת נכונה";
                break;
            case "auth/wrong-password":
                title = "סיסמה לא נכונה";
                message = "וודא שהזנת סיסמה נכונה(לפחות 6 תוים)";
                break;
            default:
                title = "שגיאה";
                message = "שגיאה";
        }
        print("eror")
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        return alert
    }
}


struct responseData {
    let data:JSON
    let succeed:Bool
    
    init(json: JSON) throws {
        let result = json["data"] as? JSON
        let success = json["success"] as? Bool
        
        self.data = result!
        self.succeed = success!
    }
}
