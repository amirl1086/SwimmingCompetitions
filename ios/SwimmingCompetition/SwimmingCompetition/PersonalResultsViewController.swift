//
//  PersonalResultsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 17/03/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class PersonalResultsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
  
    struct Result {
        var age: String!
        var maleResults:[Participant]!
        var femaleResults:[Participant]!
    }

    var array = [Result]()
    var data:JSON = [:]
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        tableView.allowsSelection = false
        
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        //self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        self.tableView.backgroundColor = UIColor.clear
        print(data)
        for age in data {
            print(age.0)
        }
        for age in data {
            if age.0 != "type" {
                
                print(age.0)
                let getAge = age.0
                var maleArray = [Participant]()
                var femaleArray = [Participant]()
                
                var d = age.1 as! JSON
                print(d["males"] as! NSArray)
                for male in d["males"] as! NSArray{
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
                
                for female in d["females"] as! NSArray {
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
        print(self.array)
        var a = Int(self.array[0].age)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return array[section].maleResults.count + array[section].femaleResults.count + 2
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "resultsCell", for: indexPath) as! ResultsTableViewCell
        let title = UILabel()
        title.font = UIFont.boldSystemFont(ofSize: title.font.pointSize)
        
        if indexPath.row == 0 {
            cell.cellView.backgroundColor = UIColor.gray
            title.text = "בנים"
            cell.name.text = title.text
            cell.rankImage.isHidden = true
            cell.score.isHidden = true
            //cell.cellView.layer.cornerRadius = cell.cellView.frame.height/2
            
        }
        else if indexPath.row-1 < array[indexPath.section].maleResults.count {
            cell.cellView.backgroundColor = UIColor.clear
            cell.name.text = "\(array[indexPath.section].maleResults[indexPath.row-1].lastName) \(array[indexPath.section].maleResults[indexPath.row-1].firstName)"
            cell.score.text = array[indexPath.section].maleResults[indexPath.row-1].score
            cell.rankImage.image = UIImage(named: "\(array[indexPath.section].maleResults[indexPath.row-1].rank).png")
            cell.rankImage.isHidden = false
            cell.score.isHidden = false
            cell.contentView.backgroundColor = UIColor.clear
            
        }
        else if indexPath.row-1 == array[indexPath.section].maleResults.count {
            cell.cellView.backgroundColor = UIColor.gray
            title.text = "בנות"
            cell.name.text = title.text
            cell.rankImage.isHidden = true
            cell.score.isHidden = true
            
            //cell.cellView.layer.cornerRadius = cell.cellView.frame.height/2
            //cell.name.font = cell.name.font.withSize(20)
        }
        else {
            cell.cellView.backgroundColor = UIColor.clear
            cell.name.text = "\(array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].lastName) \(array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].firstName)"
            cell.score.text = array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].score
            cell.rankImage.image = UIImage(named: "\(array[indexPath.section].femaleResults[indexPath.row-array[indexPath.section].maleResults.count-2].rank).png")
            cell.rankImage.isHidden = false
            cell.score.isHidden = false
            cell.contentView.backgroundColor = UIColor.clear
            
        }
        
        cell.layer.backgroundColor = UIColor.clear.cgColor
        
        
        return cell
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return array.count
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
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
  
}
