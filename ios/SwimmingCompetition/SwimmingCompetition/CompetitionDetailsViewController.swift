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
        dateLabel.text = competition.activityDate
        styleNrangeLabel.text = "\(competition.swimmingStyle) מטר \(competition.length)"
        numOfParticipantsLabel.text = "\(competition.numOfParticipants)"
    }
    
    @objc func goToStart() {
        performSegue(withIdentifier: "goToStartCompetition", sender: self)
    }

}
