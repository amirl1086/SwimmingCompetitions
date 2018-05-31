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
    var currentUser: User!
    
    var myChildren = [User]()
    let a = ["fff"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "הילדים שלי"
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
        let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "popUpId") as! PopUpViewController
        self.addChildViewController(popOverVC)
        popOverVC.view.frame = self.view.frame
        popOverVC.toolBar.items![0].title = "הוסף ילד"
        popOverVC.currentUser = self.currentUser
        self.view.addSubview(popOverVC.view)
        popOverVC.didMove(toParentViewController: self)
        /*
        self.alert = UIAlertController(title: "הוסף ילד", message: nil, preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "כתובת אימייל"
        }
        self.alert.addTextField { (textField) in
            textField.placeholder = "תאריך לידה"
           
            
            
            //
        }
        self.alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            let parameters = [
                "uid": self.currentUser.uid,
                "email": self.alert.textFields![0].text!,
                "birthDate": self.alert.textFields![1].text!
            ] as [String:AnyObject]
           
            Service.shared.connectToServer(path: "addChildToParent", method: .post, params: parameters, completion: { (response) in
                self.alert.dismiss(animated: true, completion: nil)
                if response.succeed {
                    var user = User(json: response.data)
                    user.uid = response.data["uid"] as! String
                    if !self.currentUser.children.contains(where: {$0.uid == user.uid}) {
                        self.currentUser.children.append(user)
                        self.tableView.reloadData()
                    }
                    
                    
                } else {
                    let alert = UIAlertController(title: "הוספה נכשלה", message: "נא לוודא שהנתונים נכונים", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
                        alert.dismiss(animated: true, completion: nil)
                    }))
                    self.present(alert, animated: true, completion: nil)
                }
            })
            
        }))
        self.alert.addAction(UIAlertAction(title: "ביטול", style: .cancel, handler: { (action) in
            self.alert.dismiss(animated: true, completion: nil)
        }))
        self.present(self.alert, animated: true, completion: nil)*/
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.currentUser.children.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "childCell", for: indexPath) as! ChildrenTableViewCell
        cell.label.text = "\(self.currentUser.children[indexPath.row].firstName) \(self.currentUser.children[indexPath.row].uid)"
        
        return cell
    }

}
