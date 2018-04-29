//
//  File.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/03/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation

class Date {
    
    let days = ["ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"]
    
    func convertDate(date: String) -> DateComponents {
        let formatDate = DateFormatter()
        formatDate.isLenient = true
        formatDate.dateFormat = "dd/MM/yyyy HH:mm"
       
        let convertDate = formatDate.date(from:date)
        
        let calendar = Calendar.current
        
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .weekday], from: convertDate!)
        
        return components
    }
    
    func getDate(fullDate: String) -> String {
        let date = convertDate(date: fullDate)
        return (String(date.day!).count==1 ? "0" : "") + "\(date.day!)/" + (String(date.month!).count==1 ? "0" : "") + "\(date.month!)/" + "\(date.year!)"
    }
    
    func getHour(fullDate: String) -> String {
        let date = convertDate(date: fullDate)
        return (String(date.hour!).count==1 ? "0" : "") + "\(date.hour!):" + (String(date.minute!).count==1 ? "0" : "") + "\(date.minute!)"
    }
    
    func getWeekDay(fullDate: String) -> String {
        let date = convertDate(date: fullDate)
        return self.days[date.weekday!-1]
    }
}
