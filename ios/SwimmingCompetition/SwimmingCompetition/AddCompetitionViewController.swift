//
//  AddCompetitionViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class AddCompetitionViewController: UIViewController {
    
    var pickerView = UIPickerView()
    let stylePicker = ["חזה","גב","חתירה","חופשי"]
    let rangePicker:[Int] = Array(0...100)
    let datePicker = UIDatePicker()
   
    @IBOutlet weak var styleTextField: UITextField!
    @IBOutlet weak var agesTextField: UITextField!
    @IBOutlet weak var dateTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        toolBar()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func styleTextBegin(_ sender: UITextField) {
        self.pickerViewStart()
        styleTextField.inputView = pickerView
    }
    
    @IBAction func agesTextBegin(_ sender: UITextField) {
        self.pickerViewStart()
        agesTextField.inputView = pickerView
    }
    
    @IBAction func dateTextBegin(_ sender: UITextField) {
        dateTextField.inputView = datePicker
    }
    
}

extension AddCompetitionViewController: UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 2
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerView == self.styleTextField.inputView {
            if component == 0 {
                return rangePicker.count
            }
            return stylePicker.count
        }
        
        return rangePicker.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == self.styleTextField.inputView {
            if component == 0 {
                return String(rangePicker[row])
            }
            return stylePicker[row]
        }
        return String(rangePicker[row])
        
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView == self.styleTextField.inputView {
            var age = rangePicker[pickerView.selectedRow(inComponent: 0)]
            var style = stylePicker[pickerView.selectedRow(inComponent: 1)]
            if component == 0 {
                age = rangePicker[row]
            }
            else {
                style = stylePicker[row]
            }
            styleTextField.text = "\(age) מטר \(style)"
        }
        else {
            var fromAge = rangePicker[pickerView.selectedRow(inComponent: 1)]
            var toAge = rangePicker[pickerView.selectedRow(inComponent: 0)]
            if component == 1 {
                fromAge = rangePicker[row]
            }
            else {
                toAge = rangePicker[row]
            }
            agesTextField.text = "\(fromAge) - \(toAge)"
        }
    }
    
    func pickerViewStart() {
        self.pickerView = UIPickerView()
        pickerView.delegate = self
        pickerView.dataSource = self
    }
    
    func toolBar() {
        let toolBar = UIToolbar()
        toolBar.sizeToFit()
        let doneButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(self.doneClicked))
        toolBar.setItems([doneButton], animated: false)
        
        styleTextField.inputAccessoryView = toolBar
        agesTextField.inputAccessoryView = toolBar
        dateTextField.inputAccessoryView = toolBar
    }
    
    @objc func doneClicked() {
      
            dateTextField.text = "\(datePicker.date)"
        
        view.endEditing(true)
    }
    
    
}
