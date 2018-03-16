//
//  CompetitionTableViewCell.swift
//  SwimmingCompetition
//
//  Created by Aviel on 15/03/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit

class CompetitionTableViewCell: UITableViewCell {

    @IBOutlet weak var cellView: UIView!
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var date: UILabel!
    @IBOutlet weak var ages: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
