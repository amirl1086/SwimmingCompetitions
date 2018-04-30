//
//  CompetitionDetailsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 20/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class CompetitionDetailsViewController: UIViewController {
    
    var competition: Competition!
    var currentUser: User!
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var styleNrangeLabel: UILabel!
    @IBOutlet weak var numOfParticipantsLabel: UILabel!
    @IBOutlet var joinButtonOutlet: RoundButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let startButton = UIBarButtonItem(title: "התחל תחרות", style: .plain, target: self, action: #selector(goToStart))
        self.navigationItem.rightBarButtonItem = startButton
        nameLabel.text = competition.name
        //dateLabel.text = "\(competition.activityDate) ביום \(getDay())"
        dateLabel.text = Date().getDate(fullDate: competition.activityDate)
        styleNrangeLabel.text = "\(competition.length) מטר \(competition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(competition.numOfParticipants)"
        
        //self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
       // navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        
        var exist = false
        for part in competition.participants {
            if part.uid == self.currentUser.uid {
                exist = true
                break
            }
        }
        
        if exist {
            joinButtonOutlet.setTitle("בטל רישום", for: .normal)
            joinButtonOutlet.backgroundColor = UIColor.red
            joinButtonOutlet.tag = 1
        }
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        nameLabel.text = competition.name
        //dateLabel.text = "\(competition.activityDate) ביום \(getDay())"
        //dateLabel.text = Date().getDate(fullDate: competition.activityDate)
        styleNrangeLabel.text = "\(competition.length) מטר \(competition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(competition.numOfParticipants)"
    }
    
    @objc func goToStart() {
        performSegue(withIdentifier: "goToStartCompetition", sender: self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToStartCompetition" {
            let nextView = segue.destination as! IterationViewController
            nextView.competition = self.competition
        }
    }
    
    @IBAction func joinButton(_ sender: UIButton) {
        
        let parameters = [
            "competitionId": self.competition.id,
            "firstName": self.currentUser.firstName,
            "lastName": self.currentUser.lastName,
            "birthDate": self.currentUser.birthDate,
            "gender": self.currentUser.gender,
            "uid": self.currentUser.uid,
        ] as [String:AnyObject]
        
        if sender.tag == 1 {
            sender.setTitle("הירשם", for: .normal)
            sender.backgroundColor = UIColor.green
            
            Service.shared.connectToServer(path: "cancelRegistration", method: .post, params: parameters, completion: { (response) in
                
            })
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
        viewController.competitionName = competition.getName()
        viewController.style = competition.getSwimmingStyle()
        viewController.range = Int(competition.getLength())!
        viewController.numOfParticipants = competition.getNumOfParticipants()
        viewController.dateToSend = competition.getActivityDate()
        viewController.fromAge = Int(competition.getFromAge())!
        viewController.toAge = Int(competition.getToAge())!
        viewController.editedCompetitionId = competition.getId()
        
            print(competition.getName())
            print(competition.getSwimmingStyle())
            print(competition.getNumOfParticipants())
            if let navigator = navigationController {
                navigator.pushViewController(viewController, animated: true)
            }
        
    }
    
}

extension CompetitionDetailsViewController: dataProtocol {
    func dataSelected(name: String, activityDate: String, swimmingStyle: String, length: String, numOfParticipants: String, fromAge: String, toAge: String) {
        self.competition.setName(name: name)
        self.competition.setActivityDate(activityDate: activityDate)
        self.competition.setSwimmingStyle(swimmingStyle: swimmingStyle)
        self.competition.setLength(length: length)
        self.competition.setNumOfParticipants(numOfParticipants: numOfParticipants)
        self.competition.setFromAge(fromAge: fromAge)
        self.competition.setToAge(toAge: toAge)
    }
    
    
    
    
}
