# Cosmic Drift

This is very much WIP, so don't expect much.

## Trying the game

Download [the latest release](https://github.com/cosmicdrift/cosmicdrift/releases/latest), and run the JAR.

If your platform doesn't support running the JAR from the GUI, try:

    java -jar cosmic-drift.jar

## Building the game

To get started, type:

    git submodule init
    git submodule update

To build directly, type:

    ant

Alternatively, you can open this project in IntelliJ IDEA, and run the project. (After you init and update the submodules.)

## Notes

gson-2.4.jar was downloaded from [Maven Central](http://search.maven.org/#artifactdetails|com.google.code.gson|gson|2.4|jar).

If you want, copy the sound resources into the `resources` directory of the project. (Note: due to licensing issues, we can't put them in the main repo yet.)

## Licensing

See LICENSE.md. A summary: source code is GPL v3+, and artistic assets are CC-BY-SA 4.0.
