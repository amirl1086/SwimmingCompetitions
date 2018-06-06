//
//  File.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/03/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation

class DateConvert {
    
    let days = ["ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"]
    
    func convertDate(date: String) -> DateComponents? {
        let formatDate = DateFormatter()
        formatDate.isLenient = true
        formatDate.dateFormat = "dd/MM/yyyy HH:mm"
       
        let convertDate = formatDate.date(from:date)
        if convertDate == nil {
            return nil
        }
        let calendar = Calendar.current
        
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .weekday], from: convertDate!)
        
        return components
    }
    
    func getDate(fullDate: String) -> String {
        let date = convertDate(date: fullDate)
        if date == nil {
            return ""
        }
        return (String(date!.day!).count==1 ? "0" : "") + "\(date!.day!)/" + (String(date!.month!).count==1 ? "0" : "") + "\(date!.month!)/" + "\(date!.year!)"
    }
    
    func getHour(fullDate: String) -> String {
        let date = convertDate(date: fullDate)
        if date == nil {
            return ""
        }
        return (String(date!.hour!).count==1 ? "0" : "") + "\(date!.hour!):" + (String(date!.minute!).count==1 ? "0" : "") + "\(date!.minute!)"
    }
    
    func getWeekDay(fullDate: String) -> String {
        let date = convertDate(date: fullDate)
        if date == nil {
            return ""
        }
        return self.days[date!.weekday!-1]
    }
    
    func getHowOld(date: String) -> Int? {
        let now = Date()
        let formatDate = DateFormatter()
        formatDate.isLenient = true
        formatDate.dateFormat = "dd/MM/yyyy"
        
        let convertDate = formatDate.date(from:date)
        if convertDate == nil {
            return nil
        }
        
        let calendar = Calendar.current
        
        let components = calendar.dateComponents([.year], from: convertDate!, to: now)
        
        return components.year!
    }
}
