                      # Meeting Agenda

| Key           | Value                                            |
|---------------|--------------------------------------------------|
| **Date:**     | 22-01-2025                                       |
| **Time:**     | 16:45-17:20                                      |
| **Location:** | Drebbelweg PC1-C5                                |
| **Chair:**    | Chahid                                           |
| **Minute Taker:** | Kaja                                             |
| **Attendees:** | Femke, Rares, Kaja, Chahid, Tristan, Kosmas + TA |

**Total Time:** 35 minutes

---

### 1. Opening and Check-In *(3 min)*
- **Welcome:** Welcome and initiate the meeting.
- **Check-In:** How is everyone doing?

---

### 2. Announcements by the Team *(5 min)*
- **Updates from each member:**
    - What tasks have you completed since the last meeting?
    - Check to what extent the priorities discussed last week have been accomplished.

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
- **Discuss Progress:** What is currently being worked on + all progress since last meeting
- **Discuss Known Bugs:** Again, check what bug fixes have been done, and which still can be done
- **Review of Current Feature Status:** Discuss current feature status, including extra additions for full points
  & implementation of automated change synchronization

#### **Future Development** *(10 min)*
- Look at what still needs to be done, which would bring the grade up the most, and considering the time frame divide those tasks
- Redistribute unfinished tasks in case of difficulties, looking at if we should still finish the automated change synchronization, or focus on other features.

#### **Additional Comments or Topics (Project-Related):** *(5 min)*
- Open discussion on any other relevant project-related topics.

---

### 6. Summary and Closure *(5 min)*
- **Feedback Round:** What went well and what could have been better *(2 min)*
- **Question Round:** Does anyone have anything to add before the meeting closes? *(2 min)*
- **Closure:** Final remarks by the Chair and meeting adjournment. *(1 min)*


## Notes:
#### Meeting started at 16:45
- **Recent progress/problems**
  - Web Sockets - help needed with figuring out how to implement them, however even if they don't fully work they should still be kept for a possibly higher Technology grade
  - Embedded files - almost done, renaming files left
  - Tags - completed, but there is a small bug
  - Collections - should be done by Friday, 
  - Translations - add one more language for an excellent grade (extra feature) + translate everything(scene title, popups, confirmations (Alerts))


#### 2. TA Announcements:
- Buddycheck deadline is Friday in week 10
- Need to make a README
  - shortcuts
  - adding files by right-clicking
  - keyboard navigation
  - all important additions (extra features)
- Program must compile for everyone, pull everything from scratch (delete setup or test with another) to find possible bugs.


#### 3. Team announcements:
- Gitlab will be very slow on Sunday, especially during the evening so we agreed on an internal deadline - Saturday night
- Everything from the Accessibility document is implemented (not counting the undo, which we decided not to do)
- Meeting on Friday during the WDT labs to go over all rubrics again


#### 5. Bugs:
- Application does not fully close because of Markdown.
- When a tag gets clicked the "Select tags" text disappears (possibly not a big problem)
- When filtering, you can add a note with a title that already exists, but got filtered out - there should be a list of notes that doesn't get changed and a separate one for filtered
- The language of the EditCollections Scene gets initialized once at the beginning, but doesn't get updated when the language gets changed


#### 6. Division of tasks:
- Everyone will continue with their tasks from the previous week
- Additionally:
  - Femke - adding the .css file, while keeping in mind a high contrast
  - Kosmas and/or Tristan: from the Technology rubric: Services are not used, meaning that Controllers directly interact with the repositories.
  - Tristan - make the API "more RESTful"

#### Meeting closed at 17:32. 