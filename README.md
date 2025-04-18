# Kabarak University IT Project - ELderly care Android App


Tutor: Dr Kipkebut

## Contents

1. [Summary](#summary)
2. [Getting Started](#getting-started)
   1. [Prerequisites](#prerequisites)
   2. [Installing](#installing)
3. [Deployment](#deployment)
   1. [Basic](#basic)
   2. [Testing](#testing)
   3. [Multiple emulators or devices](#multiple-emulators-or-devices)
   4. [Video call](#video-call)
   5. [Notes](#notes)
4. [Feature Instructions](#feature-instructions)
   1. [Sign in/Sign up](#sign-in)
   2. [Drop down settings](#drop-down-settings)
   3. [Tabular interface](#tabular-interface)
   4. [Chat (Feature 1)](#chat)
   5. [Voice/Video call (Features 2, 5)](#voice-video)
   6. [Maps (Feature 5)](#maps)
   7. [Contacts (Feature 4)](#contacts)
   8. [Request carer (Feature 7)](#request-carer)
   9. [Broadcast SOS (Feature 8)](#broadcast-sos)
   10. [Fall detection (Feature 9)](#fall-detection)
   11. [Toggle POI on map interface (Feature 10)](#toggle-poi)
   12. [Carer can alter the assisted person’s route (Feature 11)](#assisted-route)
   13. [Favourite places (Feature 13)](#favourite-places)
   14. [Annotations (Feature 14)](#annotations)

## Summary

Android application designed to assist elderly and disabled people with navigation. Users authenticated via Google authentication. While signed in, users may access the following features:

* Text chat (Feature 1)
* Voice/video call (Features 2 and 5)
* Maps (Feature 3)
* Contacts (Feature 4)
* Request Carer (Feature 7)
* Broadcast SOS (Feature 8)
* Fall detection (Feature 9)
* Toggle POI markers (Feature 10)
* Favourite place (Feature 13)
* Annotations (Feature 14)

The application is designed for Android API 35
Minimum Requirement: Android API 26

## Getting Started

### Prerequisites

* Latest version of Android Studio is installed on device

### Installing

1. Clone repository

for ssh

```bash
git clone git@github.com:tomleejumah/Elderly-Care-Disabled-Assistance-App.git
```

for https

```bash
git clone https://github.com/tomleejumah/Elderly-Care-Disabled-Assistance-App.git
```

you can as well just download it via that green button

2. Checkout branch 'master'

```bash
git checkout origin master
```

## Deployment

### Basic

1. Build project
2. Run project
   1. For Emulator
      1. Install Android emulator using **tools>SDK Manager**
      2. Choose a virtual device using **tools>AVD Manager**
      3. On clicking run, choose the virtual device
   2. For Android device
      1. Enable app debugging
      2. Connect device via usb cable
      3. On clicking run, choose the device

### Testing


* The androidTest folder has UI testing for the core fragments attached to the main activity; this includes:
  * Maps fragment
  * User fragment
  * Favourite fragment
* The Test folder contains unit testing for the processing of a nearby place search  


### Multiple emulators or devices

To test some of the application's features, multiple emulators/devices may be needed:

1. On each emulator/device, ensure you sign up with a different account
2. For features involving a carer, ensure ONE (not both) emulator/device is signed up as a carer (sign up details in **Feature Instructions** below)

### Video call

To test the video call feature:

1. Create a new virtual device or edit an existing one
2. Click on "Show Advanced Settings"
3. Set the front and back cameras (recommended front=webcam, back=emulated)

### Notes

* Currently works better with devices rather than emulators
* On an emulator, the video call might be extremely laggy (works quite smoothly on devices)

## Feature Instructions

* <a name="sign-in" />Sign in/Sign up
  * Upon opening the application you will be required to provide a Google account for authentication
  * After pressing the Google button, please choose the account you would like to use
  * If the account you wish to use is not present, click the add account button and fill in your details after being redirected
  * If this is your first time signing in, you will be given an account
  * Account details come from your Google account so the only information you are required to enter is whether or not you are a carer
* <a name="drop-down-settigs" />Drop down settings
  * By pressing the three dots in the top right corner of the application you enter a menu that allows you to sign out
* <a name="tabular-interface" />Tabular interface
  * The bottom bar has a set of main features for ease of access
  * To access these feature, simply press them on the bar
  * The order of access from left to right is as follows: Maps | Contacts | Favourite Places | Settings
* <a name="chat" />Chat (Feature 1)
  * To use chat functionality, you must first have a contact (see [*Contacts (Feature 4)*](#contacts) below)
  * Navigate to the Contacts tab at the bottom
  * Click on a contact to initiate a chat with them
  * Initiating chat with a contact opens an interface of chat history with the contact
  * If necessary, scroll to see previous messages
  * By pressing the text box, you open the texting interface
  * Press send to send a completed message
  * The message will be added to the list of messages between you and the contact
  * You can also send a chat message to a person you are "connected" to (see [*Request carer (Feature 7)*](#request-carer) below)
  * Once connected, you can press the message button in the top right corner of the screen, which will take you to the same chat interface described above
* <a name="voice-video" />Voice/Video call (Features 2, 5)
  * To use voice chat functionality, you must first be connected to a carer (see [*Request carer (Feature 7)*](#request-carer) below) and be on the Maps tab
  * Once connected, you can press the call button located second from top on the right side of the screen
  * This will take you to the calling interface
  * You can send a call request by pressing the call button second from bottom on the right of the screen
  * The call button on the map will turn green on the other person's device, letting them know you are in the call
  * Once both devices have pressed call, they will be connected
  * To mute/unmute yourself, press the Microphone icon (third from bottom on the right)
  * By default, video feed is turned off, to toggle video on/off, press the Video button (fourth from bottom on the right)
  * When video feed is on, camera may be switched by pressing the new Switch Camera button (fifth from bottom on the right)
  * To end the call, both devices must press the End Call button, which will have replaced the Call button after initiating a call
  * At any point during a call, either user may switch back to the map view by pressing the map button (located in the bottom right of the screen)
  * Users may freely switch back to the call view by pressing the call button again
* <a name="maps" />Maps (Feature 3)
  * Upon signing in you will be redirected to the Map tab
  * You can switch back to the Map tab at any moment by clicking the Map tab at the bottom
  * Your current location is specified by a blue dot on the map interface (ensure location services are turned on)
  * You can search for destinations by typing them into the search located at the top of the screen
  * You can route to a destination by clicking on it in the search results
  * A user is able to search for nearby destinations of a type by pressing the corrusponding button after activating the nearby interfae from the floating action button in the bottom left of the interface
* <a name="contacts" />Contacts (Feature 4)
  * To use contacts, navigate to the Contacts tab at the bottom
  * A list of contacts should appear on the screen
  * Scroll down to view multiple contacts if necessary
  * You can open the contact addition interface by clicking the floating action button in the bottom right corner
  * This interface has three functions:
    * Lower left calls a dialog that allows the user to add a contact via email
    * Upper right initiates a camera view for scanning a QR code
    * Middle right displays a user's QR code to their screen
  * A contact can only be added if the email has been signed up to use the app
  * Here are a few contacts you may use for testing purposes:
    * aduffy1@student.unimelb.edu.au
    * asims@student.unimelb.edu.au
* <a name="request-carer" />Request carer (Feature 7)
  * A carer can only be requested from an account that is NOT a carer
  * To request a carer, navigate to the Map tab at the bottom of the screen
  * Press the "REQUEST A CARER" button at the bottom of the screen
  * A request will be sent to all Carer accounts within 500m
  * On the Carer's device, a notification will be shown
  * The Carer can accept the request, and be connected, or dismiss the request
  * Once connected, an icon will appear showing the location of the other person
  * On the Carer's device, additional buttons will become visible (see [*Annotations (Feature 14)*](#annotations) below)
  * To disconnect, either user may press the "DISCONNECT" button at the bottom of the screen
* <a name="broadcast-sos" />Broadcast SOS (Feature 8)
  * To broadcast an SOS, simply press the SOS button located at the top of the screen
  * Once pressed, an SOS message is sent to all Carers within 1km
  * Incoming SOS messages appear as a notification and also place a marker on the carer's map
  * Once an assisted person sends an SOS request the SOS button will change to an OK button, pressing this button will notify the carers that they no longer require immediate assistance and the marker will be removed from their map
* <a name="fall-detection" />Fall detection (Feature 9)
  * When a fall is detected on a device, an automatic SOS message is sent
* <a name="toggle-poi" />Toggle POI on map interface (Feature 10)
  * To toggle POI markers, navigate to the Settings tab at the bottom
  * Check the boxes for the POI types you would like to have on your map interface
  * Press "Apply" to confirm the change
* <a name="assisted-route" />Carer can alter the assisted person’s route (Feature 11)
  * A carer may alter the assisted persons route
    * Once connected (see [*Request carer (Feature 7)*](#request-carer)) a carer may input a route (see [*Maps (Feature 3)*](#maps) above)
    * This will then appear on both users map, routing from the assisted persons location
  * This works with favourite places (see [*Favourite Places (feature 13)*](#favourite-places) below)
* <a name="favourite-places" />Favourite places (Feature 13)
  * Favourite places may be added from the Map
    * Navigate to the Map tab at the bottom
    * Get route to a location (see [*Maps (Feature 3)*](#maps) above)
    * Press the blue destination marker on the map
    * Press the dialog that appears to add the location to your favourites
  * To access favourite places, navigate to the Favourites tab at the bottom
  * Clicking on any of the favourite places will switch to the Map tab and route to the location
* <a name="annotations" />Annotations (Feature 14)
  * Once a Carer connected to an assisted person (see [*Request carer (Feature 7)*](#request-carer) above), new UI buttons should appear
  * Press the Edit button located in the top left to begin editing annotations
    * This should make new buttons appear
  * Tapping on the map adds "Geo Points", and will draw an arrow between them
    * Two taps should produce an arrow
    * Any subsequent taps will add a new arrow
  * Pressing the Undo button will undo the previous tap
  * Pressing the Send button will send the annotations to the assisted person and draw it to their screen
  * Pressing the Delete button will clear all annotations on both devices
  * Pressing the X will close the annotation UI
