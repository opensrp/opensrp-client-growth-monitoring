[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-growth-monitoring.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-growth-monitoring) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-growth-monitoring/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-growth-monitoring?branch=master)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4a58cd4e1748432780ac66a9fbee0394)](https://www.codacy.com/app/OpenSRP/opensrp-client-growth-monitoring?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-growth-monitoring&amp;utm_campaign=Badge_Grade)


[![Dristhi](https://raw.githubusercontent.com/OpenSRP/opensrp-client/master/opensrp-app/res/drawable-mdpi/login_logo.png)](https://smartregister.atlassian.net/wiki/dashboard.action)

## TABLE OF CONTENTS

* [Introduction](#introduction)
* [Features](#features)
* [Z-Score](#z-score)
* [App Walkthrough](#app-walkthrough)
* [Developer Documentation](#developer-documentation)
   * [Pre-requisites](#pre-requisites)
   * [Installation Devices](#installation-devices)
   * [How to install](#how-to-install)


# Introduction

This OpenSRP Module/app provides Growth Monitoring (Weight, Height) recording, updating, editing and charting capabilities. It also provides the **z-score** of a patient from their Growth Monitoring (Weight, Height).

# Features

The app has the following features:

1. It enables one to record the current or past Growth Monitoring (Weight, Height)
2. It enables one to edit a patient's Growth Monitoring (Weight, Height) record
3. It enables one to view the patient's Growth Monitoring (Weight, Height) over time on a graph
4. It enables one to view the **z-score**
5. It enables one to determine a patient's Growth Monitoring (Weight, Height) status from the z-score

# Z-Score

The z-score is a score calculated from the Growth Monitoring (Weight, Height) & age of a patient to determine their Growth Monitoring (Weight, Height) status i.e. **overweight, underweight, average Growth Monitoring (Weight, Height) ...**

This score is calculated using values provided by WHO.

#### Z-score color codes

Color | Meaning | Appropriate Z-score
----- | ------- | -------------------
Red | Underweight/Overweight | -2 and above OR +2 and above
Green | Good! :thumbsup: | Between -2 and +2
Dark Blue | Dangerous Growth Monitoring (Weight, Height) | Over +3 or below -3

You can easily tell the Growth Monitoring (Weight, Height) status by checking the z-score line on the graph and where it falls within the z-score indicators. The z-score line indicators use the same color-codes [above](#z-score-color-codes)

# App Walkthrough

The app is easy to use and has five important screens

1. When you open the app, the following page is displayed.

![Main Page](https://user-images.githubusercontent.com/31766075/30366688-a33394c6-9874-11e7-9d21-1a408dba867f.png)

 * The **Top Section** has two buttons:
     - **Record Weight Button** which enables one to record `current` or `past Growth Monitoring (Weight, Height)`

     ![Record Weight Screenshot](https://user-images.githubusercontent.com/31766075/30361164-1c9a2d64-985e-11e7-8852-099b6d55f577.png)
     ![Record Past Weight Screenshot](https://user-images.githubusercontent.com/31766075/30361167-1ca12718-985e-11e7-9863-bb4a89efa134.png)
     - **Weight Graph Button** which displays a dialog with the age respective z-scores over the patients lifetime on a graph. The specific values are also displayed below the graph with the patient's age respective age.
     ![Weight Graph Screenshot](https://user-images.githubusercontent.com/31766075/30361166-1ca12f92-985e-11e7-97b7-2ab3ed8bebe6.png)


* The **Bottom Section** displays the last 5 Growth Monitoring (Weight, Height)s recorded in the patient's lifetime. 
    - Each row has:
        + The **Patient Age** eg. `0d`
        + The **Patient Weight** eg. `3.8 unit`
        + The **Edit Weight Button** except the `birth Growth Monitoring (Weight, Height)` which cannot be changed

        ![Edit Weight Screenshot](https://user-images.githubusercontent.com/31766075/30361163-1c99caf4-985e-11e7-8e3e-f985dff40a7a.png)


# Developer Documentation

This section will provide a brief description how to build and install the application from the repository source code.

## Pre-requisites

1. Make sure you have Java 1.7 to 1.8 installed
2. Make sure you have Android Studio installed or [download it from here](https://developer.android.com/studio/index.html)


## Installation Devices

1. Use a physical Android device to run the app
2. Use the Android Emulator that comes with the Android Studio installation (Slow & not advisable)
3. Use Genymotion Android Emulator
    * Go [here](https://www.genymotion.com/) and register for genymotion account if none. Free accounts have limitations which are not counter-productive
    * Download your OS Version of VirtualBox at [here](https://www.virtualbox.org/wiki/Downloads)
    * Install VirtualBox
    * Download Genymotion & Install it
    * Sign in to the genymotion app
    * Create a new Genymotion Virtual Device 
        * **Preferrable & Stable Choice** - API 22(Android 5.1.0), Screen size of around 800 X 1280, 1024 MB Memory --> eg. Google Nexus 7, Google Nexus 5

## How to install

1. Import the project into Android Studio by: **Import a gradle project** option
   _All the plugins required are explicitly stated, therefore it can work with any Android Studio version - Just enable it to download any packages not available offline_
1. Open Genymotion and Run the Virtual Device created previously.
1. Run the app on Android Studio and chose the Genymotion Emulator as the ` Deployment Target`










