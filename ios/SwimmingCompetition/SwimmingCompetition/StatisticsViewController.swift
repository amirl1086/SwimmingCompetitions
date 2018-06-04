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
        var competition: String!
        var score: String!
    }
    var pickerView = UIPickerView()
    let style = ["חזה","גב","חתירה","חופשי"]
    let range = Array(0...100)
    
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
        
        let button = UIBarButtonItem(title: "בחר", style: .done, target: self, action: #selector(pickerViewStart))
        self.navigationItem.leftBarButtonItem = button
        
        array.append(statisticResult(competition: "12/04/2018", score: "1.2"))
        array.append(statisticResult(competition: "12/05/2018", score: "3.5"))
        array.append(statisticResult(competition: "12/06/2018", score: "0.7"))
        array.append(statisticResult(competition: "12/07/2018", score: "13.7"))
        array.append(statisticResult(competition: "12/08/2018", score: "40"))
        array.append(statisticResult(competition: "12/09/2018", score: "6.9"))
        setChart(self.array.count)
        initMenuBar()
        
        Service.shared.connectToServer(path: "getParticipantStatistics", method: .post, params: ["uid":currentUser.uid as AnyObject]) { (response) in
            print(response)
        }
        
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
    
    func setChart(_ count: Int) {
        let values = (0..<count).map { (i) -> ChartDataEntry in
            let val = Double(arc4random_uniform(UInt32(count)) + 4	)
            return ChartDataEntry(x: Double(i), y: Double(self.array[i].score)!)
            
        }
        let set1 = LineChartDataSet(values: values, label: "fffff")
        let data = LineChartData(dataSet: set1)
        
        self.lineChartView.xAxis.valueFormatter = IndexAxisValueFormatter(values: self.titleArray)
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
        pickerView.isHidden = false
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
    
    
}
