# Hub
Hub for preprocessing of questions for yodaQA
Hub is interlink between YodaQA-client and yodaQA. It's purpose is to get question from YodaQA-client, preprocess it,
send it to yodaQA (or to other service, if it decides so), get answer and send answer back to web interface. The other service
is traffic information currently.

##Installation Instructions
Quick instructions for setting up, building and running:

  * You need Java 1.8 and Gradle
  * You need running Lookup Services https://github.com/brmson/label-lookup with street file (data/streets.tsv)
  * You need running Dataset-STS scoring API https://github.com/brmson/dataset-sts (tools/scoring-api.py)
  * You need file with reference questions (data/reference_questions.tsv)
  * We assume that you cloned Hub and are now in the directory that contains this README.
  * ``gradlew build`` to build
  * ``gradlew run -PexecArgs="[Port to run] [Address of YodaQA] [Address of Lookup Service]
  [Address of dataset-sts scoring API] [File with reference questions]"`` to run

####Example
YodaQA runs on ``http://localhost:4567/``. Lookup Service runs on ``http://localhost:5000/``. Dataset-STS scoring API runs on
``http://localhost:5050/``. File with reference questions is in ``data/reference_questions.tsv``
 To run HUB on port 4568 (it must differ from yodaQA's port and Lookup Service port), run in it's root directory
  ``gradlew build`` and ``gradlew run -PexecArgs="4568 http://localhost:4567/ http://[::1]:5000/ http://localhost:5050/
   data/reference_questions.tsv"``. To connect
   YodaQA-client to HUB add ``?e=http://localhost:4568/`` to the end of url.

### Testing

We used these datasets:
Traffic training and testing datasets (int the lists 1 and 3): https://docs.google.com/spreadsheets/d/1LAY6trroXwdL8OQVGbBym6EA4yPZEFdR9eHJRAOZIIY/edit?usp=sharing.
Movies training dataset: https://github.com/brmson/dataset-factoid-movies/blob/master/moviesB/train.json
Movies testing dataset: https://github.com/brmson/dataset-factoid-movies/blob/master/moviesB/test.json

#### Traffic info 
You can test traffic question topic and street detection by running ``gradlew run_Main_Traffic -PexecArgs="[Address of Lookup Service]
[Address of dataset-sts scoring API] [File with reference questions]"`.
You can ask your question after running this command.
Output will be in the form of "topic<TAB>street name". This is used for testing mainly.

#### Traffic topic and street detection 
This dataset contains question and it's topic with street names. We usually have one street name (for questions concerning 
only single street), however there can be multiple for questions like "How to get from Evropská to Technická".
Download the spreadsheet as .tsv file or use the dataset in ``data/traffic_dataset.tsv``. Start the test by 
``gradlew run_Main_TrafficTest_TopicStreetDetection -PexecArgs="[Location of .tsv file] [Address of Lookup Service] [Address of dataset-sts scoring API]
[File with reference questions]"`. It evaluates how many questions have correctly detected topic and street name.

#### Calculating thresholds of traffic question recognition and accuracy
There are two thresholds used to recognize the traffic question. The first is the minimal score of topic. The second is 
the maximal edit distance of street name. You can run ``gradlew run_Main_TrafficTest_DomainThresholds -PexecArgs="
[Location of .tsv file with traffic questions] [Location of .json file with movies questions] [Address of Lookup Service]
[Address of dataset-sts scoring API] [File with reference questions] [mode]"`` Mode parameter determines, what thresholds will be used.
0 means only topic threshold will be calculated, 1 means only street threshold will be calculated and 2 means both thresholds will
be calculated. The output thresholds can be set in ``eu.ailao.hub.traffic.analyze.StreetAnalyzer.class`` ``DISTANCE_THRESHOLD`` 
(the street distance) and ``eu.ailao.hub.traffic.analyze.TopicAnalyzer.class`` ``THRESHOLD`` (the topic score).
This test also shows the accuracy achieved and the misclassified questions.

#### Precision and recall of traffic domain classification
The precision and recall of domain detection on the traffic class can be calculated by running ``gradlew run_Main_TrafficTest_DomainPrecisionRecall -PexecArgs="
[Location of .tsv file with traffic questions] [Location of .json file with movies questions] [Address of Lookup Service]
[Address of dataset-sts scoring API] [File with reference questions]"``

#### Precision and recall of traffic topic classification
The precision and recall of topic detection on the all topics can be calculated by running ``gradlew run_Main_TrafficTest_TopicPrecisionRecall -PexecArgs="
[Location of .tsv file] [Address of Lookup Service] [Address of dataset-sts scoring API][File with reference questions]"`.

##Dialog API
Dialog API expands YodaQA's API https://github.com/brmson/yodaqa/blob/master/doc/REST-API.md. Hub gets request from
web client and sends it further to YodaQA with minor changes. The list of changes follows.

####Artificial concepts
Artificial concepts are concepts, selected by users and not generated by YodaQA. Information about them are send in fields:

* numberOfConcepts - number of concepts generated in total
* fullLabel{i} - full label of selected concept of number {i} (replace '{i}' with number)
* pageID{i} - page id of selected concept of number {i} (replace '{i}' with number)

##Coreference resolution
Concepts of last n answers are used, when third person pronoun(he, she, it) is founded in question's text.
``MAX_QUESTIONS_TO_REMEMBER_CONCEPT`` constant is used as n. Default value is 5.

####Example
When the first question is "What book wrote J. R. R. Tolkien?", the generated concept is "J. R. R. Tolkien". The second
question "Where was he born?" contains "he", so concept from the first question will be used.

##Transformations
Hub can transform questions and answers also. We are using "age transform" currently.

####Example
Age transform changes question from "How old is someone?" to "When he was born?". We do it, because YodaQA has better
success with finding of birth date. Answers are transformed back by calculation difference between today's date and date in answer.
Transformed answers are showed to users.

##Traffic questions
Hub can help you to not get stuck in the traffic jam. You can ask him for actual traffic flow or traffic incidents in some street.
Prague is supported in current version only. We take data form Here. You need running Lookup Services https://github.com/brmson/label-lookup
with file containing the street names, Dataset-STS scoring API https://github.com/brmson/dataset-sts and file with reference questions for this feature.
We use Lookup Services for detecting street in question and Dataset-STS scoring API for detecting question's topic.

####Example
You can ask "What is the traffic flow in the Wolkerova street?" or make command "Show me all traffic incidents in Wolkerova street!".
Hub detect's question or command concerning traffic and it will take data from Here instead of asking yodaQA.