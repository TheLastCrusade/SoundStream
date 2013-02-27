If you need to quickly replace all tabs in a directory with spaces, run the following sed script in the directory. This only applies to the files in that folder and does not account for any subfolders.

'''
sed -i 's/	/    /g' */java
'''

**Note:** the first group of spaces is the tab character. To produce this character in bash, use `CTRL + v + i`. The second group of spaces is simply four spaces.
