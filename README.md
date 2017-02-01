# botXbluemix #

This repository demonstrates how to create a basic bot with LINE Messaging API with bluemix.

### How do I get set up? ###

* Configure your bot channel ini LINE@ developer

* Make *manifest.yml* file
	
	```yml
	applications:
	- path: <YOUR_WAR_FILE_PATH>
  	  memory: 512M
  	  instances: 1
  	  domain: mybluemix.net
  	  name: <YOUR_BLUEMIX_APP_NAME>
  	  host: <YOUR_BLUEMIX_APP_NAME>
  	  disk_quota: 1024M
	```

* Compile
 
    ```bash
    $ gradle clean build
    ```

* Push to bluemix
	
	`$ cf push <YOUR_BLUEMIX_APP_NAME>`
	
* See logs

	`$ cf logs <YOUR_BLUEMIX_APP_NAME>`

### How do I contribute? ###

* Add your name and e-mail address into CONTRIBUTORS.txt
