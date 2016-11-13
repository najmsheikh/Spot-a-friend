# Spot-a-Friend
You know that someone is clearly in front of you. Who is it? You know that both Emily and Jane have shortish hair, a roundish face, but the world is much too blurry for you to correctly discern what is happening. You wish there were a way for you to know instantly who was in front of you without always having to ask. But instead, you're left in constant darkness with what is going on around you.

Because of that need, our team developed a cool and interesting way to connect the visually impaired with the world around them using Android phone technology and Facebook. **Spot-a-Friend was part of HackPrinceton Fall'16 and won 'Best Wearable Hack' and 'Facebook's 2nd Favorite Hack'.**

## Overview
Spot-a-Friend allows visually impaired members of the community to better see the world around them. The process is simple. You open up the Spot-a-Friend app and place the phone into the VR headset. From there, the view the user sees will be that of the Android camera view. The person can then look around and, when they feel that there are people in the area whom they can't identify, they can press a button on the headset and/or potentially utilize a voice command -- "Spot a friend".

From there, the friend is identified and their name and general location (e.g. "Rachel is to your right") is relayed back to the user.

## Technical details
Essentially, the solution is split into two main platforms: and Android VR application, and a Flask backend server. Both communicate with each other via SocketIO.

The user installs the Spot-a-Friend application on their phone, and then places their phone [in a VR headset](https://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/000/443/188/datas/gallery.jpg). The app displays a live camera feed (split and curved, to accomodate for the VR lens) and stands-by until the user triggers the image capture. They can do so either by pressing one of the physical buttons or by saying the phrase, "Spot a friend!".

For the facial recognition, we realized that most users would not want to (let alone, in most cases, even be able to) train with a large image dataset. As such, we utilized Facebook’s picture ‘tagging’ feature to act as our facial recognition kit. Since the dataset was already trained by Facebook using images of the user and their friends, we could abstract away the grunt work. The entire process involved simulating a user session by maintaining a personal cookie, temporarily uploading the required photo with limited permissions, and finally scraping the tagged information off of the webpage to get the recognized person’s name.

The project is divided into two branches:

* **master**	contains *partial(!)* code for the Android application
* **backend** contains the server code which handles facial recognition via Facebook
