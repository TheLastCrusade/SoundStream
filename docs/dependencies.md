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

###Finished!
Everything should be set up just fine now :]
