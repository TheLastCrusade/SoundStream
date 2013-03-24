#!/bin/sh

# This script is designed to add our GPL statement to the top of all of our
# source files

# Java source files
for i in ../SoundStream*/src/com/lastcrusade/soundstream/*/*.java
do
	if ! grep -q Copyright\ 2013\ The\ Last\ Crusade\ ContactLastCrusade@gmail.com $i
	then
		cat ../docs/copyrightJava.txt $i > $i.new && mv $i.new $i
	fi
done

# XML source files
for i in ../SoundStream*/res/*/*.xml
do
	if ! grep -q Copyright\ 2013\ The\ Last\ Crusade\ ContactLastCrusade@gmail.com $i
	then
		cat ../docs/copyrightXML.txt $i > $i.new && mv $i.new $i
	fi
done

# Edge case: /net/message
for i in ../SoundStream*/src/com/lastcrusade/soundstream/*/*/*.java
do
	if ! grep -q Copyright\ 2013\ The\ Last\ Crusade\ ContactLastCrusade@gmail.com $i
	then
		cat ../docs/copyrightJava.txt $i > $i.new && mv $i.new $i
	fi
done

# Edge case: AndroidManifest.xml and build.xml
for i in ../SoundStream*/*.xml
do
	if ! grep -q Copyright\ 2013\ The\ Last\ Crusade\ ContactLastCrusade@gmail.com $i
	then
		cat ../docs/copyrightXML.txt $i > $i.new && mv $i.new $i
	fi
done

# Edge case: SoundStream-test Parcelable.java
for i in ../SoundStream*/src/android/os/*.java
do
	if ! grep -q Copyright\ 2013\ The\ Last\ Crusade\ ContactLastCrusade@gmail.com $i
	then
		cat ../docs/copyrightJava.txt $i > $i.new && mv $i.new $i
	fi
done

# Edge case: CustomApp.java and CoreActivity.java
for i in ../SoundStream*/src/com/lastcrusade/soundstream/*.java
do
	if ! grep -q Copyright\ 2013\ The\ Last\ Crusade\ ContactLastCrusade@gmail.com $i
	then
		cat ../docs/copyrightJava.txt $i > $i.new && mv $i.new $i
	fi
done
