# South Park downloader
_**Version 0.1**_

A simple to use GUI application for downloading episodes from South Park website.

## Requirements
* At least Java version 13. JDK build can be downloaded from [OpenJDK](https://jdk.java.net/13/).
* MKVMergeTool executable, as part of the [MKVToolNix](https://mkvtoolnix.download/downloads.html). (Already included inside Windows build)

## Installation

### Windows (64 bit)

* Download and extract the build from [here](http://bit.ly/3bcBaWT).
* Make sure you downloaded [JDK 13](https://jdk.java.net/13/), extracted it to a known location and added the `/bin` folder to the environment variables System path (remove earlier versions of Java from system path if needed). To check if installed successfully run `java -version` in cmd.
* Inside root of the build run command `java -jar spd2.jar` or double click on `run.vbs`

### Linux
* Currently starts up, but MKVMerge part doesn't work properly. Will update the project with a fix.

## Usage
* For the first time use go to the `Settings` tab and set download dir path and MKVMerge path. (Windows build has MKVMerge path already set)
* In browse tab browse the **repos** on the left for the desired content. Creating and managing repos explained in **Repos section** of this document.
* When traversing through repos, left click on `seasons` or `episodes` to open download details screen on the right. Click on seasons if you wish to download the whole season or on an episode to download individual episodes. Choose resolution and click on download.
* Go to `Downloads` tab to check active downloads. Menu of available options opens when right clicking on a download item.

## Repos
* Default repo with all the available content as of the date of posting this document is available as part of the build package, or can be downloaded from [here](http://bit.ly/39RT14W).
* Repos are saved in `.json` format, located inside `<app root>/repo` folder. The app will recognize any json file inside the dir as a repo and will try to read it.
* **Building repos:**
	* If you wish to build your own repo, you can use the built in GUI options by clicking the button `add` to add a repo, and then right clicking on repo to add season elements and right clicking on seasons to add episode elements.
	* Or you can create a `.json` file manually by following [this template](https://pastebin.com/raw/dsje6gi3).
	
* **Adding episodes:**
	* 	To add an episode you have to provide episode `mgid` to the repo. To obtain the episode mgid, visit the South Park episode link, inspect the html code and search for div attribute `data-mgid`. Paste the content of the attribute into the repo

## Information/ bugs
* Because of MKVMerge limitations, episodes that are downloaded  with the same name are automatically overwritten, without any warning.
* This app is not a VPN, it can access content only available in your region. If South Park episodes are not available in your region, app will probably not work.