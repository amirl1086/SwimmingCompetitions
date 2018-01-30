//
//  Competition.swift
//  SwimmingCompetition
//
//  Created by Aviel on 18/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation

struct Competition {
    
    let name:String
    let activityDate:String
    let swimmingStyle:String
    let length: String
    let numOfParticipants:String
    let id: String
    
    init?(json: JSON, id: String) {
        guard let name = json["name"] as? String,
            let activityDate = json["activityDate"] as? String,
            let swimmingStyle = json["swimmingStyle"] as? String,
            let length = json["length"] as? String,
            let numOfParticipants = json["numOfParticipants"] as? String,
            let id = id as? String
            else {return nil}
        
        self.name = name
        self.activityDate = activityDate
        self.swimmingStyle = swimmingStyle
        self.length = length
        self.numOfParticipants = numOfParticipants
        self.id = id
    }
}
