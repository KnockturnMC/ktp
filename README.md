KTP ![Java CI](https://github.com/Spottedleaf/KTP/workflows/Java%20CI/badge.svg)
==

Fork of [Paper](https://github.com/PaperMC/Paper) aimed at improving server performance at high playercounts.

## Contact
[IRC](http://irc.spi.gt/iris/?channels=KTP) | [Discord](https://discord.gg/CgDPu27)

## How To (Server Admins)
KTP uses the same paperclip jar system that Paper uses.

You can download the latest build of KTP by going [here](https://ci.codemc.io/job/Spottedleaf/job/KTP/).

You can also [build it yourself](https://github.com/Spottedleaf/KTP#building)

## How To (Plugin developers)
In order to use KTP as a dependency you must [build it yourself](https://github.com/Spottedleaf/KTP#building).
Each time you want to update your dependency you must re-build KTP.

KTP-API maven dependency:
```xml
<dependency>
    <groupId>com.KTP</groupId>
    <artifactId>KTP-api</artifactId>
    <version>1.15.2-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
 </dependency>
 ```

KTP-Server maven dependency:
```xml
<dependency>
    <groupId>com.KTP</groupId>
    <artifactId>KTP</artifactId>
    <version>1.15.2-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

There is no repository required since the artifacts should be locally installed
via building KTP.

## Building

Requirements:
- You need `git` installed, with a configured user name and email. 
   On windows you need to run from git bash.
- You need `maven` installed
- You need `jdk` 8+ installed to compile (and `jre` 8+ to run)
- Anything else that `paper` requires to build

If all you want is a paperclip server jar, just run `./KTP jar`

Otherwise, to setup the `KTP-API` and `KTP-Server` repo, just run the following command
in your project root `./KTP patch` additionally, after you run `./KTP patch` you can run `./KTP build` to build the 
respective api and server jars.

`./KTP patch` should initialize the repo such that you can now start modifying and creating
patches. The folder `KTP-API` is the api repo and the `KTP-Server` folder
is the server repo and will contain the source files you will modify.

#### Creating a patch
Patches are effectively just commits in either `KTP-API` or `KTP-Server`.
To create one, just add a commit to either repo and run `./KTP rb`, and a
patch will be placed in the patches folder. Modifying commits will also modify its
corresponding patch file.

## License
The PATCHES-LICENSE file describes the license for api & server patches,
found in `./patches` and its subdirectories except when noted otherwise.

Everything else is licensed under the MIT license, except when note otherwise.
See https://github.com/starlis/empirecraft and https://github.com/electronicboy/byof
for the license of material used/modified by this project.

### Note

The fork is based off of aikar's EMC framework found [here](https://github.com/starlis/empirecraft)
