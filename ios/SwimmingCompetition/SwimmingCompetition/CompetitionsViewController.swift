//
//  CompetitionsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class CompetitionsViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    var user: User!
    var competitions = [Competition]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        
        addButtonView()
        getCompetitionsData()
        
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToCompetitionDetails" {
            let nextView = segue.destination as! CompetitionDetailsViewController
            let competition = sender as? Competition
            nextView.competition = competition
        }
    }
    
    func addButtonView() {
        if(user.type == "coach") {
            let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(addCompetition))
            self.navigationItem.rightBarButtonItem = addButton
        }
    }
    
    @objc func addCompetition() {
        self.performSegue(withIdentifier: "goToAddCompetition", sender: self)
    }
    
    func getCompetitionsData() {
        let parameters = ["currentUser": ["uid":user.uid]] as [String: AnyObject]
        Service.shared.connectToServer(path: "getCompetitions", method: .post, params: parameters) { (response) in
            var compArray = [Competition]()
            for data in response.data {
                var competition : Competition!
                let compData = response.data[data.0] as! JSON
                competition = Competition(json: compData, id: data.0)
                compArray.append(competition)
            }
            self.competitions = compArray
            self.tableView.reloadData()
            print(self.competitions)
        }
    }
    
    
}

extension CompetitionsViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return competitions.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        cell.textLabel?.text = competitions[indexPath.row].name
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        performSegue(withIdentifier: "goToCompetitionDetails", sender: competitions[indexPath.row])
    }
    
}

class cellTableCompetition: UITableViewCell {
    
    	
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
