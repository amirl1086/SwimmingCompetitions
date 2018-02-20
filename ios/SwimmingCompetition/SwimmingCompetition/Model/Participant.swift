//
//  Participant.swift
//  SwimmingCompetition
//
//  Created by Aviel on 20/02/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation

struct Participant {
    
    let firstName:String
    let lastName:String
    let gender:String
    let birthDate: String
    let competed:String
    let score:String
    let uid: String
    
    init?(json: JSON, id: String) {
        guard let firstName = json["firstName"] as? String,
            let lastName = json["lastName"] as? String,
            let gender = json["gender"] as? String,
            let birthDate = json["birthDate"] as? String,
            let competed = json["competed"] as? String,
            let score = json["score"] as? String,
            let uid = id as? String
            else {return nil}
        
        self.firstName = firstName
        self.lastName = lastName
        self.gender = gender
        self.birthDate = birthDate
        self.competed = competed
        self.score = score
        self.uid = uid
    }
}
