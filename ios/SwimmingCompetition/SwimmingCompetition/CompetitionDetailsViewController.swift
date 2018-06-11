//
//  CompetitionDetailsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 20/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Firebase

class CompetitionDetailsViewController: UIViewController {
    
    var currentCompetition: Competition!
    var currentUser: User!
    var jsonData: JSON = [:]
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var styleNrangeLabel: UILabel!
    @IBOutlet weak var numOfParticipantsLabel: UILabel!
    @IBOutlet weak var agesLabel: UILabel!
    
    @IBOutlet var joinButtonOutlet: RoundButton!
    @IBOutlet var tempJoinButtonOutlet: RoundButton!
    @IBOutlet var editButtonOutlet: UIButton!
    @IBOutlet weak var startCompetitionButtonOutlet: RoundButton!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        startCompetitionButtonOutlet.tag = 0
        if(currentUser.type == "coach"){
            joinButtonOutlet.isHidden = true
        } else if currentUser.type == "parent" {
            joinButtonOutlet.isHidden = true
            editButtonOutlet.isHidden = true
            startCompetitionButtonOutlet.isHidden = true
        } else {
            editButtonOutlet.isHidden = true
            startCompetitionButtonOutlet.isHidden = true
        }
        

        nameLabel.text = currentCompetition.name
        dateLabel.text = DateConvert().getDate(fullDate: currentCompetition.activityDate)
        styleNrangeLabel.text = "\(currentCompetition.length) מטר \(currentCompetition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(currentCompetition.numOfParticipants)"
        agesLabel.text = "לגילאי \(currentCompetition.fromAge) עד \(currentCompetition.toAge)"
        
        
        
