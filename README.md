# Hub
Hub for preprocessing of questions for yodaQA
Hub is interling between YodaQA-client and yodaQA. It's purpose is to get question from YodaQA-client, preprocess it, send it to yodaQA (or to other service, if it decides so), get answer and send answer back to web interface.

##Installation Instructions
Quick instructions for setting up, building and running:

  * You need Java 1.8 and Gradle
  * We assume that you cloned Hub and are now in the directory that contains this README.
  * ``gradlew build`` to build
  * ``gradlew run -PexecArgs="[Port to run] [Address of YodaQA]"`` to run

####Example
YodaQA runs on ``http://localhost:4567/``. To run HUB on port 4568 (it must differ from yodaQA's port), run in it's root directory ``gradlew build`` and ``gradlew run -PexecArgs="4568 http://localhost:4567/"``. To connect YodaQA-client to HUB add ``?e=http://localhost:4568/`` to the end of url.
