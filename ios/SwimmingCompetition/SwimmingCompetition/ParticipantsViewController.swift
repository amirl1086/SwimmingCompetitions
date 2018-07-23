//
//  ParticipantsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 13/07/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Firebase
import SwiftSpinner

protocol participantProtocol {
    func setParticipants(participants:[Participant])
}

class ParticipantsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var delegate: participantProtocol?

    var partcipantsList = [Participant]()
    var maleParticipants = [Participant]()
    var femaleParticipants = [Participant]()
    
    var chosenParticipants = [Participant]()
    
    var currentCompetition: Competition!
    
    @IBOutlet weak var tableView: UITableView!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        self.title = "בחר מתחרים"
        let confirmButton = UIBarButtonItem(title: "אישור", style: .plain, target: self, action: #selector(self.confirm))
        self.navigationItem.rightBarButtonItem = confirmButton
        SwiftSpinner.show("אנא המתן...")
        Database.database().reference().child("competitions/\(currentCompetition.getId())/participants").observe(.childAdded) { (snapshot) in
            SwiftSpinner.hide()
            let data = snapshot.value as! JSON
            let participant = Participant(json: data, id: "")
            if(!participant.competed) {
                if participant.gender == "male" {
                    self.maleParticipants.append(participant)
                } else {
                    self.femaleParticipants.append(participant)
                }
                //self.partcipantsList.append(participant)
            }
            
            let formatDate = DateFormatter()
            formatDate.dateFormat = "dd/MM/yyyy"
            self.maleParticipants.sort(by: {(formatDate.date(from:$0.birthDate) != nil ? formatDate.date(from:$0.birthDate)! : Date()) < (formatDate.date(from:$1.birthDate) != nil ? formatDate.date(from:$1.birthDate)! : Date())})
            self.femaleParticipants.sort(by: {(formatDate.date(from:$0.birthDate) != nil ? formatDate.date(from:$0.birthDate)! : Date()) < (formatDate.date(from:$1.birthDate) != nil ? formatDate.date(from:$1.birthDate)! : Date())})
            self.tableView.reloadData()
        }
        
        
        tableView.separatorStyle = .singleLine
        self.tableView.backgroundColor = UIColor.clear
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0{
            return self.maleParticipants.count
        } else {
            return self.femaleParticipants.count

        }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        
        return 2
        
    }
    
    /* the sctions. ages - for the results view. competition name - for the real time view */
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let view = UIView()
        view.backgroundColor = UIColor.black
        let label = UILabel()
        if section == 0 {
            label.text = "בנים"
        } else {
            label.text = "בנות"
        }
        
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
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! ParticipantsTableViewCell
        if indexPath.section == 0 {
            cell.label.text = "\(maleParticipants[indexPath.row].birthDate)  |  \(maleParticipants[indexPath.row].lastName) \(maleParticipants[indexPath.row].firstName)"
        } else {
            cell.label.text = "\(femaleParticipants[indexPath.row].birthDate)  |  \(femaleParticipants[indexPath.row].lastName) \(femaleParticipants[indexPath.row].firstName)"
        }
        
        cell.layer.backgroundColor = UIColor.clear.cgColor
        cell.contentView.backgroundColor = UIColor.clear
        cell.backgroundColor = UIColor.clear
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if tableView.cellForRow(at: indexPath)?.accessoryType == UITableViewCellAccessoryType.checkmark {
            tableView.cellForRow(at: indexPath)?.accessoryType = UITableViewCellAccessoryType.none
            
            for i in 0..<self.chosenParticipants.count {
                if indexPath.section == 0 {
                    if self.maleParticipants[indexPath.row].uid == self.chosenParticipants[i].uid {
                        self.chosenParticipants.remove(at: i)
                        break
                    }
                } else {
                    if self.femaleParticipants[indexPath.row].uid == self.chosenParticipants[i].uid {
                        self.chosenParticipants.remove(at: i)
                        break
                    }
                }
                
            }
        } else {
            
            if self.chosenParticipants.count < Int(self.currentCompetition.getNumOfParticipants())! {
                tableView.cellForRow(at: indexPath)?.accessoryType = UITableViewCellAccessoryType.checkmark
                if indexPath.section == 0 {
                    self.chosenParticipants.append(self.maleParticipants[indexPath.row])
                } else {
                    self.chosenParticipants.append(self.femaleParticipants[indexPath.row])
                }
                
            }
            else {
                self.present(Alert().confirmAlert(title: "", message: "אין אפשרות לבחור יותר מ \(self.currentCompetition.getNumOfParticipants()) משתתפים"), animated: true, completion: nil)
            }
        }
        
        
    
    }
    
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        
    }
    
    @objc func confirm() {
        if self.chosenParticipants.count == 0 {
            self.present(Alert().confirmAlert(title: "", message: "חובה לבחור לפחות משתתף אחד"), animated: true, completion: nil)
        } else {
            self.delegate?.setParticipants(participants: self.chosenParticipants)
            _ = self.navigationController?.popViewController(animated: true)
        }
        
    }
    

}
