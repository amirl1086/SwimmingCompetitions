//
//  StatisticsTableViewCell.swift
//  SwimmingCompetition
//
//  Created by Aviel on 04/06/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class StatisticsTableViewCell: UITableViewCell {

    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var label: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
