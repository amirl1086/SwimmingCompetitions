//
//  MyChildrenViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 28/05/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class MyChildrenViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var menu_vc: MenuViewController!
    
    @IBOutlet weak var tableView: UITableView!
    let datePicker = UIDatePicker()
    var alert = UIAlertController()
    var currentUser: User!
    
    /* the children array */
    var myChildren = [User]()
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initMenuBar()
        
        self.title = "הילדים שלי"
        let addButton = UIBarButtonItem(title: "הוסף ילד", style: .plain, target: self, action: #selector(self.addChild))
        self.navigationItem.leftBarButtonItem = addButton
        
        getChildren()
        
        tableView.delegate = self
        tableView.dataSource = self
        
        self.tableView.backgroundColor = UIColor.clear
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
        
        // Do any additional setup after loading the view.
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /* request to get the children */
    func getChildren() {
        let parameters = [
            "value": self.currentUser.uid,
            "filters": "parentId"
            ] as [String:AnyObject]
        Service.shared.connectToServer(path: "getUsersByParentId", method: .post, params: parameters) { (response) in
            if response.succeed {
                /* set each child object as a User and push to the children array */
                for data in response.data {
                    var user : User!
                    let data = response.data[data.0] as! JSON
                    user = User(json: data)
                    self.currentUser.children.append(user)
                    self.tableView.reloadData()
                }
                /* if the children array is empty - no children to show */
                if self.currentUser.children.isEmpty {
                    self.present(Alert().confirmAlert(title: "", message: "אין ילדים להצגה"), animated: true, completion: nil)
                }
            } else{
                self.present(Alert().confirmAlert(title: "", message: "לא ניתן להציג ילדים"), animated: true, completion: nil)
            }
            
        }
    }
    
    /* add a new child */
    @objc func addChild() {
        let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "popUpId") as! PopUpViewController
        self.addChildViewController(popOverVC)
        popOverVC.view.frame = self.view.frame
        popOverVC.toolBar.items![0].title = "הוסף ילד"
        popOverVC.currentUser = self.currentUser
        popOverVC.senderView = self
        self.view.addSubview(popOverVC.view)
        popOverVC.didMove(toParentViewController: self)
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.currentUser.children.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "childCell", for: indexPath) as! ChildrenTableViewCell
        cell.label.textAlignment = .center
        cell.label.text = "\(self.currentUser.children[indexPath.row].firstName)"
        cell.layer.backgroundColor = UIColor.clear.cgColor
        cell.contentView.backgroundColor = UIColor.clear
        cell.backgroundColor = UIColor.clear
        
        return cell
    }
    
    /* if childe selected - go to his statistic view */
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        if let statisticView = sb.instantiateViewController(withIdentifier: "statisticsId") as? StatisticsViewController {
            statisticView.currentUser = self.currentUser.children[indexPath.row]
            statisticView.isChild = true
            self.navigationController?.pushViewController(statisticView, animated: true)
        }
    }
    
    /* create the side menu bar */
    func initMenuBar() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.menu_vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "menuId") as! MenuViewController
        menu_vc.currentUser = self.currentUser
        self.menu_vc.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
    }
    
    /* show the side menu bar */
    @objc func showMenu() {
        
        let rightButton = UIBarButtonItem(image: UIImage(named: "cancel.png"), style: .plain, target: self, action: #selector(cancelMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.addChildViewController(self.menu_vc)
        self.menu_vc.view.frame = self.view.frame
        self.view.addSubview(self.menu_vc.view)
        self.menu_vc.didMove(toParentViewController: self)
    }
    
    /* cancel side menu bar */
    @objc func cancelMenu() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        
        self.menu_vc.view.removeFromSuperview()
    }

}
