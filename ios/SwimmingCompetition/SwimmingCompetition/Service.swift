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
            guard let json = response.result.value as? JSON else{ return}
            do {
                print(json)
                let getData = try responseData(json: json)
                completion(getData)
            } catch {}
            
        }
    }
    
    
    
    
}


struct responseData {
    let data:JSON
    let succeed:Bool
    init(json: JSON) throws {
        
        let result = json["data"] as? JSON// else {
        let success = json["success"] as? Bool
          //  throw throw
        //}
        self.data = result!
        self.succeed = success!
    }
}
