#Disaster Response Emergency Workspace (DREW)  
 Disaster Response Emergency Workspace (DREW) is an android application that is used to notify users of the app if there is an emergency or disaster occurring.

#Requirements or Dependencies Used  
 Android Package is used for all the basic functionalities.  
Firebase and its package are used to handle the data both in writing onto the real time database as well as reading from it. Additionally it is used for the authentication processes including login and registration. Datasnapshots are frequently used to scan through the data of a node from a query that will be used in the program.  
OSMDROID package has been used for the map activities, including print the map for the disasters to be displayed in as well as the map in the add records screen. It was used to take the longitude and latitude for the markers that are needed.  
The tileprovider that is used for the map to print along with OSMDROID is MAPNIK a TileSourceFactory that will provide the tiles for the map to be printed
Nomanatim queries are used for locating the nearby hospitals of the disaster, this provides the information and the location of the nearby hospitals as Point of Interests that is printed onto the map and saved onto the database.

#Developer Environment  
The code is written in java as it is the official language of android development. The main compiler used is Android Studio. Android Studio has a virtual device that can run the app for us during our testing and development.


#MANUAL   
1.) After launching the application login an account, or if you are a new user, register a new account.  
When registering an account, select an appropriate role:  
Basic - basic account with basic functionality  
Responder - responder account has the ability to respond to events on the main menu, this will put their name on the responder list of the event  
Admin - Admin accounts can only be created when the user knows the admin password to create an account.  
        Additional features that the admin has is they get a delete button to delete events that have passed.  
2.)Once logged in, user will land on the main menu where the map of the Philippines will be displayed.On the map, if there are events recorded on the database there will 
  be icons corresponding to the disaster on the map. When the user clicks on the icon it shows the type of the disaster, the person who submitted it, and then shows all the icons for nearby hospitals when clicked it shows the details of the hospital.  
3.) Swipe right to show the navigation bar, here there are options for the user to pick from, to navigate to the other parts of the program. Click on the Add Event.  
4.) The Add Event screen, this is where a user will be able to add a new event to the database. In the Add Event screen, it will display an empty map as well as a spinner at the bottom. When you click on the map it will display an icon depending on the event selected in the spinner. Once the user clicks save , wait for the notification to know that the icon has been saved to the database.  
5.) When a new event is created it will notify all the users about that event.   
6.) Go back to the Main menu through the navigation bar. The newly added icon will be on the map. When you click it, with a responder account, a respond button will appear on the bottom left, when it is clicked your name will be added to responder list of that specific event. However when you press the icon with an admin account, it will show a delete button on the bottom left. The delete button means that an admin can choose to move the event to the record, this would mean that the disaster has already passed.  
7.) Go back to the navigation bar and press View Records. On this screen this will show all the disasters that have already passed and have been deleted off the map by an admin.  Each event has an item on the list, if you click one event it will show, the location, date, user who submitted, responders as well as all the nearby hospitals that were recorded at that time.  
9.) The last option on the navigation bar, is Account Options. In the Account Options Screen, the user will have control of their account, they will be able to change their password, email as well as role. These all work the same as the register screen. When you change one thing from your account, and its complete this will log you out of the application.  
10.) Lastly, Sign out button will sign out the user.  


#Use Cases Achieved
Login 
Register Account
Main Menu
Data Entry
Set roles
Remove Records
Checking Database
View Record
Update/Edit Account
Notification System


#Revision logs and information about contributors
April 18, 2021
-Started using Android Studio to start with the coding
-Created Github Repository
-Created XML files for Login, register, main menu GUI

April 20, 2021
-Implemented Firebase to the login
-Created functions for the GUI elements to work onClick
-Implemented Firebase to the register
-New users automatically gets saved in the firebase authentication as well as the fire base real time database

April 29, 2021
-Draws map for the main menu as well as the add record screen
-Fix multiple glitches with the map not responding
-Register now asks for email verification
-GUI design updates 
-Added new Navigation bar for the main menu, to be added to the other screens after the user logged in

May 5, 2021
-Map can now save location of a disaster when clicked on the map
-When a disaster icon is added to the map, the nearby hospitals get displayed on the map
-New drawable icons for each disaster and the nav bar have been added

May 10, 2021
-Markers that are saved in the Firebase real time database get printed onto the map as well as their respective on click function.
-Account options screen is now completed, includes the change email name and password which would update the relevant information on the firebase
-Zooming in onto the location as well as the displaying of nearby hospitals was added.
-Spinners are added in some screens such as account roles to be able to pick your role.
 
May 20, 2021
-Notification system was added to the application.
-All bugs including redundancy in the list of responders and events in firebase.

May 23, 2021
-View records now show all nearby hospitals per event as well as the data and the list of responders
-GUI visual improvements throughout the program 
-Currently use cases 1-9 are now complete

June 01, 2021

-signout function fully functional
-all navigationbars are functional 
-all use cases are now complete

#Firebase
To be able to use the account authentication and Realtime database features, access the tools section of android studio and select firebase. Various other features can be accessed and any given project can be connected to Firebase through this method. A developer should have a gmail account to be able to freely use and connect projects to Firebase.


https://developer.android.com/training/basics/firstapp
https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
https://cloud.google.com/firestore/docs/client/get-firebase
https://wiki.openstreetmap.org/wiki/Nominatim
