//
//  FilesViewController.swift
//  SwimmingCompetition
//
//  Created by Aviel on 05/06/2018.
//  Copyright © 2018 Aviel. All rights reserved.
//

import UIKit
import MobileCoreServices
import Firebase

class FilesViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    @IBOutlet weak var image: UIImageView!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    func fromGalery() {
        let image = UIImagePickerController()
        image.delegate = self
        image.sourceType = UIImagePickerControllerSourceType.photoLibrary
        image.allowsEditing = false
        self.present(image, animated: true) {
            
        }
    }
    
    func fromCamera() {
        let image = UIImagePickerController()
        image.delegate = self
        image.sourceType = UIImagePickerControllerSourceType.camera
        image.allowsEditing = false
        self.present(image, animated: true) {
            
        }
    }
    
    
    @IBAction func button(_ sender: Any) {
       
        let alert = UIAlertController(title: "בחר אפשרות", message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "גלריה", style: .default, handler: { (action) in
            
            self.fromGalery()
            alert.dismiss(animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "מצלמה", style: .default, handler: { (action) in
           
            self.fromCamera()
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        if let image = info[UIImagePickerControllerOriginalImage] as? UIImage {
            self.image.image = image
        } else {
            
        }
        guard let mediaType:String = info[UIImagePickerControllerMediaType] as? String else {
            self.dismiss(animated: true, completion: nil)
            return
        }
        if mediaType == (kUTTypeImage as String) {
            if let image = info[UIImagePickerControllerOriginalImage] as? UIImage,
                let imageData = UIImageJPEGRepresentation(image, 0.8) {
                uploadToFirebase(data: imageData as NSData)
            }
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    func uploadToFirebase(data: NSData) {
        
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
