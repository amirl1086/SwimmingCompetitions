//
//  Alert.swift
//  SwimmingCompetition
//
//  Created by Aviel on 03/06/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import Foundation
import UIKit

class Alert {
    func confirmAlert(title:String, message:String) -> UIAlertController {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        
        return alert
    }
}
