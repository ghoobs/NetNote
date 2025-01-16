# Meeting Agenda

| Key           | Value                                            |
|---------------|--------------------------------------------------|
| **Date:**     | 15-01-2025                                       |
| **Time:**     | 16:45-17:30                                      |
| **Location:** | Drebbelweg PC1-C5                                |
| **Chair:**    | Rares Fetele                                     |
| **Minute Taker:** | Femke Knibbe                                     |
| **Attendees:** | Femke, Rares, Kaja, Chahid, Tristan, Kosmas + TA |

**Total Time:** 45 minutes

---

### 1. Opening and Check-In *(3 min)*
- **Welcome:** Welcome attendees and initiate the meeting.
- **Check-In:** How is everyone doing?

---

### 2. Announcements by the Team *(5 min)*
- **Updates from each member:**
    - What tasks have you completed since the last meeting?
    - Check whether the priorities have been accomplished.

---

### 3. Approval *(3 min)*
- **Approval of the Agenda:** Does anyone have any additions?
- **Approval of Last Minutes:** Did everyone read the minutes from the previous meeting?

---

### 4. Announcements by the TA *(4 min)*
- Feedback or updates from the TA.

---

### 5. Talking Points *(25 min)*
#### **Progress Review** *(10 min)*
- Are we on track? What is falling behind?
- Review all feedback and plan on how to solve these issues.
- **Discuss Known Bugs:** Identify and prioritize bug fixes.
- **Review of Current Feature Status:** Discuss the current progress of each feature and identify areas for improvement.

#### **Future Development** *(10 min)*
- Create a list of remaining tasks and divide them.
- Set a date for stopping work on the project before the deadline.
- Redistribute unfinished tasks in case of difficulties.

#### **Additional Comments or Topics (Project-Related):** *(5 min)*
- Open discussion on any other relevant project-related topics.

---

### 6. Summary and Closure *(5 min)*
- **Feedback Round:** What went well and what can be improved next time? *(2 min)*
- **Question Round:** Does anyone have anything to add before the meeting closes? *(2 min)*
- **Closure:** Final remarks by the Chair and meeting adjournment. *(1 min)*

---

### Notes:
#### 1. Opening:
- Meeting started at 16:48
- **What did everyone do this week:**
    - Kosmas: working on collections, almost finsished with works, some error in commons.
    - Kaja: fixed note saving bug, gui for edit collection window.
    - Tristan: changed search functionality, worked on tag functionality, add multiple tags to notes, same tags can be used, added some tests.
    - Femke: still working on embedded files, implementations are close to being finished. 
    - Chahid: automated change sync, finished most of it, small issue.
    - Rares: worked on tags from the front side, can now select tags and filter, still some bug fixes left to do this week

#### 2. TA Announcements: 
- Last formative feedback, from now on only summative 
- Code freeze is next sunday (9th?) instead of friday; cant access gitlab and make changes anymore.
- Buddycheck deadline is somewhere in week 10
- Need to make a README
- Program must compile for everyone, pull everything from scratch (delete setup or test with another) to find possible bugs.
- Going through rubric can easily make for a higher grade. 
- Running client without server should give a popup saying that there is not server. 

#### 3. Team announcements: 
- Adding a new note still gives out the same name.
- Plan meeting this week to go through the rubrics and find all things to change and divide tasks.
- Collections need some structures in frontend mostly.

#### 4. Accessability:
- Some small changes, navigate with other keys/ shortcuts. 
- Multimedia for buttons such as add, save etc. (like we already have for language switch)

#### 5. Bugs:
- Note should only update every few seconds not immediately since it now gives error when you empty the note or if you encounter the same title.
- If you delete all notes and then add a new one, you cannot see the new note in UI. (maybe fixed with refreshing).
- Refresh button needs implementation.
- Application does not fully close because of Markdown.
- When you search a note and try to delete that note, it can't because the name is not found. 
    - When searching a note the title is actually being changed 
    - This can also give other errors when working with other characters. 
    - Easiest fix is deleting the bold lettering when searching. 
- Implementation for deleting a note needs some changes in the order for the exception.
- Some issues with translation functionalities (such as; labels are not all translated). 

#### 6. Division of tasks:
- **What will everybody do the next week:** 
    - Kosmas: working on collections, refactor frontend collections.
    - Rares: Continue on tags, implement other things, fix bugs from translations. 
    - Tristan: Adding search features for tags. Tags and search functions should intersect. 
    - Chahid: Bug fix for automated change sync. 
    - Femke: Continue working on same tasks from embedded files. Tags need to be added in embedded files.
    - Kaja: Continue working on tasks and bug fixes. 

#### 7. Closing:
- All code should be done the saturay before the deadline so possible error things can be fixed on sunday. 
- Meeting tomorrow (16th) at 2PM to look through everything (partially online). 
- Meeting closed at 17:19. 


