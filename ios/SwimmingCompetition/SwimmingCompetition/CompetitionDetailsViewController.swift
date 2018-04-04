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
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var styleNrangeLabel: UILabel!
    @IBOutlet weak var numOfParticipantsLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let startButton = UIBarButtonItem(title: "התחל תחרות", style: .plain, target: self, action: #selector(goToStart))
        self.navigationItem.rightBarButtonItem = startButton
        nameLabel.text = competition.name
        //dateLabel.text = "\(competition.activityDate) ביום \(getDay())"
        dateLabel.text = Date().getDate(fullDate: competition.activityDate)
        styleNrangeLabel.text = "\(competition.length) מטר \(competition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(competition.numOfParticipants)"
        
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
       // navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        
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
        
        
    }
    
    @IBAction func joinTempUserButton(_ sender: UIButton) {
        
        let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "tempRegID") as! TempRegPopUpViewController
        self.addChildViewController(popOverVC)
        popOverVC.view.frame = self.view.frame
        self.view.addSubview(popOverVC.view)
        popOverVC.didMove(toParentViewController: self)
        
    }
    

}
