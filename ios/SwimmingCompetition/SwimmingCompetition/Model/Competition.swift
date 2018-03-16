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
    let fromAge: String
    let toAge: String
    var participants = [Participant]()
    
    
    init(json: JSON, id: String) {
            let name = json["name"] as? String
            let activityDate = json["activityDate"] as? String
            let swimmingStyle = json["swimmingStyle"] as? String
            let length = json["length"] as? String
            let numOfParticipants = json["numOfParticipants"] as? String
            let fromAge = json["fromAge"] as? String
            let toAge = json["toAge"] as? String
            let id = id as? String
            let participants = json["participants"] as? JSON
            //else{return nil}
        
        
        self.name = name!
        self.activityDate = activityDate!
        self.swimmingStyle = swimmingStyle!
        self.length = length!
        self.numOfParticipants = numOfParticipants!
        self.fromAge = fromAge!
        self.toAge = toAge!
        self.id = id!
        
        if participants != nil {
            for part in participants! {
                var participant : Participant!
                let data = participants![part.0] as! JSON
                participant = Participant(json: data, id: part.0)
                self.participants.append(participant)
            }
        }
      
    }
}
