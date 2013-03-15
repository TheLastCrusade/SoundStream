# Using new relic analytics

This changes our deployment process from eclipse's build and run to an Ant build. Their instructions are located at [New Relic website](http://newrelic.wistia.com/medias/rzbz77365w)

## Dependencies
1. [Android SDK](http://developer.android.com/sdk/index.html)
2. [Ant](http://ant.apache.org/)

## Install New Relic
1. Download the New relic Android Agent
2. Unzip the contents into the third_party directory
3. Make sure your android path is set

   You can check by typing `export` (or `set` in Windows) in your terminal and looking for `ANDROID_HOME`. If it is not you can set it by adding the following line to your .bash_profile.
   `export ANDROID_HOME=<YOUR FILE PATH>` (or `set ANDROID_HOME=<YOUR FILE PATH>` in Windows)
4. Set an environment variable for new relic in your bash profile
   `export NEWRELIC_HOME=<YOUR FILE PATH>` (or `set export NEWRELIC_HOME=<YOUR FILE PATH')`
5. After you are all setup you can build and install to your connect phone or emulator with `$NEWRELIC_HOME/bin/nrandroid-ant clean debug install`

### If you are on Windows you also need
1. `JAVA_HOME` and `ANT_HOME`

### Trouble shooting:

#### If the needed files are not in git

Run `$NEWRELIC_HOME/bin/nrandroid-setup` 
This will ask you for your project directory and for your application key. If you don't have an application key you will need to get one by signing up at http://newrelic.com/
    
#### If you get `Unable to locate tools.jar. Expected to find it in /opt/java/64/jre1.7.0_17/lib/tools.jar` That means you don't have the java JRE.

