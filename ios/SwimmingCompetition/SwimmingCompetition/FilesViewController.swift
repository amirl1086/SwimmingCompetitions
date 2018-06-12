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
import SwiftSpinner

struct Media {
    var url: String
    var contentType: String
    var data: Data?
}


class FilesViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UICollectionViewDelegateFlowLayout {
    @IBOutlet weak var collectionView: UICollectionView!
    
    let avPlayerView = AVPlayerViewController()
    var avPlayer: AVPlayer?
    
    var currentUser: User!
    var currentCompetition: Competition!
    
    var mediaArray = [Media]()
    
    var backgroundView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
       
        let button =  UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(addMedia))
        self.navigationItem.rightBarButtonItem = button
        
        getMedia()

       
        
        self.collectionView.backgroundColor = UIColor.clear
        self.backgroundView = UIImageView(frame: self.view.bounds)
        self.backgroundView.image = UIImage(named: "abstract_swimming_pool.jpg")//if its in images.xcassets
        self.view.insertSubview(self.backgroundView, at: 0)
    }
    
    override func viewDidLayoutSubviews() {
        self.backgroundView.frame = self.view.bounds
    }
    
    func videoSnapshot(filePath: String, completion: (_ result: AVAssetImageGenerator) -> Void)  {
     
        let url = URL(fileURLWithPath: filePath)
        let asset = AVURLAsset(url: url)
        let generator = AVAssetImageGenerator(asset: asset)
        generator.appliesPreferredTrackTransform = true
       
        completion(generator)
       
    }
    
    func fromGalery() {
        let image = UIImagePickerController()
        image.delegate = self
        image.mediaTypes = [kUTTypeImage as String]
        image.sourceType = UIImagePickerControllerSourceType.photoLibrary
        image.allowsEditing = false
        self.present(image, animated: true) {
            
        }
    }
    
    func fromCamera() {
        let image = UIImagePickerController()
        image.delegate = self
        image.mediaTypes = [kUTTypeImage as String]
        image.sourceType = UIImagePickerControllerSourceType.camera
        image.allowsEditing = false
        self.present(image, animated: true) {
            
        }
    }
    
    @objc func addMedia() {
       
        let alert = UIAlertController(title: "בחר אפשרות", message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "גלריה", style: .default, handler: { (action) in
            
            self.fromGalery()
            alert.dismiss(animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "מצלמה", style: .default, handler: { (action) in
            
            self.fromCamera()
            alert.dismiss(animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "ביטול", style: .cancel, handler: { (action) in
            
            alert.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
   
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        if let image = info[UIImagePickerControllerOriginalImage] as? UIImage {
            //self.image.image = image
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
        let date = Date()
        let storageRef = Storage.storage().reference(withPath: "pics/\(date).jpg")
        let uploadMetadata = StorageMetadata()
        uploadMetadata.contentType = "image/jpeg"
        
        let upload = storageRef.putData(data, metadata: uploadMetadata) { (metadata, error) in
            if error != nil {
                self.present(Alert().confirmAlert(title: "שגיאה", message: "העלאת מדיה נכשלה"), animated: true, completion: nil)
            } else {
                self.uploadToDB(contentType: (metadata?.contentType)!, url: (metadata?.downloadURL())!)
            }
        }
        upload.observe(.progress) { (snapshot) in
            //guard let strongSelf = self else { return }
            guard snapshot.progress != nil else { return }
            SwiftSpinner.show(progress: 100.0 * Double(snapshot.progress!.completedUnitCount) / Double(snapshot.progress!.totalUnitCount), title: "מעלה מדיה")
        }
    }
    
    func uploadMovieToFirebase(data: URL) {
         let date = Date()
        let storageRef = Storage.storage().reference(withPath: "videos/\(date)")
        let uploadMetadata = StorageMetadata()
        uploadMetadata.contentType = "video/mp4"
        let upload = storageRef.putFile(from: data, metadata: uploadMetadata) { (metadata, error) in
            if error != nil {
                self.present(Alert().confirmAlert(title: "שגיאה", message: "העלאת מדיה נכשלה"), animated: true, completion: nil)
            } else {
                self.uploadToDB(contentType: (metadata?.contentType)!, url: (metadata?.downloadURL())!)
            }
        }
        upload.observe(.progress) { (snapshot) in
            //guard let strongSelf = self else { return }
            guard snapshot.progress != nil else { return }
            SwiftSpinner.show(progress: 100.0 * Double(snapshot.progress!.completedUnitCount) / Double(snapshot.progress!.totalUnitCount), title: "מעלה מדיה")
        }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    func uploadToDB(contentType: String, url: URL) {
        let parameters = [
            "competitionId": self.currentCompetition.getId(),
            "url": url,
            "contentType": contentType
        ] as [String:AnyObject]
        Service.shared.connectToServer(path: "addNewMedia", method: .post, params: parameters) { (response) in
            if response.succeed {
                let newMedia = Media(url: response.data["url"] as! String, contentType: response.data["contentType"] as! String, data: nil)
                self.mediaArray.append(newMedia)
                self.collectionView.reloadData()
                self.present(Alert().confirmAlert(title: "", message: "העלאה בוצעה בהצלחה"), animated: true, completion: nil)
            } else {
                self.present(Alert().confirmAlert(title: "שגיאה", message: "העלאת מדיה נכשלה"), animated: true, completion: nil)
            }
            SwiftSpinner.hide()
        }
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    func getMedia() {
        let parameters = [
            "competitionId": self.currentCompetition.getId()
        ] as [String:AnyObject]
        Service.shared.connectToServer(path: "getMediaByCompetitionId", method: .post, params: parameters) { (response) in
            if response.succeed {
                for data in response.data {
                    let activity = UIActivityIndicatorView(frame: CGRect(x: 0.0, y: 0.0, width: 10.0, height: 10.0)) as UIActivityIndicatorView
                    let mediaData = response.data[data.0] as! JSON
                    let newMedia = Media(url: mediaData["url"] as! String, contentType: mediaData["contentType"] as! String, data: nil)
                    self.mediaArray.append(newMedia)
                    self.collectionView.reloadData()
                }
            } else {
                self.present(Alert().confirmAlert(title: "", message: "לא נמצאה מדיה לתחרות שנבחרה"), animated: true, completion: nil)
            }
        }
    }
    
    func getSnapshotFromVideo(path: String) -> UIImage? {
        let videoUrl = URL(fileURLWithPath: path)
        let asset = AVURLAsset(url: videoUrl)
        let generator = AVAssetImageGenerator(asset: asset)
        generator.appliesPreferredTrackTransform = true
        let timeStamp = CMTime(seconds: 0, preferredTimescale: 1)
        do {
            let imageRef = try generator.copyCGImage(at: timeStamp, actualTime: nil)
            return UIImage(cgImage: imageRef)
        } catch {
            return nil
        }
    }
    
    func getDataFromUrl(url: URL, completion: @escaping (Data?, URLResponse?, Error?) -> ()) {
        URLSession.shared.dataTask(with: url) { data, response, error in
            completion(data, response, error)
        }.resume()
    }

}

extension FilesViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as! FilesCollectionViewCell
        cell.isUserInteractionEnabled = false
        cell.backgroundColor = UIColor.white
        cell.activity.startAnimating()
        cell.activity.hidesWhenStopped = true
        cell.activity.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray
    
        
        self.getDataFromUrl(url: URL(string: self.mediaArray[indexPath.row].url)!) { (data, response, error) in
            DispatchQueue.main.async() {
                print(response!)
                cell.activity.stopAnimating()
                self.mediaArray[indexPath.row].data = data!
                if self.mediaArray[indexPath.row].contentType == "image/jpeg" {
                    cell.imageView.image = UIImage(data: data!)
                } else {
                    cell.imageView.image = UIImage(named: "play.png")
                    
                    //self.getSnapshotFromVideo(path: self.mediaArray[indexPath.row].url)
                }
                
                cell.isUserInteractionEnabled = true
            }
           
        }
        return cell
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.mediaArray.count
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if self.mediaArray[indexPath.row].contentType == "image/jpeg" {
            let sb = UIStoryboard(name: "Main", bundle: nil)
            if let detailsView = sb.instantiateViewController(withIdentifier: "imageId") as? FilesDetailsViewController {
                detailsView.image = UIImage(data: self.mediaArray[indexPath.row].data!)!
                self.navigationController?.pushViewController(detailsView, animated: true)
            }
        } else {
            let movie: URL? = URL(string: self.mediaArray[indexPath.row].url)
            if let url = movie {
                self.avPlayer = AVPlayer(url: url)
                self.avPlayerView.player = self.avPlayer
            }
            self.present(self.avPlayerView, animated: true) {
                self.avPlayerView.player?.play()
            }
        }
        
    }
    
 
}

