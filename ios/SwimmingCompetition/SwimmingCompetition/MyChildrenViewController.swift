//
//  MyChildrenViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 28/05/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class MyChildrenViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var tableView: UITableView!
    let datePicker = UIDatePicker()
    var alert = UIAlertController()
    
    var myChildren = [User]()
    let a = ["fff"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let addButton = UIBarButtonItem(title: "הוסף ילד", style: .plain, target: self, action: #selector(self.addChild))
        self.navigationItem.leftBarButtonItem = addButton
     
        tableView.delegate = self
        tableView.dataSource = self
        
        // Do any additional setup after loading the view.
    }
    
    @objc func doneClicked() {
        self.alert.dismiss(animated: true, completion: nil)
        self.present(self.alert, animated: false, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func addChild() {
        self.alert = UIAlertController(title: "הוסף ילד", message: nil, preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "כתובת אימייל"
        }
        self.alert.addTextField { (textField) in
            textField.placeholder = "תאריך לידה"
            self.datePicker.datePickerMode = .date
            self.datePicker.locale = NSLocale(localeIdentifier: "he_IL") as Locale as Locale
            
            let toolBar = UIToolbar()
            toolBar.sizeToFit()
            let doneButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(self.doneClicked))
            toolBar.setItems([doneButton], animated: false)
            textField.inputView = self.datePicker
            textField.inputAccessoryView = toolBar
            
            let formatDate = DateFormatter()
            formatDate.dateFormat = "dd/MM/YYYY"
            
                textField.text = formatDate.string(from: self.datePicker.date)
            
            //
        }
        self.alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            let parameters = [
                "email": self.alert.textFields![0],
                "birthDate": self.alert.textFields![1]
            ] as [String:AnyObject]
            
            self.alert.dismiss(animated: true, completion: nil)
        }))
        self.alert.addAction(UIAlertAction(title: "ביטול", style: .cancel, handler: { (action) in
            self.alert.dismiss(animated: true, completion: nil)
        }))
        self.present(self.alert, animated: true, completion: nil)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.a.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "childCell", for: indexPath) as! ChildrenTableViewCell
        cell.label.text = self.a[indexPath.row]
        
        return cell
    }

}
