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
import AVFoundation
import AVKit


class FilesViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    let avPlayerView = AVPlayerViewController()
    var avPlayer: AVPlayer?
    
    @IBOutlet weak var image: UIImageView!
    override func viewDidLoad() {
        super.viewDidLoad()

        let movie: URL? = URL(string: "https://firebasestorage.googleapis.com/v0/b/firebase-swimmingcompetitions.appspot.com/o/Movie%2Fjustapic.mov?alt=media&token=12b846aa-3772-471e-b27a-36e1657cbf64")
        if let url = movie {
            self.avPlayer = AVPlayer(url: url)
            self.avPlayerView.player = self.avPlayer
        }
        /*self.image.image = videoSnapshot(filePath: "https://firebasestorage.googleapis.com/v0/b/firebase-swimmingcompetitions.appspot.com/o/Movie%2Fjustapic.mov?alt=media&token=12b846aa-3772-471e-b27a-36e1657cbf64")*/
        
        videoSnapshot(filePath: "https://firebasestorage.googleapis.com/v0/b/firebase-swimmingcompetitions.appspot.com/o/Movie%2Fjustapic.mov?alt=media&token=12b846aa-3772-471e-b27a-36e1657cbf64") { (result) in
            let time = CMTime(seconds: 1, preferredTimescale: 60)
            
            do {
                let imageRef = try result.copyCGImage(at: time, actualTime: nil)
                self.image.image =  UIImage(cgImage: imageRef)
            } catch let error as Error {
                print("problem \(error)")
                
            }
        }
        // Do any additional setup after loading the view.
    }
    
    func videoSnapshot(filePath: String, completion: (_ result: AVAssetImageGenerator) -> Void)  {
     
        let url = URL(fileURLWithPath: filePath)
        let asset = AVURLAsset(url: url)
        let generator = AVAssetImageGenerator(asset: asset)
        generator.appliesPreferredTrackTransform = true
       
        completion(generator)
        
        /*let time = CMTime(seconds: 1, preferredTimescale: 60)
        
        do {
            let imageRef = try generator.copyCGImage(at: time, actualTime: nil)
            return UIImage(cgImage: imageRef)
        } catch let error as Error {
            print("problem")
            return nil
        }*/
    }
    
    func fromGalery() {
        let image = UIImagePickerController()
        image.delegate = self
        image.mediaTypes = [kUTTypeImage as String, kUTTypeMovie as String]
        image.sourceType = UIImagePickerControllerSourceType.photoLibrary
        image.allowsEditing = false
        self.present(image, animated: true) {
            
        }
    }
    
    func fromCamera() {
        let image = UIImagePickerController()
        image.delegate = self
        image.mediaTypes = [kUTTypeImage as String, kUTTypeMovie as String]
        image.sourceType = UIImagePickerControllerSourceType.camera
        image.allowsEditing = false
        self.present(image, animated: true) {
            
        }
    }
    
    
    @IBAction func button(_ sender: Any) {
        /*self.present(self.avPlayerView, animated: true) {
            self.avPlayerView.player?.play()
        }*/
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
                uploadToFirebase(data: imageData)
            }
        } else if mediaType == (kUTTypeMovie as String) {
            if let movie = info[UIImagePickerControllerMediaURL] as? URL {
                uploadMovieToFirebase(data: movie)
            }
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    func uploadToFirebase(data: Data) {
        let storageRef = Storage.storage().reference(withPath: "pic/justapic.jpg")
        let uploadMetadata = StorageMetadata()
        uploadMetadata.contentType = "image/jpeg"
        
        let upload = storageRef.putData(data, metadata: uploadMetadata) { (metadata, error) in
            if error != nil {
                print("error \(error?.localizedDescription)")
            } else {
                print("complete \(metadata)")
            }
        }
        upload.observe(.progress) { (snapshot) in
            //guard let strongSelf = self else { return }
            guard snapshot.progress != nil else { return }
            
        }
    }
    
    func uploadMovieToFirebase(data: URL) {
        let storageRef = Storage.storage().reference(withPath: "Movie/justapic.mov")
        let uploadMetadata = StorageMetadata()
        uploadMetadata.contentType = "video/quicktime"
        storageRef.putFile(from: data, metadata: uploadMetadata) { (metadata, error) in
            if error != nil {
                print("error \(error)")
            } else {
                print("complete: \(metadata)")
                print("the url: \(metadata?.downloadURL())")
            }
        }
        
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
