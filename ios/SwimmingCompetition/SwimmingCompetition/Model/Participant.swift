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
    
    init(json: JSON, id: String) {
        let firstName = json["firstName"] as? String
            let lastName = json["lastName"] as? String
            let gender = json["gender"] as? String
            let birthDate = json["birthDate"] as? String
        var competed:Bool = false
        if(json["competed"] as? String == "true" || json["competed"] as? String == "1" || json["competed"] as? Bool == true) {
            competed = true
        }
        
       
            let score = json["score"] as? String
            let uid = id as? String
            
        
        self.firstName = firstName!
        self.lastName = lastName!
        self.gender = gender!
        self.birthDate = birthDate!
        self.competed = competed
        self.score = score!
        self.uid = uid!
        self.rank = 0
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
