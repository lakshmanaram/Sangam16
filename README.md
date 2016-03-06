Text Bot
========
A fully functional bot that takes input using an android app and fetches the required book/file for the User from an ideal Record maintenance system (Libraries, Court File rooms, etc)

Important features
----
- The bot uses classic bluetooth HC05 for getting the input from the android application.
- RFID Reader is used to identify the book's location.
- 2 DOF arm is used to grip the book
- Very limited navigation is possible.
- Constraints: 3 books in each row, 2 rows in a rack.

TODO
----
- Integrate the image recognition part of the code to the bot to give a wider range of coverage compared to RFID.
- Try to increase the range of RFID by manipulating the firmware.
- A better navigation system using line-follower or image recognition.
- A better gripper arm that can work in ideal scenario.
- updating bluetooth v2 to BLE
