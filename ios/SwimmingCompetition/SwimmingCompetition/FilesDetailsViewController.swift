//
//  FilesDetailsViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 09/06/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class FilesDetailsViewController: UIViewController {

    @IBOutlet weak var imageView: UIImageView!
    var image = UIImage()
    override func viewDidLoad() {
        super.viewDidLoad()

        self.imageView.image = self.image
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
