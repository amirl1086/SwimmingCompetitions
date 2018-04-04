//
//  TempRegPopUpViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 04/04/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class TempRegPopUpViewController: UIViewController, UITextFieldDelegate {
    
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var gender: UISegmentedControl!
    @IBOutlet weak var birthDate: UIDatePicker!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.8)

        showAnimate()
        birthDate.datePickerMode = .date
        birthDate.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
        
        firstName.delegate = self
        lastName.delegate = self
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func closeButton(_ sender: Any) {
        removeAnimate()
    }
    
    @IBAction func confirmButton(_ sender: Any) {
        print(firstName.text!)
        print(lastName.text!)
        print(gender.titleForSegment(at: gender.selectedSegmentIndex)!)
        let formatDate = DateFormatter()
        formatDate.dateFormat = "dd/MM/YYYY"
        
        print(formatDate.string(from: birthDate.date))
    }
    
    func showAnimate() {
        self.view.transform = CGAffineTransform(scaleX: 1.3, y: 1.3)
        self.view.alpha = 0.0
        UIView.animate(withDuration: 0.25) {
            self.view.alpha = 1.0
            self.view.transform = CGAffineTransform(scaleX: 1.0, y: 1.0)
        }
    }
    
    func removeAnimate() {
        UIView.animate(withDuration: 0.25, animations: {
            self.view.transform = CGAffineTransform(scaleX: 1.3, y: 1.3)
            self.view.alpha = 0.0
        }) { (finished) in
            if finished {
                self.view.removeFromSuperview()
            }
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
}
