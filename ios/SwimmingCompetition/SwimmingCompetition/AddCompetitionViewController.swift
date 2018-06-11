//
//  AddCompetitionViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

protocol dataProtocol {
    func dataSelected(name: String, activityDate: String, swimmingStyle: String, length: String, numOfParticipants: String, fromAge: String, toAge: String)
}

class AddCompetitionViewController: UIViewController, UITextFieldDelegate {
    
    var delegate: dataProtocol?
    //The picker view object
    var pickerView = UIPickerView()
    let stylePicker = ["חזה","גב","חתירה","חופשי"]
    let rangePicker:[Int] = Array(0...100)
    let rangeSwimPicker:[Int] = Array(0...5)
    let datePicker = UIDatePicker()
    
    
    @IBOutlet weak var scrollView: UIScrollView!
    
    @IBOutlet weak var nameTextField: UITextField!
    @IBOutlet weak var styleTextField: UITextField!
    @IBOutlet weak var numberTextField: UITextField!
    @IBOutlet weak var dateTextField: UITextField!
    @IBOutlet weak var agesTextField: UITextField!
    var activeTextField: UITextField!
    
    var isEdit: Bool = false
    var editedCompetitionId: String = ""
    var competitionName: String = ""
    var numOfParticipants: String = ""
    var range: Int = 0
    var style: String = ""
    var fromAge: Int = 0
    var toAge: Int = 0
    var dateToSend = ""
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nameTextField.delegate = self
        styleTextField.delegate = self
        numberTextField.delegate = self
        dateTextField.delegate = self
        agesTextField.delegate = self
        
        activeTextField = nameTextField
        
        if isEdit {
            nameTextField.text = competitionName
            styleTextField.text = "\(range) מטר \(style)"
            numberTextField.text = numOfParticipants
            dateTextField.text = "\(DateConvert().getHour(fullDate: dateToSend)) \(DateConvert().getDate(fullDate: dateToSend))"
            agesTextField.text = "מגיל \(fromAge) עד גיל \(toAge)"
        }
        
        toolBar()
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
        //self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        //navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillChangeFrame, object: nil)
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
        
        nameTextField.bottomLineBorder()
        styleTextField.bottomLineBorder()
        numberTextField.bottomLineBorder()
        dateTextField.bottomLineBorder()
        agesTextField.bottomLineBorder()
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
        
        if self.nameTextField.text == "" || self.styleTextField.text == "" || self.numberTextField.text == "" || self.dateTextField.text == "" || self.agesTextField.text == "" {
            
            self.present(Alert().confirmAlert(title: "", message: "חובה למלא את כל השדות!"), animated: true, completion: nil)
        } else if self.fromAge > self.toAge {
            self.present(Alert().confirmAlert(title: "", message: "טווח הגילאים אינו תקין"), animated: true, completion: nil)
        }
        else {
            var parameters = [
                "activityDate": dateToSend,
                "length": self.range,
                "name": self.nameTextField.text!,
                "numOfParticipants": self.numberTextField.text!,
                "swimmingStyle": self.style,
                "fromAge": self.fromAge,
                "toAge": self.toAge
                ] as [String : AnyObject]
            if isEdit {
                
                parameters["id"] = editedCompetitionId as AnyObject
            }
            
            
            Service.shared.connectToServer(path: "setNewCompetition", method: .post, params: parameters) { (response) in
                
                var message = ""
                if response.succeed {
                    message = "התחרות נשמרה בהצלחה"
                }
                else {
                    message = "התחרות לא נוספה"
                }
                let alert = UIAlertController(title: "", message: message, preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                    if response.succeed {
                        if self.isEdit {
                            self.delegate?.dataSelected(name: self.nameTextField.text!, activityDate: self.dateToSend, swimmingStyle: self.style, length: "\(self.range)", numOfParticipants: "\(self.numberTextField.text!)", fromAge: "\(self.fromAge)", toAge: "\(self.toAge)")
                        } else {
                            if (self.parent as? CompetitionsViewController) != nil {
                                let parentView = self.parent as! CompetitionsViewController
                                var competition : Competition!
                                let compData = response.data
                                competition = Competition(json: compData, id: compData["id"] as! String)
                                parentView.competitions.append(competition)
                                parentView.tableView.reloadData()
                            }
                           
                        }
                        _ = self.navigationController?.popViewController(animated: true)
                    }
                }))
                self.present(alert, animated: true, completion: nil)
            
                
                
            }
        }
        
       
    }
}

extension AddCompetitionViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    
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
        if pickerView == self.numberTextField.inputView {
            return rangeSwimPicker.count
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
        if pickerView == self.numberTextField.inputView {
            return String(rangeSwimPicker[row])
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
            numberTextField.text = String(rangeSwimPicker[row])
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
        if dateTextField.inputView == datePicker {
            let formatDate = DateFormatter()
            formatDate.dateFormat = "HH:mm dd/MM/YYYY"
            
            dateTextField.text = formatDate.string(from: datePicker.date)
            
            formatDate.dateFormat = "dd/MM/yyyy HH:mm"
            
            
            dateToSend = formatDate.string(from: datePicker.date)
        }
        
        view.endEditing(true)
    }
    
    
}
