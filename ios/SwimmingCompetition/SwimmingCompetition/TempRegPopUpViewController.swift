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
    var activeTextField: UITextField!

    override func viewDidLoad() {
        super.viewDidLoad()
       
        activeTextField = firstName
        
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.8)

        showAnimate()
        birthDate.datePickerMode = .date
        birthDate.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
        
        firstName.delegate = self
        lastName.delegate = self
        
        // Do any additional setup after loading the view.
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        activeTextField = textField
    }
    
    @objc func keyboardWillChange(notification: Notification) {
        guard let keyboardRect = (notification.userInfo![UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue else {
            return
        }
        if notification.name == Notification.Name.UIKeyboardWillShow ||
            notification.name == Notification.Name.UIKeyboardWillChangeFrame {
            if ((view.frame.height - keyboardRect.height) <= (activeTextField.frame.origin.y+activeTextField.frame.height)) {
                view.frame.origin.y = -keyboardRect.height
               
            } else {
                view.frame.origin.y = 0
            }
            
        } else {
            view.frame.origin.y = 0
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func closeButton(_ sender: Any) {
        removeAnimate()
    }
    
    @IBAction func confirmButton(_ sender: Any) {
        if firstName.text == "" || lastName.text == "" {
            let alert = UIAlertController(title: nil, message: "חובה למלא את כל השדות!", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
            }))
            self.present(alert, animated: true, completion: nil)
        }
        else {
            /*var alert: UIAlertView = UIAlertView(title: "מוסיף משתמש", message: "אנא המתן...", delegate: nil, cancelButtonTitle: nil);
            
            
            let loadingIndicator: UIActivityIndicatorView = UIActivityIndicatorView(frame: CGRect(x: 50, y: 10, width: 37, height: 37)) as UIActivityIndicatorView
            loadingIndicator.center = self.view.center;
            loadingIndicator.hidesWhenStopped = true
            loadingIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
            loadingIndicator.startAnimating();
            
            alert.setValue(loadingIndicator, forKey: "accessoryView")
            loadingIndicator.startAnimating()
            
            alert.show();*/
            let currentCompetition = (self.parent as! CompetitionDetailsViewController).competition
            
            let formatDate = DateFormatter()
            formatDate.dateFormat = "dd/MM/YYYY HH:mm"
            var genderToSend = ""
            if gender.titleForSegment(at: gender.selectedSegmentIndex)! == "זכר" {
                genderToSend = "male"
            }
            else {
                genderToSend = "female"
            }
            let parameters = [
                "competitionId": currentCompetition!.id,
                "firstName": firstName.text!,
                "lastName": lastName.text!,
                "birthDate": formatDate.string(from: birthDate.date),
                "gender": genderToSend
                ] as [String:AnyObject]
            
            Service.shared.connectToServer(path: "joinToCompetition", method: .post, params: parameters) { (response) in
                //alert.dismiss(withClickedButtonIndex: -1, animated: true)
                self.removeAnimate()
            }
        }
        
        
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
    
    
}
