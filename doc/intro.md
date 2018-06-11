# Introduction to simple-editor

This is a really simple text editor, motivated by rohitpaulk's Clojure editor,
with a few more features and tests (did they have CI?). It can handle a single
text file with any number of lines, though may cause issues if the number of
characters per line runs off the page. It can save files on exiting. Here are a
list of keystrokes that it takes.

<kbd>Escape</kbd> exits the program, prompting a save. Only by pressing "Y" will
your changes be saved, all other inputs would scrap your changes.

<kbd>Up</kbd><kbd>Down</kbd><kbd>Left</kbd><kbd>Right</kbd> moves the cursor up,
down, left, or right.

<kbd>Backspace</kbd><kbd>Delete</kbd> removes the character behind/on the
cursor.

<kbd>Home</kbd><kbd>End</kbd> moves the cursor to the start/end of the current
line.

