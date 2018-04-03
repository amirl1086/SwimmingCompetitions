//
//  AddCompetitionViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class AddCompetitionViewController: UIViewController {
    
    //The picker view object
    var pickerView = UIPickerView()
    let stylePicker = ["חזה","גב","חתירה","חופשי"]
    let rangePicker:[Int] = Array(0...100)
    let datePicker = UIDatePicker()
   
    @IBOutlet weak var nameTextField: UITextField!
    @IBOutlet weak var styleTextField: UITextField!
    @IBOutlet weak var numberTextField: UITextField!
    @IBOutlet weak var dateTextField: UITextField!
    @IBOutlet weak var agesTextField: UITextField!
    
    var range: Int = 0
    var style: String = ""
    var fromAge: Int = 0
    var toAge: Int = 0
    var dateToSend = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        toolBar()
      
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        //navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func styleTextBegin(_ sender: UITextField) {
        self.pickerViewStart()
        styleTextField.inputView = pickerView
    }
    
    @IBAction func numberTextBegin(_ sender: UITextField) {
        self.pickerViewStart()
        numberTextField.inputView = pickerView
    }
    
    @IBAction func agesTextBegin(_ sender: UITextField) {
        self.pickerViewStart()
        agesTextField.inputView = pickerView
    }
    
    
    @IBAction func dateTextBegin(_ sender: UITextField) {
        dateTextField.inputView = datePicker
    }
    
    @IBAction func addCompetitionButton(_ sender: Any) {
        let parameters = [
            "activityDate": dateToSend,
            "length": Float(self.range),
            "name": self.nameTextField.text!,
            "numOfParticipants": self.numberTextField.text!,
            "swimmingStyle": self.style,
            "fromAge": self.fromAge,
            "toAge": self.toAge
            ] as [String : AnyObject]
        
        Service.shared.connectToServer(path: "setNewCompetition", method: .post, params: parameters) { (response) in
            print(response.data)
            var message = ""
            if response.succeed {
                message = "התחרות נוספה בהצלחה"
            }
            else {
                message = "התחרות לא נוספה"
            }
            let alert = UIAlertController(title: "", message: message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
                self.dismiss(animated: true, completion: nil)
            }))
            self.present(alert, animated: true, completion: nil)
            
        }
       
    }
}

extension AddCompetitionViewController: UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate {
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        if (pickerView == self.styleTextField.inputView || pickerView == self.agesTextField.inputView) {
            return 2
        }
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerView == self.styleTextField.inputView {
            if component == 0 {
                return stylePicker.count
            }
            return rangePicker.count
        }
        
        return rangePicker.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == self.styleTextField.inputView {
            if component == 0 {
                return stylePicker[row]
            }
            return String(rangePicker[row])
        }
        return String(rangePicker[row])
        
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView == self.styleTextField.inputView {
            var style = stylePicker[pickerView.selectedRow(inComponent: 0)]
            var range = rangePicker[pickerView.selectedRow(inComponent: 1)]
            if component == 0 {
                style = stylePicker[row]
            }
            else {
                range = rangePicker[row]
            }
            self.range = range
            self.style = style
            styleTextField.text = "\(range) מטר \(style)"
        }
        else if pickerView == self.agesTextField.inputView {
            var fromAge = rangePicker[pickerView.selectedRow(inComponent: 1)]
            var toAge = rangePicker[pickerView.selectedRow(inComponent: 0)]
            
            if component == 1 {
                fromAge = rangePicker[row]
            }
            else {
                toAge = rangePicker[row]
            }
            self.fromAge = fromAge
            self.toAge = toAge
            agesTextField.text = "מגיל \(fromAge) עד גיל \(toAge)"
        }
        else {
            numberTextField.text = String(rangePicker[row])
        }
    }
    
    func pickerViewStart() {
        self.pickerView = UIPickerView()
        pickerView.delegate = self
        pickerView.dataSource = self
    }
    
    func toolBar() {
        datePicker.datePickerMode = .dateAndTime
        datePicker.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
        
        let toolBar = UIToolbar()
        toolBar.sizeToFit()
        let doneButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(self.doneClicked))
        
        toolBar.setItems([doneButton], animated: false)
        
        styleTextField.inputAccessoryView = toolBar
        numberTextField.inputAccessoryView = toolBar
        dateTextField.inputAccessoryView = toolBar
        agesTextField.inputAccessoryView = toolBar
    }
    
    @objc func doneClicked() {
        let formatDate = DateFormatter()
        formatDate.dateFormat = "dd/MM/YYYY"
       
        
        print(formatDate.locale!)
        dateTextField.text = formatDate.string(from: datePicker.date)
        
        formatDate.dateFormat = "E MMM dd HH:mm:ss yyyy"
        
        
        dateToSend = formatDate.string(from: datePicker.date)
        
        view.endEditing(true)
    }
    
    
}
