#!/bin/sh

mv ../signed_apks/fanClub.apk ../signed_apks/$(git log --pretty=format:'%h' -n 1).apk
