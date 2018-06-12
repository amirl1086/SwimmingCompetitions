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

    @IBOutlet weak var scrollView: UIScrollView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
       
        firstName.delegate = self
        lastName.delegate = self
        firstName.autocorrectionType = .no
        lastName.autocorrectionType = .no
        activeTextField = firstName
        
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.8)

        showAnimate()
        birthDate.datePickerMode = .date
        birthDate.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
        
        scrollView.isScrollEnabled = true
        scrollView.isUserInteractionEnabled = true
       
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
        guard let userInfo = notification.userInfo,
            let frame = (userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue else{return}
        var contentInset = UIEdgeInsets.zero
        
        if notification.name == Notification.Name.UIKeyboardWillShow ||
            notification.name == Notification.Name.UIKeyboardWillChangeFrame {
            contentInset = UIEdgeInsets(top: 0, left: 0, bottom: frame.height, right: 0)
        }
        
        scrollView.contentInset = contentInset
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
        
        let currentCompetition = (self.parent as! CompetitionDetailsViewController).currentCompetition
        
        let formatDate = DateFormatter()
        formatDate.dateFormat = "dd/MM/YYYY"
        
        if firstName.text == "" || lastName.text == "" {
            let alert = UIAlertController(title: nil, message: "נא למלא את כל השדות", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
            }))
            self.present(alert, animated: true, completion: nil)
        } else if DateConvert().getHowOld(date: formatDate.string(from: birthDate.date))! < Int((currentCompetition?.fromAge)!)! || DateConvert().getHowOld(date: formatDate.string(from: birthDate.date))! > Int((currentCompetition?.toAge)!)! {
            self.present(Alert().confirmAlert(title: "", message: "גיל המשתמש אינו מתאים לתחרות"), animated: true, completion: nil)
        }
        else {
            
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
                self.removeAnimate()
                if response.succeed {
                    self.present(Alert().confirmAlert(title: "", message: "הרשמה בוצעה בהצלחה"), animated: true, completion: nil)
                } else {
                    self.present(Alert().confirmAlert(title: "שגיאה", message: "הרשמה לא בוצעה"), animated: true, completion: nil)
                }
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
