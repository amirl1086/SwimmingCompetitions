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
    
    /* Struct for the statistic results by competition and user's score */
    struct statisticResult {
        var competition: Competition!
        var score: String!
    }
    
    /* Struct for the picker view by style and its length */
    struct pickerResult {
        var style: String
        var length: [String]
    }
    
    var pickerArray = [pickerResult]()
    //var pickerView = UIPickerView()
    @IBOutlet weak var pickerView: UIPickerView!
    @IBOutlet weak var toolbarPicker: UIToolbar!
    let style = ["חזה","גב","חתירה","חופשי"]
    let range = Array(0...100)
    var styleToShow = ""
    var rangeToShow = ""
    
    var filteredArray = [statisticResult]()
    var array = [statisticResult]()
    
    @IBOutlet weak var lineChartView: LineChartView!
    @IBOutlet weak var titleTable: UILabel!
    @IBOutlet weak var tableView: UITableView!
    var menu_vc: MenuViewController!
    
    var currentUser: User!
    var isChild = false
    
    var backgroundView: UIImageView!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        toolbarPicker.isHidden = true
        tableView.delegate = self
        tableView.dataSource = self
        pickerView.delegate = self
        pickerView.dataSource = self
        pickerView.isHidden = true
        pickerView.backgroundColor = UIColor.lightGray
        
        let button = UIBarButtonItem(title: "בחר", style: .done, target: self, action: #selector(pickerViewStart))
        
        if isChild {
            self.navigationItem.rightBarButtonItem = button
        } else {
            self.navigationItem.leftBarButtonItem = button
            initMenuBar()
        }
        
        /* get the statistic data */
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
    
    /* Request the statistics data */
    func getData() {
        Service.shared.connectToServer(path: "getParticipantStatistics", method: .post, params: ["uid":currentUser.uid as AnyObject]) { (response) in
            if response.succeed {
                /* get the result and push to array as a statisticResult object by competition and score */
                let resultsData = response.jsonAll["data"]! as? NSArray
                for data in resultsData! {
                    let jsonData = data as! JSON
                    let jsonCompetition = jsonData["competition"] as! JSON
                    let newCompetition = Competition(json: jsonCompetition, id: jsonCompetition["id"] as! String)
                    let score = jsonData["score"] as! String
                    self.array.append(statisticResult(competition: newCompetition, score: score))
                    
                }
                /* if there is not data */
                if self.array.isEmpty {
                    self.present(Alert().confirmAlert(title: "", message: "לא נמצאו תוצאות"), animated: true, completion: nil)
                }

                self.setPickerValue()
                self.tableView.reloadData()
                
            } else {
                self.present(Alert().confirmAlert(title: "שגיאה", message: "לא ניתן להשיג מידע"), animated: true, completion: nil)
            }
          
        }
    }
    
    /* set the pickerArray by styles and lengths */
    func setPickerValue() {
        for result in self.array {
            if self.pickerArray.contains(where: { (picker) -> Bool in
                return picker.style == result.competition.swimmingStyle
            }) {
                for i in 0..<self.pickerArray.count {
                    if self.pickerArray[i].style == result.competition.swimmingStyle {
                        if !self.pickerArray[i].length.contains(result.competition.length) {
                            self.pickerArray[i].length.append(result.competition.length)
                        }
                    }
                }
            } else {
                self.pickerArray.append(pickerResult(style: result.competition.swimmingStyle, length: [result.competition.length]))
            }
        }
    }
    
    /* draw the graph when the user choose a style and length to show */
    func setChart(_ count: Int) {
        var dateArray = [String]()
        let formatDate = DateFormatter()
        let values = (0..<count).map { (i) -> ChartDataEntry in
            /* sort by date */
            formatDate.dateFormat = "dd/MM/yyyy HH:mm"
            let oldDate = formatDate.date(from: self.filteredArray[i].competition.activityDate)
            formatDate.dateFormat = "MM/yyyy"
            let newDate = formatDate.string(from: oldDate!)
            dateArray.append(newDate)
            return ChartDataEntry(x: Double(i), y: Double(self.filteredArray[i].score)!)
            
        }
        let set1 = LineChartDataSet(values: values, label: "תוצאות")
        set1.setColor(UIColor.black)
        let data = LineChartData(dataSet: set1)
        self.lineChartView.xAxis.valueFormatter = IndexAxisValueFormatter(values: dateArray)
        self.lineChartView.xAxis.granularity = 1
       
        self.lineChartView.data = data
        self.lineChartView.chartDescription?.text = ""
        self.lineChartView.tintColor = UIColor.black
        set1.circleHoleColor = UIColor.black
    }
    
    /* create side menu bar */
    func initMenuBar() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.menu_vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "menuId") as! MenuViewController
        menu_vc.currentUser = self.currentUser
        self.menu_vc.view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
    }
    
    /* action to show the side menu bar */
    @objc func showMenu() {
        
        let rightButton = UIBarButtonItem(image: UIImage(named: "cancel.png"), style: .plain, target: self, action: #selector(cancelMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        self.addChildViewController(self.menu_vc)
        self.menu_vc.view.frame = self.view.frame
        self.view.addSubview(self.menu_vc.view)
        self.menu_vc.didMove(toParentViewController: self)
    }
    
    /* action to close the side menu bar */
    @objc func cancelMenu() {
        let rightButton = UIBarButtonItem(image: UIImage(named: "menu.png"), style: .plain, target: self, action: #selector(showMenu))
        self.navigationItem.rightBarButtonItem = rightButton
        
        self.menu_vc.view.removeFromSuperview()
    }
    
    /* action to show the style and lengh picker */
    @objc func pickerViewStart() {
        
        pickerView.reloadAllComponents()
        pickerView.isHidden = !pickerView.isHidden
        toolbarPicker.isHidden = !toolbarPicker.isHidden
    }

    /* action when the user choose styke and length and confirm for show */
    @IBAction func confirmButton(_ sender: Any) {
        pickerView.reloadAllComponents()
        if !self.array.isEmpty {
            self.titleTable.text = "\(self.rangeToShow) מטר \(self.styleToShow)"
        }
        
        pickerView.isHidden = true
        toolbarPicker.isHidden = true
        self.filteredArray.removeAll()
        /* push the correct data to filtered array to show the results */
        for result in self.array {
            if result.competition.getLength() == self.rangeToShow && result.competition.getSwimmingStyle() == self.styleToShow {
                let data = statisticResult(competition: result.competition, score: result.score)
                self.filteredArray.append(data)
            }
        }
        /* sort the competitions by date */
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

/* the picker view functions */
extension StatisticsViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    /* the number of components for the picker */
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 2
    }
    
    /* the number of rows in component */
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if self.array.count != 0 {
            if component == 0 {
                return self.pickerArray.count
            } else {
                let selected = pickerView.selectedRow(inComponent: 0)
                return self.pickerArray[selected].length.count
            }
        }
        return 1
    }
    
    /* the row titles */
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        
        if self.array.count != 0 {
            if component == 0 {
                self.styleToShow = self.pickerArray[row].style
                return self.pickerArray[row].style
            } else {
                let selected = pickerView.selectedRow(inComponent: 0)
                self.rangeToShow = self.pickerArray[selected].length[row]
                return self.pickerArray[selected].length[row]
            }
        }
        return "אין פרטים להציג"
    }
    
    /* show the selected rows */
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        
        if self.array.count != 0 {
            pickerView.reloadComponent(1)
            var selectedStyle = pickerView.selectedRow(inComponent: 0)
            var selectedLength = pickerView.selectedRow(inComponent: 1)
            let styleString = self.pickerArray[selectedStyle].style
            let rangeString = self.pickerArray[selectedStyle].length[selectedLength]
            self.rangeToShow = String(rangeString)
            self.styleToShow = styleString
        }
        
    }
    
}

/* the table view functions */
extension StatisticsViewController: UITableViewDelegate, UITableViewDataSource {
    
    /* the number of rows for the table */
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
