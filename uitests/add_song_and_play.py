#! /usr/bin/env monkeyrunner
from ui_test import UITest
from ui_utils import *

class AddSongAndPlay(object):
	"""docstring for AddSongAndPlay"""
	def __init__(self):
		UITest.__init__(self)
			
	t = UITest()

	touchWithCD(t.vc, 'Create')
	touchWithCD(t.vc, 'Music Library')
	touchWithCD(t.vc, 'Add To Playlist_Avalanche Rock')
	touchWithCD(t.vc, 'Navigate up')
	touchWithCD(t.vc, 'Playlist')
	touchWithCD(t.vc, 'Play/Pause')
	print("AddSongAndPlay complete")
