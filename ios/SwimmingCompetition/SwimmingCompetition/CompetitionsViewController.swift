//
//  CompetitionsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 10/01/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class CompetitionsViewController: UIViewController {
    
    var currenUser: User!
    var competitions = [Competition]()
    var controllerType = ""
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        
        if controllerType == "" {
            addButtonView()
        }
        
        getCompetitionsData()
        
        let imageView = UIImageView(frame: self.view.bounds)
        imageView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(imageView, at: 0)
        
        self.tableView.backgroundColor = UIColor.clear
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getCompetitionsData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToCompetitionDetails" {
            let nextView = segue.destination as! CompetitionDetailsViewController
            let competition = sender as? Competition
            nextView.currentCompetition = competition
            nextView.currentUser = self.currenUser
        }
    }
    
    func addButtonView() {
        if(currenUser.type == "coach") {
            let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(addCompetition))
            self.navigationItem.rightBarButtonItem = addButton
        }
        
        let addButton = UIBarButtonItem(title: "edit", style: .done, target: self, action: #selector(addCompetition(_:)))
        self.navigationItem.rightBarButtonItem = addButton
    }
    
    @objc func addCompetition(_ sender: UIBarButtonItem) {
        self.performSegue(withIdentifier: "goToAddCompetition", sender: self)
    }
    
    func getCompetitionsData() {
        
        /*var alert: UIAlertView = UIAlertView(title: "טוען תחרויות", message: "אנא המתן...", delegate: nil, cancelButtonTitle: nil);
        
        
        let loadingIndicator: UIActivityIndicatorView = UIActivityIndicatorView(frame: CGRect(x: 50, y: 10, width: 37, height: 37)) as UIActivityIndicatorView
        loadingIndicator.center = self.view.center;
        loadingIndicator.hidesWhenStopped = true
        loadingIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
        loadingIndicator.startAnimating();
        
        alert.setValue(loadingIndicator, forKey: "accessoryView")
        loadingIndicator.startAnimating()
        
        alert.show();*/
        
        let parameters = [
            "currentUser": [
                "uid":currenUser.uid
            ]
        ] as [String: AnyObject]
     
        Service.shared.connectToServer(path: "getCompetitions", method: .post, params: parameters) { (response) in
            var compArray = [Competition]()
            
            for data in response.data {
                var competition : Competition!
                let compData = response.data[data.0] as! JSON
                competition = Competition(json: compData, id: data.0)
                compArray.append(competition)
            }
            let formatDate = DateFormatter()
            formatDate.dateFormat = "dd/MM/yyyy HH:mm"
            self.competitions = compArray
            self.competitions.sort(by: {formatDate.date(from:$0.activityDate)! > formatDate.date(from:$1.activityDate)!})
            
            self.tableView.reloadData()
            //alert.dismiss(withClickedButtonIndex: -1, animated: true)
            
        }
    }
    
  
    
    
}

extension CompetitionsViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return competitions.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "competitionCell", for: indexPath) as! CompetitionTableViewCell
        cell.name.text = competitions[indexPath.row].name
        cell.date.text = "מתקיימת בתאריך \(Date().getDate(fullDate: competitions[indexPath.row].activityDate)) ביום \(Date().getWeekDay(fullDate: competitions[indexPath.row].activityDate))"

        cell.time.text = "בשעה \(Date().getHour(fullDate: competitions[indexPath.row].activityDate))"
        cell.ages.text = "לגילאי \(competitions[indexPath.row].fromAge) עד \(competitions[indexPath.row].toAge)"
        
        cell.layer.backgroundColor = UIColor.clear.cgColor
        cell.contentView.backgroundColor = UIColor.clear
        //cell.cellView.layer.cornerRadius = cell.cellView.frame.height/4
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 230
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if controllerType == "realTime" {
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let resultsView = sb.instantiateViewController(withIdentifier: "resultsId") as? PersonalResultsViewController {
                resultsView.controllerType = "realTime"
                resultsView.competition = competitions[indexPath.row]
                self.navigationController?.pushViewController(resultsView, animated: true)
            }
        } else {
            performSegue(withIdentifier: "goToCompetitionDetails", sender: competitions[indexPath.row])
            tableView.deselectRow(at: indexPath, animated: true)
        }
        
    }
  
    
}

class cellTableCompetition: UITableViewCell {
    
    	
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
