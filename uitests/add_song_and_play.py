#! /usr/bin/env monkeyrunner
from ui_test import UITest
from ui_utils import *

class AddSongAndPlay(UITest):
	"""Verify the user can add a song from the library on host phone and can then play that song"""
	def __init__(self):
		UITest.__init__(self)
			
	def execute_test():
		t = UITest()

		touch_with_cd(t.vc, CREATE)
		touch_with_cd(t.vc, MUSIC_LIBRARY)
		touch_with_cd(t.vc, ATP_AVALANCHE_ROCK)
		touch_with_cd(t.vc, NAVIGATE_UP)
		touch_with_cd(t.vc, PLAYLIST)
		touch_with_cd(t.vc, PLAY_PAUSE)
		print("AddSongAndPlay complete")

	if __name__ == "__main__":
		execute_test()
