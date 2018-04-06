//
//  Competition.swift
//  SwimmingCompetition
//
//  Created by Aviel on 18/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation

class Competition {
    
    var name:String
    var activityDate:String
    var swimmingStyle:String
    var length: String
    var numOfParticipants:String
    var id: String
    var fromAge: String
    var toAge: String
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
        self.participants = []
        
        if participants != nil {
            for part in participants! {
                var participant : Participant!
                let data = participants![part.0] as! JSON
                participant = Participant(json: data, id: part.0)
                self.participants.append(participant)
            }
        }
    }
    
    func getName() -> String {
        return self.name
    }
    
    func setName(name: String) {
        self.name = name
    }
    
    func getActivityDate() -> String {
        return self.activityDate
    }
    
    func setActivityDate(activityDate: String) {
        self.activityDate = activityDate
    }
    
    func getSwimmingStyle() -> String {
        return self.swimmingStyle
    }
    
    func setSwimmingStyle(swimmingStyle: String) {
        self.swimmingStyle = swimmingStyle
    }
    
    func getLength() -> String {
        return self.length
    }
    
    func setLength(length: String) {
        self.length = length
    }
    
    func getNumOfParticipants() -> String {
        return self.numOfParticipants
    }
    
    func setNumOfParticipants(numOfParticipants: String) {
        self.numOfParticipants = numOfParticipants
    }
    
    func getFromAge() -> String {
        return self.fromAge
    }
    
    func setFromAge(fromAge: String) {
        self.fromAge = fromAge
    }
    
    func getToAge() -> String {
        return self.toAge
    }
    
    func setToAge(toAge: String) {
        self.toAge = toAge
    }
    
}
