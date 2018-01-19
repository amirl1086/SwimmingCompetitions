//
//  User.swift
//  SwimmingCompetition
//
//  Created by Aviel on 30/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import Foundation

struct User {
    
    let firstName:String
    let lastName:String
    let birthDate:String
    let gender: String
    let email:String
    let type:String
    let uid:String
    
    init?(json: JSON) {
        guard let firstName = json["firstName"] as? String,
        let lastName = json["lastName"] as? String,
        let birthDate = json["birthDate"] as? String,
        let gender = json["gender"] as? String,
        let email = json["email"] as? String,
        let type = json["type"] as? String,
        let uid = json["uid"] as? String
        else {return nil}
        
        self.firstName = firstName 
        self.lastName = lastName
        self.birthDate = birthDate
        self.gender = gender
        self.email = email
        self.type = type
        self.uid = uid
        
    }
    
    /*var firstName: String = ""
    var lastName: String = ""
    var birthDate: String = ""
    var gender: String = ""
    var email: String = ""
    var type: String = ""
    
    init(firstName: String, lastName: String, birthDate: String, gender: String, email: String, type: String) {
        self.firstName = firstName
        self.lastName = lastName
        self.birthDate = birthDate
        self.gender = gender
        self.email = email
        self.type = type
    }*/
}
