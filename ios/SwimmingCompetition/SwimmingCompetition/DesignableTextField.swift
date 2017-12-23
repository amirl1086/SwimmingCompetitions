//
//  DesignableTextField.swift
//  SwimmingCompetition
//
//  Created by Aviel on 18/12/2017.
//  Copyright © 2017 Aviel. All rights reserved.
//

import UIKit

@IBDesignable
class DesignableTextField: UITextField {

    @IBInspectable var leftImage: UIImage? {
        didSet {
            updateView()
        }
    }
    
    func updateView() {
        if let image = leftImage {
            leftViewMode = .always
            
            let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 30, height: 25))
            imageView.image = image
            
            let view = UIView(frame: CGRect(x: 0, y: 0, width: 30, height: 25))
            view.addSubview(imageView)
            
            leftView = view
        }
        else {
            leftViewMode = .never
        }
    }
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */

}
