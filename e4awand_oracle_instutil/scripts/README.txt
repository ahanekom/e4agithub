Excel4Apps - GL Wand 5 for Oracle: Installation Instructions
============================================================

Installations
=============

1) Register the XXE4A application
2) Move the glwand5.5.X.zip to your application server
3) Login to the application server as the application owner
4) Ensure you have source the environment file
5) Unzip the glwand5.5.X.zip file
6) Change directory to unzip destination folder
7) Execute $> ./install.sh --debug
8) Shutdown the application tier
9) Run AutoConfig - adautocfg.sh
10) Start the application tier

APPS_TIER Installations
=======================

1) Move the glwand5.5.X.zip to your application server
2) Login to the application server as the application owner
3) Ensure you have source the environment file
4) Unzip the glwand5.5.X.zip file
5) Change directory to unzip destination folder
6) Execute the following command $> ./install.sh --debug APPS_TIER
7) Shutdown the application tier
8) Run AutoConfig - adautocfg.sh
9) Start the application tier