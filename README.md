sims-topaz-android
=================

La partie Android du projet SIMS.

Il y a deux moyen de faire afin d'installer Eclipse avec tout ce qui'il faut pour faire du dev android:

**1. Télécharger un Eclipse avec tout ce qui lui faut (windows ou d'autre plateformes)**

* http://developer.android.com/sdk/index.html?utm_source=weibolife

* Ouvre Android SDK Manager a partir de Eclipse Windows/Android SDK Manager et install tous les API a partir de API 8 (API 8 c'est Android 2.3). Des extras install au moins Android Support Repository et Android Studio Library

**2. Utiliser un Eclipse existant pour le dev Android** 

* Telecharge et unzip android SDK (http://developer.android.com/sdk/installing/index.html et dans la partie Information for other platforms on trouve ce qu'il faut pour linux)
Note: Il faut que tu retient l'endroit d'installation!!

* Install le ADT Plugin: vas dans Eclipse en Help/Install New Software et ajoute le lien https://dl-ssl.google.com/android/eclipse/ et install tout ce qui te propose

* Configure le plugin ADT : Normalement au restart de Eclipse il vas te proposer d'introduire l'endroit d'installation du SDK du coup tu vas aller dans le folder du SDK 

* Ouvre Android SDK Manager a partir de Eclipse Windows/Android SDK Manager et install tous les API a partir de API 8 (API 8 c'est Android 2.3). Des extras install au moins :
    * Android Support Repository 
    * Android Studio Library
    * Google Play services

* Pour pouvoir avoir la carte il faut ouvrir la library google-play-services_lib qui se trouve  
> <android-sdk-folder>/extras/google/google_play_services/libproject/google-play-services_lib  


**3. Utiliser Genymotion (au lieu de l'émulateur par défaut)**

Attention la manipulation utilise le SDK 4.3
* S'inscrire et télécharger Genymotion : http://www.genymotion.com/
* Créer un émulateur Galaxy Nexus - 4.3 - API 18 - 720x1280 
* Télécharger http://goo.im/gapps/gapps-jb-20130813-signed.zip
* Lancer l'emulateur et glisser déposer le fichier dézippé
* Une popup s'affiche, faire OK, des erreurs peuvent d'afficher mais les ignorer
* Redémarrer l'émulateur
* Android doit demander de configurer le téléphone, inscrire un compte gmail
* Redémarrer l'émulateur
http://www.tushroy.com/2013/12/installing-google-play-services-on-genymotion-2-0.html