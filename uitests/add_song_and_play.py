#! /usr/bin/env monkeyrunner
from ui_test import UITest
from ui_utils import *

class AddSongAndPlay(UITest):
	"""Verify the user can add a song from the library on host phone and can then play that song"""
	def __init__(self):
		UITest.__init__(self)
			
	def execute_test(self):

		touch_with_cd(self.vc, CREATE)
		touch_with_cd(self.vc, MUSIC_LIBRARY)
		touch_with_cd(self.vc, ATP_AVALANCHE_ROCK)
		touch_with_cd(self.vc, NAVIGATE_UP)
		touch_with_cd(self.vc, PLAYLIST)
		touch_with_cd(self.vc, PLAY_PAUSE)
		print("AddSongAndPlay complete")

if __name__ == "__main__":
	t = AddSongAndPlay()
	t.execute_test()
