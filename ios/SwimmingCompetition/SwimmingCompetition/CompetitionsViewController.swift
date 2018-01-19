//
//  CompetitionsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class CompetitionsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate{
    
    var user: User!
    
    @IBOutlet weak var tableView: UITableView!
    
    var competitions = [Competition]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(addCompetition))
        self.navigationItem.rightBarButtonItem = addButton
        getData()
    }
    
    @objc func addCompetition() {
        self.performSegue(withIdentifier: "goToAddCompetition", sender: self)
    }
    
    func getData() {
        let parameters = ["currentUser": ["uid":user.uid]]
        Service.shared.connectToServer(path: "getCompetitions", method: .post, params: parameters) { (response) in
            print(response.data)
        }
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
}

