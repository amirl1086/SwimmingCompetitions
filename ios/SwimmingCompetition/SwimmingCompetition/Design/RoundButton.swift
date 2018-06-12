//
//  RoundButton.swift
//  SwimmingCompetition
//
//  Created by Aviel on 26/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit

@IBDesignable
class RoundButton: DesignableButton {
    
    @IBInspectable var cornerRadius: CGFloat = 0 {
        didSet {
            self.layer.cornerRadius = cornerRadius
        }
    }
    
    @IBInspectable var borderWidth: CGFloat = 0 {
        didSet {
            self.layer.borderWidth = borderWidth
        }
    }
    
    @IBInspectable var borderColor: UIColor = UIColor.clear {
        didSet {
            self.layer.borderColor = borderColor.cgColor
        }
    }
   
}
