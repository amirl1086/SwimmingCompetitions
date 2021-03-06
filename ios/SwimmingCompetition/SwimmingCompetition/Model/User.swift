//
//  User.swift
//  SwimmingCompetition
//
//  Created by Aviel on 30/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import Foundation

struct User {
    
    var firstName:String
    var lastName:String
    var birthDate:String
    var gender: String
    var email:String
    var type:String
    var phoneNumber:String
    var uid:String
    var token:String
    var children = [User]()
    
    init(json: JSON) {
        let firstName = json["firstName"] as? String
        let lastName = json["lastName"] as? String
        let birthDate = json["birthDate"] as? String
        let gender = json["gender"] as? String
        let email = json["email"] as? String
        let type = json["type"] as? String
        let phoneNumber = json["phoneNumber"] as? String
        let uid = json["uid"] as? String
        let token = json["token"] as? String
        let children = json["children"] as? JSON
        
        self.firstName = firstName != nil ? firstName! : ""
        self.lastName = lastName != nil ? lastName! : ""
        self.birthDate = birthDate != nil ? birthDate! : ""
        self.gender = gender != nil ? gender! : ""
        self.email = email != nil ? email! : ""
        self.type = type != nil ? type! : ""
        self.phoneNumber = phoneNumber != nil ? phoneNumber! : ""
        self.uid = uid != nil ? uid! : ""
        self.token = token != nil ? token! : ""
        self.children = []
        
        
        
    }
  
}
