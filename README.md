#LATCH INSTALLATION GUIDE FOR Open-Xchange


##INSTALLING THE LATCH PLUGIN
###PREREQUISITES
* Open-Xchange version 7.6.2

* To get the **"Application ID"** and **"Secret"**, (fundamental values for integrating Latch in any application), it’s necessary to register a developer account in [Latch's website](https://latch.elevenpaths.com). On the upper right side, click on **"Developer area"**. 



###DOWNLOADING THE PLUGIN
 * When the account is activated, the user will be able to create applications with Latch and access to developer documentation, including existing SDKs and plugins. The user has to access again to [Developer area](https://latch.elevenpaths.com/www/developerArea), and browse his applications from **"My applications"** section in the side menu.

* When creating an application, two fundamental fields are shown: **"Application ID"** and **"Secret"**, keep these for later use. There are some additional parameters to be chosen, as the application icon (that will be shown in Latch) and whether the application will support OTP  (One Time Password) or not.

* From the side menu in developers area, the user can access the **"Documentation & SDKs"** section. Inside it, there is a **"SDKs and Plugins"** menu. Links to different SDKs in different programming languages and plugins developed so far, are shown.

###INSTALLING THE PLUGIN
* Make sure that the Open-Xchange backend process is not running during the installation. The installer will try to stop the server before copying any files but it is preferable to have previously stopped it manually. 

* Make sure that there is a full backup of the Open-Xchange backend installation directory just in case something goes wrong during the installation.

* After downloading the installer, the user has to run it. heck the requirements, accept the license and follow the instructions about customizing installation path since the plugin will overwrite Open-Xchange files. Finally, the *uninstaller* folder will be shown.


####CONFIGURING THE INSTALLED MODULE
* Remember to review the configuration properties that the component needs to function correctly and that are stored in a file called **latch.properties** in the **etc** folder of the Open-Xchange backend installation directory. 

* In that file users should set Latch **application id** and **secret** as well as several other options regarding the repository where the pairing information will be stored.

* Remember that it may be necessary to add the CA that signs the Latch backend TLS certificates (StartCom Certification Authority) to the JVM certificate truststore as not all JVMs include it by default.

* The installer will not try to start the Open-Xchange backend process after the installation. Start it manually once the needed configuration tasks have been executed.



###UNINSTALLING THE PLUGIN IN Open-Xchange
* In order to uninstall this component,remember that the installer will generate an uninstaller named **uninstall.jar** in a folder named **uninstaller** in the installation directory.

* Make sure that the Open-Xchange backend process won´t be running during the uninstall. The uninstaller will try to stop the server before deleting any files but it is preferable to have previously stopped it manually.

* The uninstaller will not try to start the Open-Xchange backend process once the uninstall has finished. The user will have to start the server manually.


##USE OF LATCH PLUGIN FOR THE USERS
###PAIRING A USER IN Open-Xchange  
**Latch does not affect in any case or in any way the usual operations with an account. It just allows or denies actions over it, acting as an independent extra layer of security that, once removed or without effect, will have no effect over the accounts, which will remain with their original state.**

The user needs the Latch application installed on the phone, and follow these steps:

* **Step 1:** Pairing Open-Xchange account with Latch. The user has to log on into his Open-Xchange account. Go to **"Settings"**, and click on the new button **"Latch"**.

* **Step 2:** From the Latch app on the phone, the user has to generate the token, pressing on **“Add a new service"** at the bottom of the application, and pressing **"Generate new code"** will take the user to a new screen where the pairing code will be displayed.

* **Step 3:** The user has to type the characters generated on the phone into the text box displayed on the web page. Click on **"Pair"** button.

* **Step 4:** Now the user may lock and unlock the account, preventing any unauthorized access.     

###UNPAIRING A USER IN Open-Xchange       
The user has to log on into his Open-Xchange account and through the **Settings** button, get to Latch tab, where **unpair** may be pressed. He will receive a notification indicating that the service has been unpaired.

##RESOURCES
- You can access Latch´s use and installation manuals, together with a list of all available plugins here: [https://latch.elevenpaths.com/www/developers/resources](https://latch.elevenpaths.com/www/developers/resources)


- Further information on de Latch´s API can be found here: [https://latch.elevenpaths.com/www/developers/doc_api](https://latch.elevenpaths.com/www/developers/doc_api)

- For more information about how to use Latch and testing more free features, please refer to the user guide in Spanish and English:
	1. [English version](https://latch.elevenpaths.com/www/public/documents/howToUseLatchNevele_EN.pdf)
	1. [Spanish version](https://latch.elevenpaths.com/www/public/documents/howToUseLatchNevele_ES.pdf)
