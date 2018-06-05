//
//  StatisticsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 03/06/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import Charts

class StatisticsViewController: UIViewController {

    struct statisticResult {
        var competition: Competition!
        var score: String!
    }
    //var pickerView = UIPickerView()
    @IBOutlet weak var pickerView: UIPickerView!
    @IBOutlet weak var toolbarPicker: UIToolbar!
    let style = ["חזה","גב","חתירה","חופשי"]
    let range = Array(0...100)
    var styleToShow = ""
    var rangeToShow = ""
    
    var filteredArray = [statisticResult]()
    var array = [statisticResult]()
    var titleArray = ["12/04/2018","12/05/2018","12/06/2018","12/07/2018","12/08/2018","12/09/2018"]
    @IBOutlet weak var lineChartView: LineChartView!
    @IBOutlet weak var titleTable: UILabel!
    @IBOutlet weak var tableView: UITableView!
    var menu_vc: MenuViewController!
    
    var currentUser: User!
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        toolbarPicker.isHidden = true
        tableView.delegate = self
        tableView.dataSource = self
        
        pickerView.isHidden = true
        pickerView.backgroundColor = UIColor.lightGray
        let button = UIBarButtonItem(title: "בחר", style: .done, target: self, action: #selector(pickerViewStart))
        self.navigationItem.leftBarButtonItem = button
        
        setChart(self.array.count)
        initMenuBar()
        getData()
        
        self.tableView.backgroundColor = UIColor.clear
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
        // Do any additional setup after loading the view.
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func getData() {
        Service.shared.connectToServer(path: "getParticipantStatistics", method: .post, params: ["uid":currentUser.uid as AnyObject]) { (response) in
            if response.succeed {
                let resultsData = response.jsonAll["data"]! as? NSArray
                for data in resultsData! {
                    let jsonData = data as! JSON
                    let jsonCompetition = jsonData["competition"] as! JSON
                    let newCompetition = Competition(json: jsonCompetition, id: jsonCompetition["id"] as! String)
                    let score = jsonData["score"] as! String
                    self.array.append(statisticResult(competition: newCompetition, score: score))
                    self.tableView.reloadData()
                    print(self.array.count)
                }
            }
          
        }
    }
    
    func setChart(_ count: Int) {
        var dateArray = [String]()
        let formatDate = DateFormatter()
        let values = (0..<count).map { (i) -> ChartDataEntry in
            formatDate.dateFormat = "dd/MM/yyyy HH:mm"
            let oldDate = formatDate.date(from: self.filteredArray[i].competition.activityDate)
            formatDate.dateFormat = "dd/MM/yyyy"
            let newDate = formatDate.string(from: oldDate!)
            dateArray.append(newDate)
            return ChartDataEntry(x: Double(i), y: Double(self.filteredArray[i].score)!)
            
        }
        let set1 = LineChartDataSet(values: values, label: "תוצאות")
        let data = LineChartData(dataSet: set1)
        
        self.lineChartView.xAxis.valueFormatter = IndexAxisValueFormatter(values: dateArray)
        self.lineChartView.xAxis.granularity = 1
        self.lineChartView.data = data
        self.lineChartView.chartDescription?.text = ""
        self.lineChartView.tintColor = UIColor.black
    }
    
    
    func initMenuBar() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.menu_vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "menuId") as! MenuViewController
        menu_vc.currentUser = self.currentUser
        self.menu_vc.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
    }
    
    @objc func showMenu() {
        
        let rightButton = UIBarButtonItem(image: UIImage(named: "cancel.png"), style: .plain, target: self, action: #selector(cancelMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.addChildViewController(self.menu_vc)
        self.menu_vc.view.frame = self.view.frame
        self.view.addSubview(self.menu_vc.view)
        self.menu_vc.didMove(toParentViewController: self)
    }
    
    @objc func cancelMenu() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        
        self.menu_vc.view.removeFromSuperview()
    }
    
    @objc func pickerViewStart() {
        pickerView.delegate = self
        pickerView.dataSource = self
        pickerView.isHidden = !pickerView.isHidden
        toolbarPicker.isHidden = !toolbarPicker.isHidden
    }

    @IBAction func confirmButton(_ sender: Any) {
        print("push")
        self.titleTable.text = "\(self.rangeToShow) מטר \(self.styleToShow)"
        pickerView.isHidden = true
        toolbarPicker.isHidden = true
        self.filteredArray.removeAll()
        for result in self.array {
            if result.competition.getLength() == self.rangeToShow && result.competition.getSwimmingStyle() == self.styleToShow {
                let data = statisticResult(competition: result.competition, score: result.score)
                self.filteredArray.append(data)
            }
        }
        let formatDate = DateFormatter()
        formatDate.dateFormat = "dd/MM/yyyy HH:mm"
        self.filteredArray.sort(by: {formatDate.date(from:$0.competition.activityDate)! <	 formatDate.date(from:$1.competition.activityDate)!})
        setChart(self.filteredArray.count)
        self.tableView.reloadData()
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension StatisticsViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 2
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if component == 0 {
            return self.style.count
        }
        return self.range.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if component == 0 {
            return self.style[row]
        }
        return String(self.range[row])
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        var styleString = style[pickerView.selectedRow(inComponent: 0)]
        var rangeString = range[pickerView.selectedRow(inComponent: 1)]
        if component == 0 {
            styleString = style[row]
        }
        else {
            rangeString = range[row]
        }
        self.rangeToShow = String(rangeString)
        self.styleToShow = styleString
    }
    
}

extension StatisticsViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.filteredArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "statisticCell", for: indexPath) as! StatisticsTableViewCell
        cell.label.textAlignment = .center
        cell.title.text = "\(self.filteredArray[indexPath.row].competition.name)"
        cell.label.text = "התקיימה ב: \(self.filteredArray[indexPath.row].competition.activityDate)  , תוצאה: \(self.filteredArray[indexPath.row].score!) שניות"
        cell.layer.backgroundColor = UIColor.clear.cgColor
        cell.contentView.backgroundColor = UIColor.clear
        cell.backgroundColor = .clear
        
        return cell
    }
}
