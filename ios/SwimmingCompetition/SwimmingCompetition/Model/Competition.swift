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
    let length: Int
    let numOfParticipants:Int
    
    init?(json: JSON) {
        guard let name = json["name"] as? String,
            let activityDate = json["activityDate"] as? String,
            let swimmingStyle = json["swimmingStyle"] as? String,
            let length = json["length"] as? Int,
            let numOfParticipants = json["numOfParticipants"] as? Int
            else {return nil}
        
        self.name = name
        self.activityDate = activityDate
        self.swimmingStyle = swimmingStyle
        self.length = length
        self.numOfParticipants = numOfParticipants
    }
}
