//
//  IterationViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 20/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import SwiftyJSON

class IterationViewController: UIViewController {

    var subviews: [UIView] = []
    
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var resetButtonOutlet: UIButton!
    @IBOutlet weak var endIterationButtonOutlet: UIButton!
    
    
    var competition: Competition!
    var participantsIndex = [Int]()
    var buttonsArray = [UIButton!]()
    var labelsArray = [UILabel!]()
    
    var timer = Timer()
    var validTimer = false
    var minutes: Int = 0
    var seconds: Int = 0
    var fractions: Int = 0
    
    
    var iterationNumber:Int = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        iterationNumber = Int(competition.numOfParticipants)!
    
        startNewIteration()
        
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @IBAction func endIterationButton(_ sender: Any) {
        startNewIteration()
    }
    
    @IBAction func startButton(_ sender: UIButton) {
        if !validTimer {
            validTimer = true
            sender.backgroundColor = .red
            sender.setTitle("עצור", for: .normal)

            timer = Timer.scheduledTimer(timeInterval: 0.01, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: true)
            resetButtonOutlet.isEnabled = false
        }
        else {
            validTimer = false
            timer.invalidate()
            sender.backgroundColor = .green
            sender.setTitle("המשך", for: .normal)
            resetButtonOutlet.isEnabled = true
            
            
        }
        
    }
    
    @IBAction func resetButton(_ sender: Any) {
        validTimer = false
        minutes = 0
        seconds = 0
        fractions = 0
        self.timeLabel.text = "00:00:00"
    }
    
    @objc func updateTimer() {
        fractions += 1
        if fractions == 100 {
            seconds += 1
            fractions = 0
        }
        if seconds == 60 {
            minutes += 1
            seconds = 0
        }
        
        let fractionsString = fractions > 9 ? "\(fractions)" : "0\(fractions)"
        let secondsString = seconds > 9 ? "\(seconds)" : "0\(seconds)"
        let minutesString = minutes > 9 ? "\(minutes)" : "0\(minutes)"
        
        self.timeLabel.text = "\(minutesString):\(secondsString):\(fractionsString)"
    }
    
    func createButtonsLabels() {
        let width: Int = (Int(self.view.frame.width)/iterationNumber) - 10
        var start:Int = 0
        for i in 0...iterationNumber-1 {
            let name = UILabel(frame: CGRect(x: start, y: 400, width: width, height: 30))
            name.text = "מתחרה \(i+1)"
            name.textAlignment = .center
            
            let time = UILabel(frame: CGRect(x: start, y: 440, width: width, height: 30))
            time.text = "00:00:00"
            time.textAlignment = .center
            time.tag = i
            time.isUserInteractionEnabled = true
            let gestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped))
            time.addGestureRecognizer(gestureRecognizer)
            self.view.addSubview(time)
            
