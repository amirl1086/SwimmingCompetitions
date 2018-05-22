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
  
    struct Result {
        var age: String!
        var maleResults:[Participant]!
        var femaleResults:[Participant]!
    }

    var array = [Result]()
    var data:JSON = [:]
    var controllerType = ""
    var competition: Competition!
    
    var realTimeArray = [Participant]()
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        tableView.allowsSelection = false
        
        if controllerType == "" {
            getCompetitionResults()
        } else {
            
            Database.database().reference().child("personalResults/\(competition.getId())").observeSingleEvent(of: .value) { (snapshot) in
                for part in snapshot.children.allObjects as! [DataSnapshot]{
                    let data = part.value as! JSON
                    let participant = Participant(json: data, id: part.key)
                    self.realTimeArray.append(participant)
                    print(participant.firstName)
                    
                }
                print(self.realTimeArray.count)
                self.tableView.reloadData()
            }
            Database.database().reference().child("personalResults/\(competition.getId())").observe(.childAdded) { (snapshot) in
                let data = snapshot.value as! JSON
                let participant = Participant(json: data, id: "")
                self.realTimeArray.insert(participant, at: 0)
                
                print(snapshot)
                self.tableView.reloadData()
            }
        }
        
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        //self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        self.tableView.backgroundColor = UIColor.clear
       
        
        
        
        
        // Do any additional setup after loading the view.
    }
    
    func getCompetitionResults() {
        for age in data {
            if age.0 != "type" {
                let getAge = age.0
                
                var maleArray = [Participant]()
                var femaleArray = [Participant]()
                
                var allData = age.1 as! JSON
                
                for male in allData["males"] as! NSArray{
                    let data = male as! JSON
                    let maleResult = Participant(json: data, id: "")
                    maleArray.append(maleResult)
                }
                
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
                
                for female in allData["females"] as! NSArray {
                    let data = female as! JSON
                    let femaleResult = Participant(json: data, id: "")
                    femaleArray.append(femaleResult)
                }
                
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
        
        self.array = self.array.sorted(by: {Int($0.age)! < Int($1.age)!})
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if controllerType == "" {
            return array[section].maleResults.count + array[section].femaleResults.count + 2
        }
        return self.realTimeArray.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "resultsCell", for: indexPath) as! ResultsTableViewCell
        let title = UILabel()
        title.font = UIFont.boldSystemFont(ofSize: title.font.pointSize)
        
        if controllerType == "" {
            if indexPath.row == 0 {
                cell.cellView.backgroundColor = UIColor.blue
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
                cell.cellView.backgroundColor = UIColor.purple
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
            
            cell.layer.backgroundColor = UIColor.clear.cgColor
            
            
            return cell
        }
        
        cell.cellView.backgroundColor = UIColor.clear
        cell.contentView.backgroundColor = UIColor.clear
        cell.name.text = "\(self.realTimeArray[indexPath.row].firstName) \(self.realTimeArray[indexPath.row].lastName)"
        cell.rankImage.isHidden = true
        cell.score.isHidden = false
        cell.score.text = self.realTimeArray[indexPath.row].score
        
        return cell
        
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        if controllerType == "" {
            return array.count
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let view = UIView()
        if controllerType == "" {
            
            view.backgroundColor = UIColor.black
            let label = UILabel()
            label.text = "גילאי \(array[section].age!)"
            label.textAlignment = .center
            label.font = label.font.withSize(25)
            label.textColor = UIColor.white
            label.frame = CGRect(x: (self.view.frame.width/2)-75, y: 5, width: 150, height: 35)
            view.addSubview(label)
            
        }
        
        return view
        
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 50
    }
  
}
