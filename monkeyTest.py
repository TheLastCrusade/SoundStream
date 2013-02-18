import os
# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

# Installs the Android package. Notice that this method returns a boolean, so you can test
# to see if the installation worked.
device.installPackage('fanClub/bin/fanClub.apk')

# sets a variable with the package's internal name
package = 'com.lastcrusade.fanclub'

# sets a variable with the name of an Activity in the package
activity = 'com.lastcrusade.fanclub.LandingActivity'

# sets the name of the component to start
runComponent = package + '/' + activity

# Runs the component
device.startActivity(component=runComponent)
# Sleep for half of a second to allow activity to load
MonkeyRunner.sleep(.5)

# Takes a screenshot
result = device.takeSnapshot()

# Writes the screenshot to a file
# Calculates number of files in test_results
num_files = len([name for name in os.listdir('test_results/') if os.path.isfile(name)])
result.writeToFile('test_results/shot'+ str(num_files) +'.png','png')

# Select and press create
##TODO make test to open create
