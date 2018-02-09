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
    var joined = false
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
        styleNrangeLabel.text = "\(competition.length) מטר \(competition.swimmingStyle)"
        numOfParticipantsLabel.text = "\(competition.numOfParticipants)"
        
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        
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
            "firstName": "aviel",
            "lastName": "sh",
            "birthDate": "11",
            "gender": "male",
            "competed": false,
            "score": 0
            ] as [String : Any]
        
        Service.shared.connectToServer(path: "joinToCompetition", method: .post, params: parameters) { (response) in
            print(response.data)
        }
        if !joined {
            self.joined = true
            sender.setTitle("בטל רישום", for: .normal)
            
        }
        else {
            let alert = UIAlertController(title: "ביטול רישום", message: "?האם את/ה בטוח/ה", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "כן", style: .default, handler: { (action) in
                sender.setTitle("רישום", for: .normal)
                self.joined = false
                alert.dismiss(animated: true, completion: nil)
            }))
            alert.addAction(UIAlertAction(title: "לא", style: .default, handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
            }))
            self.present(alert, animated: true, completion: nil)
            
        }
        
    }
    
    @IBAction func joinTempUserButton(_ sender: UIButton) {
        let alert = UIAlertController(title: "רישום משתמש זמני", message: nil, preferredStyle: .alert)
        alert.addTextField { (firstName) in
            firstName.placeholder = "שם פרטי"
            firstName.textAlignment = .center
        }
        alert.addTextField { (lastName) in
            lastName.placeholder = "שם משפחה"
            lastName.textAlignment = .center
        }
        alert.addTextField { (birthDate) in
            birthDate.placeholder = "תאריך לידה"
            birthDate.textAlignment = .center
        }
        alert.addTextField { (gender) in
            gender.placeholder = "מין"
            gender.textAlignment = .center
        }
        alert.addAction(UIAlertAction(title: "אישור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "ביטול", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
        
    }
    

}