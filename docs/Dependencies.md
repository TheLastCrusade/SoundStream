#Sliding Menu + ActionBarScherlock

In order to get the sliding drawer functionality we wanted, we are using the SlidingMenu library developed by Jeremy Feinstein. Since we are depending upon the action bar to interact with this menu, we are also using the ActionBarSherlock to create an action bar in older versions of Android.

##Getting the Libraries

###Downloads
SlidingMenu: https://github.com/jfeinstein10/SlidingMenu (at pull request 191)
ActionBarScherlock: http://actionbarsherlock.com/ (Version 4.2.0)

Download the zip files and extract them to wherever you would like. Just remember where you put them because we need that for importing

###Import Libraries into Eclipse
We have to import both libraries, but start with SlidingMenu

1. Create a new Android Project from Existing Code
2. Navigate to your unzipped SlidingMenu folder
3. Select the library folder (you can import all of them, but library is the one that we need)
4. Make sure the library folder is checked, and Select Finish
5. Rename the project that was just created to SlidingMenuLibrary
6. Ignore the errors for now, we will fix them when we set up the dependencies

Now on to ActionBarSherlock (again, this applies to Eclipse only)

1. Create a new Android Project from Existing Code
2. Navigate to the unzipped ActionBarSherlock folder
3. Select the library folder
4. Make sure the library folder is checked, and Select Finish
5. Rename the project to ABSLibrary
6. Clean the ABSLibrary Project


###Set up SlidingMenu Dependencies in Eclipse
Time to deal with the errors

1. Select SlidingMenuLibrary
2. Right click, select properties
3. Go to Android
4. Change Project Build Target to Google APIs, Android 4.2
5. Go down to the Library box and click Add
6. Select ABSLibrary
7. You will now get an error telling you about mismatched jar files. Go into the lib folder of SlidingMenuLibrary and delete the android support jar -  it will just go and use the one in ABSLibrary.
8. If you still have errors, clean the project.
9. Go into the src folder and open up SlidingFragmentActivity
10. change the extends FragmentActivity to extends SherlockFragmentActivity (Note: If we use more later, you will need to go change them to their Sherlock versions, but for now this is all we are using)


###Set up Project Dependencies
Finally we can set up the actual project dependencies.

1. Select the LandingActivity project
2. Right click-> Properties -> Android -> Add Library -> SlidingMenuLibrary
3. Again, delete the support jar in the libs folder

###Finished!
Everything should be set up just fine now :]
