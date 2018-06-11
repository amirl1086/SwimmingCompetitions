//
//  CompetitionsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class CompetitionsViewController: UIViewController {
    
    var menu_vc: MenuViewController!
    
    var currentUser: User!
    var competitions = [Competition]()
    
    var controllerType = "competitions"
    var pathString = "getCompetitions"
    
    @IBOutlet weak var tableView: UITableView!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initMenuBar()
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        
        if controllerType == "competitions" {
            addButtonView()
        } else if controllerType == "realTime" {
            self.title = "צפייה בזמן אמת"
        } else {
            self.title = "תוצאות תחרות"
        }
        
        getCompetitionsData()
        
        self.tableView.backgroundColor = UIColor.clear
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
        
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tableView.reloadData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToCompetitionDetails" {
            let nextView = segue.destination as! CompetitionDetailsViewController
            let competition = sender as? Competition
            nextView.currentCompetition = competition
            nextView.currentUser = self.currentUser
        }
    }
    
    func addButtonView() {
        if(currentUser.type == "coach") {
            let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(addCompetition))
            self.navigationItem.leftBarButtonItem = addButton
        }
        
    }
    
    @objc func addCompetition(_ sender: UIBarButtonItem) {
        self.performSegue(withIdentifier: "goToAddCompetition", sender: self)
    }
    
    func getCompetitionsData() {
        
        var parameters = [
            "currentUser": [
                "uid":currentUser.uid,
                "birthDate":currentUser.birthDate
            ]
        ] as [String: AnyObject]
        
        if currentUser.type == "student" {
            parameters["filters"] = "age" as AnyObject
        }
       
        Service.shared.connectToServer(path: self.pathString, method: .post, params: parameters) { (response) in
            var compArray = [Competition]()
            print(response)
            for data in response.data {
                var competition : Competition!
                let compData = response.data[data.0] as! JSON
                competition = Competition(json: compData, id: data.0)
                compArray.append(competition)
            }
            let formatDate = DateFormatter()
            formatDate.dateFormat = "dd/MM/yyyy HH:mm"
            self.competitions = compArray
            //self.competitions.sort(by: {formatDate.date(from:$0.activityDate)! > formatDate.date(from:$1.activityDate)!})
            
            self.tableView.reloadData()
            
        }
    }
    
    func initMenuBar() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.menu_vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "menuId") as! MenuViewController
        menu_vc.currentUser = self.currentUser
        self.menu_vc.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
    }
    
    @objc func showMenu() {
        
        let rightButton = UIBarButtonItem(image: UIImage(named: "cancel.png"), style: .plain, target: self, action: #selector(cancelMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.addChildViewController(self.menu_vc)
        self.menu_vc.view.frame = self.view.frame
        self.view.addSubview(self.menu_vc.view)
        self.menu_vc.didMove(toParentViewController: self)
    }
    
    @objc func cancelMenu() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        
        self.menu_vc.view.removeFromSuperview()
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
        let cell = tableView.dequeueReusableCell(withIdentifier: "competitionCell", for: indexPath) as! CompetitionTableViewCell
        cell.name.text = competitions[indexPath.row].name
        cell.date.text = "מתקיימת בתאריך \(DateConvert().getDate(fullDate: competitions[indexPath.row].activityDate)) ביום \(DateConvert().getWeekDay(fullDate: competitions[indexPath.row].activityDate))"

        cell.time.text = "בשעה \(DateConvert().getHour(fullDate: competitions[indexPath.row].activityDate))"
        cell.ages.text = "לגילאי \(competitions[indexPath.row].fromAge) עד \(competitions[indexPath.row].toAge)"
        
        cell.layer.backgroundColor = UIColor.clear.cgColor
        cell.contentView.backgroundColor = UIColor.clear
        //cell.cellView.layer.cornerRadius = cell.cellView.frame.height/4
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 230
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if controllerType == "competitions" {
            performSegue(withIdentifier: "goToCompetitionDetails", sender: competitions[indexPath.row])
            tableView.deselectRow(at: indexPath, animated: true)
        } else {
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let resultsView = sb.instantiateViewController(withIdentifier: "resultsId") as? PersonalResultsViewController {
                resultsView.controllerType = self.controllerType
                resultsView.competition = competitions[indexPath.row]
                self.navigationController?.pushViewController(resultsView, animated: true)
            }
        }
        
       
        
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