            let button = UIButton(frame: CGRect(x: start, y: 500, width: width, height: 50))
            button.backgroundColor = .red
            button.tag = i
            button.setTitle("עצור", for: .normal)
            button.addTarget(self, action: #selector(stopUserTimeButton), for: .touchUpInside)
            button.isEnabled = false
            self.view.addSubview(button)
            print("hetttttttttt")
            print(participantsIndex.count)
            print(i)
            if participantsIndex.count > i {
                name.text = self.competition.participants[participantsIndex[i]].firstName
                time.tag = participantsIndex[i]
                button.tag = participantsIndex[i]
                button.isEnabled = true
            }
            self.view.addSubview(name)
            subviews.append(name)
            subviews.append(time)
            subviews.append(button)
            labelsArray.append(time)
            buttonsArray.append(button)
            
            start += width + 10
            
        }
    }
    
    @objc func stopUserTimeButton(sender: UIButton!) {
        self.labelsArray[sender.tag].text = "\(self.timeLabel.text!)"
        competition.participants[sender.tag].competed = "1"
        competition.participants[sender.tag].score = "\(self.timeLabel.text!)"
        //sender.isEnabled = false
        var i = 0
        for button in buttonsArray {
            if (button?.isEnabled)! == false {
                i+=1
            }
        }
        if i == iterationNumber {
            self.endIterationButtonOutlet.isEnabled = true
        }
    }
    
    @objc func labelTapped(gesture : UITapGestureRecognizer) {
        let id = gesture.view!.tag
        let alert = UIAlertController(title: "זמן של מתחרה \(id+1)", message: "\(labelsArray[id].text!)", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    func startNewIteration() {
        if participantsIndex.count != 0 {
            iterationIsDone()
        }
        
        participantsIndex.removeAll()
        print("participant Index")
        print(participantsIndex)
        for (index, part) in self.competition.participants.enumerated() {
            if self.participantsIndex.count == iterationNumber {
                break
            }
            
            if (part.competed == "0" || part.competed == "false") {
                self.participantsIndex.append(index)
            }
        }
        
        for view in subviews {
            view.removeFromSuperview()
        }
        subviews.removeAll()
        print(participantsIndex)
        createButtonsLabels()
        print("========start==========")
        /*let a:JSON = ["competition":competition] as Dictionary<String,Any>
        Service.shared.connectToServer(path: "setCompetitionResults", method: .post, params: a as [String : AnyObject]) { (f) in
            print(f)
        }*/
        
       /* let send = ["competiton": ["id":"eeee", "numOfParticipants":"2", "activityDate":"2/3/12", "name":"ff",
        "fromAge":"1", "toAge":"3", "lengh":"20", "swimmingStyle": "ff", "participants":["22": ["score":"1.2", "id":"22", "gender":"ff","lastName":"ggg","birthDate":"1/2/13","competed":true,"firstName":"rrr"]]]] as [String:AnyObject]
        
      
       
        Service.shared.connectToServer(path: "setCompetitionResults", method: .post, params: send) { (a) in
            print(a)
        }*/
        print("========end==========")
        
    }
    func iterationIsDone() {
        
        var sendString = "{\"id\":\"\(self.competition.id)\",\"participants\":\"{"
        
        for i in 0...self.participantsIndex.count-1 {
            print("im here")
            print(participantsIndex.count)
            let id = self.competition.participants[participantsIndex[i]].uid
            let firstName = self.competition.participants[participantsIndex[i]].firstName
            let lastName = self.competition.participants[participantsIndex[i]].lastName
            let birthDate = self.competition.participants[participantsIndex[i]].birthDate
            let gender = self.competition.participants[participantsIndex[i]].gender
            let score = self.competition.participants[participantsIndex[i]].score
            let competed = self.competition.participants[participantsIndex[i]].competed
            
            sendString += "\\\"\(id)\\\":\\\"{\\\\\\\"firstName\\\\\\\":\\\\\\\"\(firstName)\\\\\\\",\\\\\\\"lastName\\\\\\\":\\\\\\\"\(lastName)\\\\\\\",\\\\\\\"birthDate\\\\\\\":\\\\\\\"\(birthDate)\\\\\\\",\\\\\\\"gender\\\\\\\":\\\\\\\"\(gender)\\\\\\\",\\\\\\\"score\\\\\\\":\\\\\\\"\(score)\\\\\\\",\\\\\\\"id\\\\\\\":\\\\\\\"\(id)\\\\\\\",\\\\\\\"competed\\\\\\\":\\\\\\\"\(competed)\\\\\\\"}\\\""
            if i != self.participantsIndex.count-1 {
                sendString += ","
            }
        }
        
        sendString += "}\"}" as String
        
        
        let param = ["competition":sendString] as [String:AnyObject]
        
        
        Service.shared.connectToServer(path: "setCompetitionResults", method: .post, params: param) { (a) in
            print(a)
        }
    }
   
    
}
