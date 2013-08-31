#Sliding Menu + ActionBarSherlock

In order to get the sliding drawer functionality we wanted, we are using the SlidingMenu library developed by Jeremy Feinstein. Since we are depending upon the action bar to interact with this menu, we are also using the ActionBarSherlock to create an action bar in older versions of Android.

##Getting the Libraries

###Downloads
SlidingMenu: https://github.com/jfeinstein10/SlidingMenu (at pull request 191)
ActionBarSherlock: http://actionbarsherlock.com/ (Version 4.2.0)

Download the zip files and extract them. You only need the library folders for each of these. The following steps outline isolating the library folders and putting them in the right directory

1. Open the unzipped SherlockActionBar folder
2. Navigate to the directory named library
3. Rename that directory to ABSLibrary
4. Move the ABSLibrary directory into the third_party directory
5. Open the unzipped SlidingMenu folder
6. Navigate to the directory named library
7. Rename that directory to SlidingMenuLibrary
8. Move the SlidingMenuLibrary directoy into the third_party directory

###Import Libraries into Eclipse
We now have to import both libraries into Eclipse.

Starting with ABSLibrary

1. Create a new Android Project from Existing Code
2. Navigate to the ABSLibrary
3. Make sure the folder is checked and Select Finish

Now on to SlidingMenuLibrary (again, this applies to Eclipse only)

1. Create a new Android Project from Existing Code
2. Navigate to the SlidingMenuLibrary
3. Make sure the folder is checked, and Select Finish
4. There will be a few errors here - we will fix them in the next section


###Set up SlidingMenu Dependencies in Eclipse
Time to deal with the errors

1. Select SlidingMenuLibrary
2. Right click, select properties
3. Go to Android
4. Change Project Build Target to Google APIs, Android 4.2
5. Go down to the Library box and click Add
6. Select ABSLibrary
7. You will now get an error telling you about mismatched jar files. Go into the lib folder of SlidingMenuLibrary and delete the android support jar -  it will just go and use the one in ABSLibrary.
8. If you still have errors, build, clean, and refresh the project.
9. Go into the src folder and open up SlidingFragmentActivity
10. change the extends FragmentActivity to extends SherlockFragmentActivity (Note: If we use more later, you will need to go change them to their Sherlock versions, but for now this is all we are using)
11. Import `com.actionbarsherlock.app.SherlockFragmentActivity`

###Updating Android Support Jar
Because we use new Android Gesture Detection, we need to have the most recent version of the android support library in ABSLibrary.

1. Go to the SDK Manager and make sure that you have the most recent android support library
2. Navigate to the android-sdk folder on your computer
3. Go into extras/android/support/v4
4. Copy the android support v4 jar
5. Navigate to third_party/ABSLibrary/libs
6. Paste the copied jar to replace the existing one

##Google Analytics sdk

1. Download the [Analytics SDK](https://developers.google.com/analytics/devguides/collection/android/resources)
2. Unzip and copy libGoogleAnalyticsV2.jar into libs folder

##ACRA (for crash reporting)

1. Download the [Acra Library](https://oss.sonatype.org/content/groups/public/ch/acra/acra/4.5.0/)
2. Unzip and copy the jar from the acra build folder into the project libs folder

##Testing Libraries

###Robotium

1. Download the [Robotium Library](https://code.google.com/p/robotium/), version 4.2
2. Create a libs directory in the project SoundStream-test, and place the jar in there
3. Right click the project in eclipse, and go click on properties
4. Go to the build path option, and click on the Libraries tab
5. Click "Add JARs..." and select the robotium jar
6. Go to the Order and Export tab, and click on the check box next to the robotium jar

###Spoon

1. Download the spoon runner and client from (http://square.github.io/spoon/#download)
2. Move the Spoon Runner jar into the third_party directory
2. Move the client jar to the libs directory in the SoundStream-test project
3. Right click the project in eclipse, and go click on properties
4. Go to the build path option, and click on the Libraries tab
5. Click "Add JARs..." and select the Spoon Client jar
6. Go to the Order and Export tab, and click on the check box next to the Spoon Client jar

###Finished!
Everything should be set up just fine now :]
