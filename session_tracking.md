# Pair Programming Session Tracking




<br>

#### Tracking Format - REMOVE BEFORE SUBMISSION
 - Dates use heading 2 (##)
 - Times use heading 3 (###)
 - Template at end of document




<br>

## 02/10/26
### 14:00 - 15:00
**Driver**: Cameron Russell

**Navigator**: Jake Fordy

**Session Objective**: Understanding the Problem

**Top Five Commits**:
 - Started planning implementation of card playing simulation in README.md.
 - Added more implementation notes.

**Commit Summary**: Planning implementation.




<br>

## 05/10/26
### 13:30 - 14:00
**Driver**: Cameron Russell

**Navigator**: Jake Fordy

**Session Objective**: Understanding the Problem

**Top Five Commits**:
 - Added gitignore file.
 - Added run scripts for Windows, MacOS, and Linux, to make the build and run process faster.
 - Started writing the main classes.
 - Updated README.

**Commit Summary**: Automated build scripts and more implementation notes in README only.

### 18:00 - 18:30
**Driver**: Cameron Russell

**Navigator**: Jake Fordy

**Session Objective**: Understanding the Problem

**Top Five Commits**:
- Added new constructor to Card class to allow automated creation of a Card by specifying
  the suit and rank indices, and wrote more Javadoc.

**Commit Summary**: More implementation notes in README only.




<br>

## 08/10/26
### 14:30 - 17:30
**Driver**: Cameron Russell

**Navigator**: Jake Fordy

**Session Objective**: Understanding the Problem

**Top Five Commits**:
 - Fix: replace backslash with forwardslash for linux run.sh and README.md
 - Fix: update gitignore with linux forwardslash bin folder
 - Fix: gitignore forwardslash and idea folder
 - docs: Added a Markdown file to track our pair programming sessions.

**Commit Summary**: Setting up project structure.




<br>

## 12/10/26
### 10:30 - 13:30
**Driver**: Cameron Russell

**Navigator**: Jake Fordy

**Session Objective**: Work on Implementation

**Top Five Commits**:
 - fix: removed Suit and Rank enums because a Card only has a value in this project,
   and hence updated Card class to reflect this.
 - feat: added Pack class with methods to add and remove cards.
 - feat: successfully written readPackFile() and tested validation rules using test_four.txt!
 - feat: JUnit setup and CardTest file
 - feat: have written addCard() and removeCard() logic in Deck class.

**Commit Summary**: Cleaned old incorrect code, wrote pack class, implemented pack file
validation, and setup JUnit.


### 17:30 - 20:00
**Driver**: Cameron Russell

**Navigator**: Jake Fordy

**Session Objective**: Work on Implementation

**Top Five Commits**:
 - feat: added Javadoc to all methods in the Deck class and fixed the logic in addCard()
   and removeCard().
 - docs: wrote Javadoc for the readPackFile() method within the CardGame class.
 - fix: Converted CardGame.main() to create an instance of CardGame rather than everything
   being static; allows threads (players) to communicate with main thread (program).
 - feat: Converted addCard() and removeCard() methods of the Deck class to use synchronisation.
 - feat: Wrote main bulk of Player class; including run(), giveCard(), setLeftDeck(), and
   setRightDeck(), but TODOs remain, requiring functionality to be added.

**Commit Summary**: Wrote lots of Javadoc, implemented synchronisation, and wrote some player
functionality.




<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>

TEMPLATE:

## _DATE_
### _TIME_FROM_ - _TIME_TO_
**Driver**: _NAME_

**Navigator**: _NAME_

**Session Objective**: _OBJECTIVE_

**Top Five Commits**:
 - _COMMIT_1_
 - _COMMIT_2_
 - _COMMIT_3_
 - _COMMIT_4_
 - _COMMIT_5_

**Commit Summary**: _SUMMARY_
