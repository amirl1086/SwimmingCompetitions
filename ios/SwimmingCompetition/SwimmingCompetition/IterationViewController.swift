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
    
    @IBOutlet weak var startButtonOutlet: RoundButton!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var resetButtonOutlet: UIButton!
    @IBOutlet weak var endIterationButtonOutlet: UIButton!
  
    @IBOutlet weak var scrollView: UIScrollView!
    
    var competition: Competition!
    var jsonData: JSON = [:]
    
    var participantsIndex = [Int]()
    var buttonsArray = [UIButton?]()
    var timesArray = [UILabel?]()
    var namesArray = [UILabel?]()
    
    var timer = Timer()
    var validTimer = false
    var minutes: Int = 0
    var seconds: Int = 0
    var fractions: Int = 0
    var userTime: String = ""
    
    var iterationNumber:Int = 0
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        iterationNumber = Int(competition.numOfParticipants)!
      
        startNewIteration()
    
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "iteration_screen.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
       
        //navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidLayoutSubviews() {
        backgroundView.frame = self.view.bounds
        
        let width: Int = (Int(self.view.frame.width)/iterationNumber) - 10
        var start: Int = 0
        var count = 1
        
        for view in subviews {
            view.frame = CGRect(x: start, y: Int(view.frame.origin.y), width: width, height: 30)
            
            if count == 3 {
                start += width + 10
                count = 1
            } else {
                count += 1
            }
        }
    }
    
    @IBAction func endIterationButton(_ sender: Any) {
        timer.invalidate()
        resetButton(sender)
        iterationIsDone()
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
        
        let asSeconds = minutes+seconds
        
        self.userTime = "\(asSeconds).\(fractions)"
        self.timeLabel.text = "\(minutesString):\(secondsString):\(fractionsString)"
    }
    
    @IBAction func resetButton(_ sender: Any) {
        startButtonOutlet.setTitle("התחל", for: .normal)
        startButtonOutlet.backgroundColor = .green
        validTimer = false
        minutes = 0
        seconds = 0
        fractions = 0
        self.timeLabel.text = "00:00:00"
    }
    
    
    
    func createButtonsLabels() {
        let width: Int = (Int(self.view.frame.width)/self.competition.currentParticipants.count) - 10
        var start:Int = 0
        for i in 0...self.competition.currentParticipants.count-1 {
            let name = UILabel(frame: CGRect(x: start, y: Int(self.resetButtonOutlet.frame.origin.y + self.resetButtonOutlet.frame.height + 20), width: width, height: 30))
            name.text = "\(self.competition.currentParticipants[i].firstName) \(self.competition.currentParticipants[i].lastName)"
            name.textAlignment = .center
            self.scrollView.addSubview(name)
            
            let time = UILabel(frame: CGRect(x: start, y: Int(name.frame.origin.y+name.frame.height+10), width: width, height: 30))
            time.text = "00:00:00"
            time.textAlignment = .center
            time.tag = i
            time.isUserInteractionEnabled = true
            let gestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped))
            time.addGestureRecognizer(gestureRecognizer)
            self.scrollView.addSubview(time)
            
            let button = UIButton(frame: CGRect(x: start, y: Int(time.frame.origin.y+time.frame.height+10), width: width, height: 50))
            button.backgroundColor = .red
            button.tag = i
            button.setTitle("עצור", for: .normal)
            button.addTarget(self, action: #selector(stopUserTimeButton), for: .touchUpInside)
            self.scrollView.addSubview(button)
            
            
            subviews.append(name)
            subviews.append(time)
            subviews.append(button)
            namesArray.append(name)
            timesArray.append(time)
            buttonsArray.append(button)
            
            start += width + 10
            
        }
    }
    
    @objc func stopUserTimeButton(sender: UIButton!) {
        
        self.timesArray[sender.tag]?.text = "\(self.timeLabel.text!)"
        
        competition.currentParticipants[sender.tag].setCompeted(competed: true)
        competition.currentParticipants[sender.tag].score = self.userTime
        
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
        let alert = UIAlertController(title: "\(String(describing: namesArray[id]!.text!))", message: "\(String(describing: timesArray[id]!.text!))", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    func startNewIteration() {
        
        namesArray.removeAll()
        timesArray.removeAll()
       
        
        for view in subviews {
            view.removeFromSuperview()
        }
        subviews.removeAll()
        
        if self.competition.currentParticipants.count != 0 {
            createButtonsLabels()
        }
        

    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToCompetitionResults" {
            let nextView = segue.destination as! PersonalResultsViewController
            let result = self.jsonData
            nextView.data = result
        }
    }
    
    func iterationIsDone() {
    
        var sendString = "{\"id\":\"\(self.competition.id)\",\"numOfParticipants\":\"\(self.competition.numOfParticipants)\",\"activityDate\":\"\(self.competition.activityDate)\",\"name\":\"\(self.competition.name)\",\"fromAge\":\"\(self.competition.fromAge)\",\"length\":\"\(self.competition.length)\",\"swimmingStyle\":\"\(self.competition.swimmingStyle)\",\"toAge\":\"\(self.competition.toAge)\",\"currentParticipants\":\"{"
        
        for i in 0..<self.self.competition.currentParticipants.count {
            let id = self.competition.currentParticipants[i].uid
            let firstName = self.competition.currentParticipants[i].firstName
            let lastName = self.competition.currentParticipants[i].lastName
            let birthDate = self.competition.currentParticipants[i].birthDate
            let gender = self.competition.currentParticipants[i].gender
            let score = self.competition.currentParticipants[i].score
            let competed = self.competition.currentParticipants[i].competed
            
            sendString += "\\\"\(id)\\\":{\\\"firstName\\\":\\\"\(firstName)\\\",\\\"lastName\\\":\\\"\(lastName)\\\",\\\"birthDate\\\":\\\"\(birthDate)\\\",\\\"gender\\\":\\\"\(gender)\\\",\\\"score\\\":\\\"\(score)\\\",\\\"uid\\\":\\\"\(id)\\\",\\\"competed\\\":\\\"\(competed)\\\"}"
            if i != self.competition.currentParticipants.count-1 {
                sendString += ","
            }
        }
        
        var participantsString = ""
        for participants in competition.participants {
            participantsString += "\\\"\(participants.uid)\\\":{\\\"birthDate\\\":\\\"\(participants.birthDate)\\\",\\\"firstName\\\":\\\"\(participants.firstName)\\\",\\\"lastName\\\":\\\"\(participants.lastName)\\\",\\\"gender\\\":\\\"\(participants.gender)\\\",\\\"score\\\":\\\"\(participants.score)\\\",\\\"uid\\\":\\\"\(participants.uid)\\\",\\\"competed\\\":\\\"\(participants.competed)\\\"}"
                        if participants.uid != competition.participants.last?.uid {
                participantsString += ","
            }
        }
        
        sendString += "}\",\"participants\":\"{\(participantsString)}\"}"
        
        let param = ["competition":sendString] as [String:AnyObject]
        
        
        Service.shared.connectToServer(path: "setCompetitionResults", method: .post, params: param) { (response) in
            if response.data["type"] as? String == "resultsMap" {
                let alert = UIAlertController(title: nil, message: "תחרות הסתיימה", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
                    alert.dismiss(animated: true, completion: nil)
                }))
                self.present(alert, animated: true, completion: nil)
                
                let resultsButton = UIBarButtonItem(title: "תוצאות", style: .plain, target: self, action: #selector(self.goToResults))
                self.navigationItem.rightBarButtonItem = resultsButton
                self.startNewIteration()
                self.jsonData = response.data
                
            } else {
                var competition: Competition!
                let data = response.data
                competition = Competition(json: data, id: self.competition.getId())
                self.competition = competition
                self.startNewIteration()
            }
        }
    }
    
    @objc func goToResults() {
        performSegue(withIdentifier: "goToCompetitionResults", sender: self)
    }
    
}

