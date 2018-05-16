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
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var styleNrangeLabel: UILabel!
    @IBOutlet weak var numOfParticipantsLabel: UILabel!
    
    @IBOutlet var joinButtonOutlet: RoundButton!
    @IBOutlet var tempJoinButtonOutlet: RoundButton!
    @IBOutlet var editButtonOutlet: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if(currentUser.type == "coach"){
            let startButton = UIBarButtonItem(title: "התחל תחרות", style: .plain, target: self, action: #selector(goToStart))
            self.navigationItem.rightBarButtonItem = startButton
            joinButtonOutlet.isHidden = true
            tempJoinButtonOutlet.center.x = self.view.center.x
        } else if currentUser.type == "parent" {
            joinButtonOutlet.isHidden = true
            editButtonOutlet.isHidden = true
            tempJoinButtonOutlet.center.x = self.view.center.x
        } else {
            editButtonOutlet.isHidden = true
        }
        

        nameLabel.text = currentCompetition.name
        dateLabel.text = Date().getDate(fullDate: currentCompetition.activityDate)
        styleNrangeLabel.text = "\(currentCompetition.length) מטר \(currentCompetition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(currentCompetition.numOfParticipants)"
        
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")
        self.view.insertSubview(imageView, at: 0)
        
        if userExist() {
            joinButtonOutlet.setTitle("בטל רישום", for: .normal)
            joinButtonOutlet.backgroundColor = UIColor.red
            joinButtonOutlet.tag = 1
        }
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        nameLabel.text = currentCompetition.name
        styleNrangeLabel.text = "\(currentCompetition.length) מטר \(currentCompetition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(currentCompetition.numOfParticipants)"
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToStartCompetition" {
            let nextView = segue.destination as! IterationViewController
            nextView.competition = self.currentCompetition
        }
    }
    
    @objc func goToStart() {
        
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
                
                //let resultsButton = UIBarButtonItem(title: "תוצאות", style: .plain, target: self, action: #selector(self.goToResults))
                //self.navigationItem.rightBarButtonItem = resultsButton
                
                //self.jsonData = response.data
                
            }
            else {
                var competition: Competition!
                let data = response.data
                competition = Competition(json: data, id: self.currentCompetition.getId())
                let iterationView = IterationViewController()
                iterationView.competition = competition
                self.performSegue(withIdentifier: "goToStartCompetition", sender: self)
            }
        })
        
        
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
                        sender.backgroundColor = UIColor.green
                    }
                    else {
                        
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
            sender.setTitle("בטל רישום", for: .normal)
            sender.backgroundColor = UIColor.red
            sender.tag = 1
            
            Service.shared.connectToServer(path: "joinToCompetition", method: .post, params: parameters, completion: { (response) in
                
            })
        }
        
    }
    
    @IBAction func joinTempUserButton(_ sender: UIButton) {
        
        let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "tempRegID") as! TempRegPopUpViewController
        self.addChildViewController(popOverVC)
        popOverVC.view.frame = self.view.frame
        self.view.addSubview(popOverVC.view)
        popOverVC.didMove(toParentViewController: self)
        
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
