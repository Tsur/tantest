
Android Tantest Application
===========================

(Note : Basically, the build status makes clear that this work has passed the build tests that are required in order for it to work as expected)

## What is Tantest?

Tantest is an application written in Java and based on android operative system. 
The word Tantest makes allusion to the own nature of the applicaton as it's an acronym from
Tanaj and Test, so it's an application where you can take and perform small tests or quizzes about the Tanaj 
or most commonly known in western countries as the Bible.The other part of the application allows users to 
add contacts, create new conversations and share with them the tests you make.

The application has two main components, the client side which is the application itself and from which can get the sources here.
The other component is the server to which the application connects to. The server is not available yet.

Features
--------


## How does it work?

First, you need to download the tantest application file(the apk file), or just generate it from sources. Once you have 
installed it on your phone, go to it and the first step is to register an account. First time, you can only register an 
account by using a phone since it's sent a sms to your phone to create your account. Later, you can register other 
devices as tablets, by registering an email.

Then, once you have an account/phone registered, you can start a 1)new test or you can start a 2)new conversation. This can be achieved
by touching the two small icons on the top-right corner of the device's screen respectively. Let's see each of them in detail.

Building ...

![Login Section](https://github.com/Tsur/tantest/blob/master/tantest-app/res/drawable-hdpi/e1.png?raw=true)

![Contacts Section](https://github.com/Tsur/tantest/blob/master/tantest-app/res/drawable-hdpi/e2.png?raw=true)

![Chat Section](https://github.com/Tsur/tantest/blob/master/tantest-app/res/drawable-hdpi/e3.png?raw=true)

![Sending a message](https://github.com/Tsur/tantest/blob/master/tantest-app/res/drawable-hdpi/e4.png?raw=true)


## Instrucctions to generate the apk from sources


1) Make sure Apache Maven is installed on your machine
	http://www.sonatype.com/books/mvnref-book/reference/installation.html
	(Make sure JAVA_HOME and MAVEN_HOME environments variables are set with PATH updated)
2) Make sure android-maven-plugin is properly configured
	(Note 1: Install Eclipse marketplace if using eclipse:  Help/Install new Software -> http://download.eclipse.org/releases/juno -> Marketplace Client)
	(Note 2: Install Android-maven-plugin from Marketplace:  android m2e)
	https://code.google.com/p/maven-android-plugin/wiki/GettingStarted
3) Download Tantest repo to some previosly selected location  
4) Move to root of Tantest repo before downloaded and run the command: mvn clean install

Note: Tantest uses JDK 7
Note 2: You must install actionbarsherlock and android-vcard as dependences in your local maven repository in order
to compile Tantest.
-------

Copyright &copy; 2013, ScripturesOS.

Members who have contributed to the project:
[Tsur Pabon](mailto:prucheta@gmail.com)(http://github.com/tsur).

Spanish translation &copy; 2013, 
English translation &copy; 2013, 

This work is licensed under a
[Creative Commons Attribution-Noncommercial-Share Alike 3.0 United States
License](http://creativecommons.org/licenses/by-nc-sa/3.0/us/).  