        if userExist() {
            joinButtonOutlet.setTitle("בטל רישום", for: .normal)
            joinButtonOutlet.tag = 1
        }
        
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")
        self.view.insertSubview(self.backgroundView, at: 0)
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        nameLabel.text = currentCompetition.name
        dateLabel.text = DateConvert().getDate(fullDate: currentCompetition.activityDate)
        styleNrangeLabel.text = "\(currentCompetition.length) מטר \(currentCompetition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(currentCompetition.numOfParticipants)"
        agesLabel.text = "לגילאי \(currentCompetition.fromAge) עד \(currentCompetition.toAge)"
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToStartCompetition" {
            let nextView = segue.destination as! IterationViewController
            nextView.competition = self.currentCompetition
        }
    }
    
    
    //Function to check if the user already sign up to current competition
    func userExist() -> Bool {
        for part in currentCompetition.participants {
            if part.uid == self.currentUser.uid {
                return true
            }
        }
        return false
    }
    
    @IBAction func joinButton(_ sender: UIButton) {
        
        let parameters = [
            "competitionId": self.currentCompetition.id,
            "firstName": self.currentUser.firstName,
            "lastName": self.currentUser.lastName,
            "birthDate": self.currentUser.birthDate,
            "gender": self.currentUser.gender,
            "uid": self.currentUser.uid,
        ] as [String:AnyObject]
        
        if sender.tag == 1 {
            let alert = UIAlertController(title: "ביטול הרשמה לתחרות", message: "האם את/ה בטוח/ה?", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
                
                
                Service.shared.connectToServer(path: "cancelRegistration", method: .post, params: parameters, completion: { (response) in
                    if response.succeed {
                        sender.setTitle("הירשם", for: .normal)
                        for i in 0...self.currentCompetition.participants.count {
                            if self.currentCompetition.participants[i].uid == self.currentUser.uid {
                                self.currentCompetition.participants.remove(at: i)
                                break
                            }
                        }
                        self.present(Alert().confirmAlert(title: "", message: "הרשמה בוטלה"), animated: true, completion: nil)
                    }
                    else {
                        self.present(Alert().confirmAlert(title: "שגיאה", message: "לא בוטלה ההרשמה"), animated: true, completion: nil)
                    }
                })
                alert.dismiss(animated: true, completion: nil)
            }))
            alert.addAction(UIAlertAction(title: "ביטול", style: .default, handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
            }))
            self.present(alert, animated: true, completion: nil)
        }
        
        else {
            
            
            Service.shared.connectToServer(path: "joinToCompetition", method: .post, params: parameters, completion: { (response) in
                if response.succeed {
                    sender.setTitle("בטל רישום", for: .normal)
                    sender.tag = 1
                    var participant : Participant!
                    let data = response.data
                    participant = Participant(json: data, id: data["uid"] as! String)
                    self.currentCompetition.participants.append(participant)
                    self.present(Alert().confirmAlert(title: "", message: "הרשמה בוצעה בהצלחה"), animated: true, completion: nil)
                } else {
                    self.present(Alert().confirmAlert(title: "שגיאה", message: "לא בוצעה הרשמה"), animated: true, completion: nil)
                }
                
            })
        }
        
    }
    
    @IBAction func joinTempUserButton(_ sender: UIButton) {
        let alert = UIAlertController(title: "בחר אפשרות רישום", message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "משתמש זמני", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
            let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "tempRegID") as! TempRegPopUpViewController
            self.addChildViewController(popOverVC)
            popOverVC.view.frame = self.view.frame
            self.view.addSubview(popOverVC.view)
            popOverVC.didMove(toParentViewController: self)
        }))
        alert.addAction(UIAlertAction(title: "משתמש קיים", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
            let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "popUpId") as! PopUpViewController
            self.addChildViewController(popOverVC)
            popOverVC.view.frame = self.view.frame
            popOverVC.currentUser = self.currentUser
            popOverVC.toolBar.items![0].title = "רישום משתמש קיים"
            popOverVC.senderView = self
            self.view.addSubview(popOverVC.view)
            popOverVC.didMove(toParentViewController: self)
        }))
        alert.addAction(UIAlertAction(title: "ביטול", style: .cancel, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
        
    }
    
    @IBAction func editCompetitionButton(_ sender: Any) {
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "addCompetitionID") as! AddCompetitionViewController
        viewController.delegate = self
        viewController.isEdit = true
        viewController.competitionName = currentCompetition.getName()
        viewController.style = currentCompetition.getSwimmingStyle()
        viewController.range = Int(currentCompetition.getLength())!
        viewController.numOfParticipants = currentCompetition.getNumOfParticipants()
        viewController.dateToSend = currentCompetition.getActivityDate()
        viewController.fromAge = Int(currentCompetition.getFromAge())!
        viewController.toAge = Int(currentCompetition.getToAge())!
        viewController.editedCompetitionId = currentCompetition.getId()
        
            
            if let navigator = navigationController {
                navigator.pushViewController(viewController, animated: true)
            }
        
    }
    
    @IBAction func startCompetitionButton(_ sender: UIButton) {
        if sender.tag == 0 {
            print("thissss iss the id")
            print(currentCompetition.getId())
            let param = [
                "competitionId": currentCompetition.getId()
                ] as [String:AnyObject]
            
            Service.shared.connectToServer(path: "initCompetitionForIterations", method: .post, params: param, completion: { (response) in
                if response.data["type"] as? String == "resultsMap" {
                    let alert = UIAlertController(title: nil, message: "תחרות הסתיימה", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                        alert.dismiss(animated: true, completion: nil)
                    }))
                    self.present(alert, animated: true, completion: nil)
                    sender.setTitle("צפה בתוצאות", for: .normal)
                    sender.tag = 1
                    self.jsonData = response.data
                    
                }
                else {
                    var competition: Competition!
                    let data = response.data
                    let sb = UIStoryboard(name: "Main", bundle: nil)
                    competition = Competition(json: data, id: self.currentCompetition.getId())
                    if let iterationView = sb.instantiateViewController(withIdentifier: "iterationId") as? IterationViewController {
                        iterationView.competition = competition
                        self.navigationController?.pushViewController(iterationView, animated: true)
                    }
                }
            })
        } else {
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let resultsView = sb.instantiateViewController(withIdentifier: "resultsId") as? PersonalResultsViewController {
                resultsView.data = self.jsonData
                self.navigationController?.pushViewController(resultsView, animated: true)
            }
        }
        
        
    }
}

extension CompetitionDetailsViewController: dataProtocol {
    func dataSelected(name: String, activityDate: String, swimmingStyle: String, length: String, numOfParticipants: String, fromAge: String, toAge: String) {
        self.currentCompetition.setName(name: name)
        self.currentCompetition.setActivityDate(activityDate: activityDate)
        self.currentCompetition.setSwimmingStyle(swimmingStyle: swimmingStyle)
        self.currentCompetition.setLength(length: length)
        self.currentCompetition.setNumOfParticipants(numOfParticipants: numOfParticipants)
        self.currentCompetition.setFromAge(fromAge: fromAge)
        self.currentCompetition.setToAge(toAge: toAge)
    }
    
    
    
    
}
