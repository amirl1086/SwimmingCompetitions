//
//  PersonalResultsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/03/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Firebase

class PersonalResultsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
  
    /* Struct by age with male and female arrays */
    struct Result {
        var age: String!
        var maleResults:[Participant]!
        var femaleResults:[Participant]!
    }

    var menu_vc: MenuViewController!
    
    var currentUser: User!
    
    var array = [Result]()
    var data:JSON = [:]
    
    /* the type of view controller */
    var controllerType = ""
    var competition: Competition!
    
    var realTimeArray = [Participant]()
    
    @IBOutlet weak var tableView: UITableView!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        tableView.allowsSelection = false
        
        if controllerType == "results" {
            getResults()
        } else if controllerType == "realTime" {
            self.title = "צפייה בזמן אמת"
            initMenuBar()
            Service.shared.connectToServer(path: "getCompetitionInProgress", method: .post, params: [:]) { (response) in
                if response.succeed {
                    for data in response.data {
                        var competition : Competition!
                        let compData = response.data[data.0] as! JSON
                        competition = Competition(json: compData, id: data.0)
                        self.competition = competition
                        self.tableView.reloadData()
                    }
                    self.getRealTimeResults()
                } else {
                    self.present(Alert().confirmAlert(title: "", message: "לא נמצאה תחרות"), animated: true, completion: nil)
                }
            }
        } else {
            setCompetitionResults()
        }
        
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
        self.tableView.backgroundColor = UIColor.clear
       
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }
    
    /* listener for competition in real time - to show the results */
    func getRealTimeResults() {
        if self.competition != nil {
            Database.database().reference().child("personalResults/\(competition.getId())").observe(.childAdded) { (snapshot) in
                let data = snapshot.value as! JSON
                let participant = Participant(json: data, id: "")
                self.realTimeArray.insert(participant, at: 0)
                let formatDate = DateFormatter()
                formatDate.dateFormat = "dd/MM/yyyy HH:mm:ss"
                /* sort the competitors by timeStamp */
                self.realTimeArray.sort(by: {(formatDate.date(from:$0.timeStamp) != nil ? formatDate.date(from:$0.timeStamp)! : Date()) > (formatDate.date(from:$1.timeStamp) != nil ? formatDate.date(from:$1.timeStamp)! : Date())})
                self.tableView.reloadData()
            }
        } else {
            self.present(Alert().confirmAlert(title: "", message: "אין תחרות לצפיה"), animated: true, completion: nil)
        }
    }
    
    /* get the result for specific competition by id */
    func getResults() {
        let parameters = [
            "competition": "{\"id\":\"\(self.competition.getId())\"}"
        ] as [String: AnyObject]
        Service.shared.connectToServer(path: "getPersonalResults", method: .post, params: parameters) { (response) in
            if response.succeed {
                
                self.data = response.data
                self.setCompetitionResults()
                self.tableView.reloadData()
            }else {
                self.present(Alert().confirmAlert(title: "", message: "לא ניתן להציג תוצאות"), animated: true, completion: nil)
                
            }
        }
    }
    
    /* set the results after the "coach" finish with the iterations and go to results view */
    func setCompetitionResults() {
        for age in data {
            /* for each age - sort by males and females */
            if age.0 != "type" {
                var getAge = age.0
                if Int(getAge) == nil {
                    getAge = "0"
                }
                var maleArray = [Participant]()
                var femaleArray = [Participant]()
                
                var allData = age.1 as! JSON
                /* get the male participants */
                for male in allData["males"] as! NSArray{
                    let data = male as! JSON
                    let maleResult = Participant(json: data, id: "")
                    maleArray.append(maleResult)
                }
                /* set the first places */
                for i in 0..<maleArray.count {
                    if i == 0 {
                        maleArray[i].setRank(rank: 1)
                    }
                    if i == 1 {
                        maleArray[i].setRank(rank: 2)
                    }
                    if i == 2 {
                        maleArray[i].setRank(rank: 3)
                    }
                }
                /* get the female participants */
                for female in allData["females"] as! NSArray {
                    let data = female as! JSON
                    let femaleResult = Participant(json: data, id: "")
                    femaleArray.append(femaleResult)
                }
                /* set the first places */
                for i in 0..<femaleArray.count {
                    if i == 0 {
                        femaleArray[i].setRank(rank: 1)
                    }
                    if i == 1 {
                        femaleArray[i].setRank(rank: 2)
                    }
                    if i == 2 {
                        femaleArray[i].setRank(rank: 3)
                    }
                }
                
                let results = Result(age: getAge, maleResults: maleArray, femaleResults: femaleArray)
                self.array.append(results)
            }
            
        }
        /* sort by age */
        self.array = self.array.sorted(by: {Int($0.age)! < Int($1.age)!})
        if self.array.isEmpty {
            self.present(Alert().confirmAlert(title: "", message: "אין תוצאות להציג"), animated: true, completion: nil)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if controllerType == "realTime" {
            return self.realTimeArray.count
        }
       
        return array[section].maleResults.count + array[section].femaleResults.count + 2
     
        
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "resultsCell", for: indexPath) as! ResultsTableViewCell
        let title = UILabel()
        title.font = UIFont.boldSystemFont(ofSize: title.font.pointSize)
        
        if controllerType == "realTime" {
            cell.cellView.backgroundColor = UIColor.clear
            cell.contentView.backgroundColor = UIColor.clear
            cell.name.text = "\(self.realTimeArray[indexPath.row].firstName) \(self.realTimeArray[indexPath.row].lastName)"
            cell.rankImage.isHidden = true
            cell.score.isHidden = false
            cell.score.text = self.realTimeArray[indexPath.row].score
            cell.layer.backgroundColor = UIColor.clear.cgColor
        } else {
            if indexPath.row == 0 {
                //cell.cellView.backgroundColor = UIColor.blue
                title.text = "בנים"
                cell.name.text = title.text
                cell.rankImage.isHidden = true
                cell.score.isHidden = true
                cell.cellView.layer.cornerRadius = cell.cellView.frame.height/2
                cell.name.font = cell.name.font.withSize(35)
                
                //cell.name.frame = CGRect(x: (cell.cellView.frame.width/2)-(cell.name.frame.width/2), y: cell.name.frame.origin.y, width: cell.name.frame.width, height: cell.name.frame.height)
                
            }
            else if indexPath.row-1 < array[indexPath.section].maleResults.count {
                cell.cellView.backgroundColor = UIColor.clear
                cell.name.text = "\(array[indexPath.section].maleResults[indexPath.row-1].lastName) \(array[indexPath.section].maleResults[indexPath.row-1].firstName)"
                cell.score.text = array[indexPath.section].maleResults[indexPath.row-1].score
                cell.rankImage.image = UIImage(named: "\(array[indexPath.section].maleResults[indexPath.row-1].rank).png")
                cell.rankImage.isHidden = false
                cell.score.isHidden = false
                cell.contentView.backgroundColor = UIColor.clear
                cell.name.font = cell.name.font.withSize(20)
                //cell.name.frame = CGRect(x: 322, y: cell.name.frame.origin.y, width: cell.name.frame.width, height: cell.name.frame.height)
                
            }
            else if indexPath.row-1 == array[indexPath.section].maleResults.count {
                //cell.cellView.backgroundColor = UIColor.purple
                title.text = "בנות"
                cell.name.text = title.text
                cell.rankImage.isHidden = true
                cell.score.isHidden = true
                
                cell.cellView.layer.cornerRadius = cell.cellView.frame.height/2
                cell.name.font = cell.name.font.withSize(35)
                
                //cell.name.frame = CGRect(x: (cell.cellView.frame.width/2)-(cell.name.frame.width/2), y: cell.name.frame.origin.y, width: cell.name.frame.width, height: cell.name.frame.height)
            }
            else {
                cell.cellView.backgroundColor = UIColor.clear
                cell.name.text = "\(array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].lastName) \(array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].firstName)"
                cell.score.text = array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].score
                cell.rankImage.image = UIImage(named: "\(array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].rank).png")
                cell.rankImage.isHidden = false
                cell.score.isHidden = false
                cell.contentView.backgroundColor = UIColor.clear
                cell.name.font = cell.name.font.withSize(20)
                //cell.name.frame = CGRect(x: 322, y: cell.name.frame.origin.y, width: cell.name.frame.width, height: cell.name.frame.height)
            }
            cell.cellView.backgroundColor = UIColor.clear
            cell.layer.backgroundColor = UIColor.clear.cgColor
        }
        
        return cell
        
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        
        if controllerType == "realTime" {
            return 1
        }
        return array.count
        
    }
    
    /* the sctions. ages - for the results view. competition name - for the real time view */
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        if controllerType == "realTime" {
            let view = UIView()
            view.backgroundColor = UIColor.black
            let label = UILabel()
            label.text = self.competition != nil ? self.competition.name : ""
            label.textAlignment = .center
            label.font = label.font.withSize(25)
            label.textColor = UIColor.white
            label.frame = CGRect(x: 0, y: 5, width: self.view.frame.width, height: 35)
            view.addSubview(label)
            return view
        }
        
        let view = UIView()
        view.backgroundColor = UIColor.black
        let label = UILabel()
        label.text = "גילאי \(array[section].age!)"
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        label.textColor = UIColor.white
        label.frame = CGRect(x: (self.view.frame.width/2)-75, y: 5, width: 150, height: 35)
        view.addSubview(label)
        return view
        
        
        
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 50
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
