# esen-prototype

This project implements an HCD (Hardware Control Daemon) and an Assembly using 
TMT Common Software ([CSW](https://github.com/tmtsoftware/csw)) APIs for the purpose of 
prototyping the OPC UA HCD and testing with TwinCAT.  The TwinCAT project used for this
prototype is included in this archive at the top level directory with the extension .tnzip.

## Subprojects

* esen-prototype-assembly - an assembly that talks to the esen-prototype HCD - not used in PD prototyping
* esen-prototype-hcd - an HCD that talks to TwinCAT over OPC/UA using the eclipse.milo project APIs
* esen-prototype-deploy - for starting/deploying HCDs and assemblies
* ESEN Remote Temo Demo.tnzip - TwinCAT 3 project archive.

## Build Instructions

The build is based on sbt and depends on libraries generated from the 
[csw](https://github.com/tmtsoftware/csw) project.

See [here](https://www.scala-sbt.org/1.0/docs/Setup.html) for instructions on installing sbt.

## Prerequisites for running Components

The CSW services need to be running before starting the components. 
This is done by starting the `csw-services.sh` script, which is installed as part of the csw build.
If you are not building csw from the sources, you can get the script as follows:

 - Download csw-apps zip from https://github.com/tmtsoftware/csw/releases.
 - Unzip the downloaded zip.
 - Go to the bin directory where you will find `csw-services.sh` script.
 - Run `./csw_services.sh --help` to get more information.
 - Run `./csw_services.sh start` to start the location service and config server.

## Building the HCD and Assembly Applications

 - Run `sbt esen-prototype-deploy/universal:packageBin`, this will create self contained zip in `esen-prototype-deploy/target/universal` directory
 - Unzip the generated zip and cd into the bin directory

Note: An alternative method is to run `sbt stage`, which installs the applications locally in `esen-prototype-deploy/target/universal/stage/bin`.

## Running the TwinCAT project
Download the .tnzip file from this project to the TwinCAT 3 development environment and load the project archive.

## Running the HCD and Assembly

Run the container cmd script with arguments. For example:

* Run the HCD in standalone mode with a local config file (The standalone config format is differennt than the container format):

```
./target/universal/stage/bin/esenprototype-container-cmd-app --standalone --local ./src/main/resources/JEsenprototypeHcdStandalone.conf
```
