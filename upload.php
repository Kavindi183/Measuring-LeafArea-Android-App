
<?php
	
	if (isset($_POST['image'])){


		//give an id to the image according to the time when the image is uploaded
		//this can be changed to give the name to the image according to the IP address of the user


		// $now=DateTime::createFromFormat('U.u',microtime(true));
		// $id=$now->format('YmdHisu');
		
		$id='leaf';

		//upload the image to the folder
		
		$upload_folder="upload";
		$path="$upload_folder/$id.jpg";
		$image=$_POST['image'];

		//if the upload is successful
		if(file_put_contents($path,base64_decode($image))!=false){
			
			echo "uploaded_success";
			
			
			//execute the matlab code
			$inputDir  = "C:\\xampp\\htdocs\\news"; // directory in server which the matlab code is saved 
			$command = "matlab -sd ".$inputDir." -r countArea()";
			
			exec($command);
						
			exit;
			
		}else{ //upload is not successful
			
			echo "uploaded_failed";
			exit;
			
		}
		
	}
	else{

		echo 'image_not_in';
		exit;
			
	}
	
?>

