//
//  IterationViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 20/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class IterationViewController: UIViewController {

    @IBOutlet weak var timeLabel: UILabel!
    
    var competition: Competition!
    var buttonsArray = [UIButton!]()
    var labelsArray = [UILabel!]()
    
    var timer = Timer()
    var minutes: Int = 0
    var seconds: Int = 0
    var fractions: Int = 0
    
    var iterationNumber:Int = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        iterationNumber = Int(competition.numOfParticipants)!
        createButtonsLabels()
        // Do any additional setup after loading the view.
        
        self.view.backgroundColor = UIColor(patternImage: UIImage(named: "poolImage.jpg")!)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
   
    @IBAction func startButton(_ sender: UIButton) {
        for i in 0...iterationNumber-1 {
            buttonsArray[i].isEnabled = true
        }
        timer = Timer.scheduledTimer(timeInterval: 0.01, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: true)
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
            self.view.addSubview(name)
            let time = UILabel(frame: CGRect(x: start, y: 440, width: width, height: 30))
            time.text = "00:00:00"
            time.textAlignment = .center
            time.tag = i
            time.isUserInteractionEnabled = true
            let gestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(labelTapped))
            time.addGestureRecognizer(gestureRecognizer)
            self.view.addSubview(time)
            labelsArray.append(time)
            let button = UIButton(frame: CGRect(x: start, y: 500, width: width, height: 50))
            button.backgroundColor = .red
            button.tag = i
            button.setTitle("עצור", for: .normal)
            button.addTarget(self, action: #selector(d), for: .touchUpInside)
            button.isEnabled = false
            self.view.addSubview(button)
            buttonsArray.append(button)

            start += width + 10
        }
    }
    
    @objc func d(sender: UIButton!) {
        self.labelsArray[sender.tag].text = "\(self.timeLabel.text!)"
        sender.isEnabled = false
    }
    
    @objc func labelTapped(gesture : UITapGestureRecognizer) {
        let id = gesture.view!.tag
        let alert = UIAlertController(title: "זמן של מתחרה \(id+1)", message: "\(labelsArray[id].text!)", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "סגור", style: .default, handler: { (action) in
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
}
