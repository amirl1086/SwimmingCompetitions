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

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func addChild() {
        let alert = UIAlertController(title: "הוסף ילד", message: nil, preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "כתובת אימייל"
        }
        alert.addTextField { (textField) in
            textField.placeholder = "תאריך לידה"
        }
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            let parameters = [
                "email": alert.textFields![0],
                "birthDate": alert.textFields![1]
            ] as [String:AnyObject]
            
            alert.dismiss(animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "ביטול", style: .cancel, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
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
