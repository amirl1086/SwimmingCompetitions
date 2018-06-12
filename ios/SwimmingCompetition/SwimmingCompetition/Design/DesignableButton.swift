//
//  DesignableButton.swift
//  SwimmingCompetition
//
//  Created by Aviel on 12/06/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class DesignableButton: UIButton {
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.transform = CGAffineTransform(scaleX: 1.1, y: 1.1)
        
        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 6, options: .allowUserInteraction, animations: {
            self.transform = CGAffineTransform.identity
        }, completion: nil)
        
        super.touchesBegan(touches, with: event)
    }
}
