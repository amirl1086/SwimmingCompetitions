//
//  Participant.swift
//  SwimmingCompetition
//
//  Created by Aviel on 20/02/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation

class Participant {
    
    var firstName:String
    var lastName:String
    var gender:String
    var birthDate: String
    var competed:Bool
    var score:String
    var uid: String
    var rank: Int
    var timeStamp: String
    
    init(json: JSON, id: String) {
        let firstName = json["firstName"] as? String
        let lastName = json["lastName"] as? String
        let gender = json["gender"] as? String
        let birthDate = json["birthDate"] as? String
        var competed:Bool = false
        if(json["competed"] as? String == "true" || json["competed"] as? String == "1" || json["competed"] as? Bool == true) {
            competed = true
        }
        let timeStamp = json["timeStamp"] as? String
        let score = json["score"] as? String
        let uid = json["uid"] as? String != nil ? json["uid"] as! String : id
            
        
        self.firstName = firstName != nil ? firstName! : ""
        self.lastName = lastName != nil ? lastName! : ""
        self.gender = gender != nil ? gender! : ""
        self.birthDate = birthDate != nil ? birthDate! : ""
        self.competed = competed
        self.score = score != nil ? score! : ""
        self.uid = uid
        self.rank = 0
        self.timeStamp = timeStamp != nil ? timeStamp! : ""
    }
    
    func setCompeted(competed:Bool) {
        self.competed = competed
    }
    
    func getCompeted() -> Bool {
        return self.competed
    }
    
    func setRank(rank: Int) {
        self.rank = rank
    }
}
