[![Dependency Status](https://www.versioneye.com/user/projects/572367cbba37ce00464e04b6/badge.svg?style=flat)](https://www.versioneye.com/user/projects/572367cbba37ce00464e04b6)

# h2o-scoring-engine-base
The `h2o-scoring-engine-base` is an application prototype for exposing H2O model POJO as a RESTful service.
Repository contains a prototype application and a building script for creating scoring engine based on provided H2O model.

## Prerequisites
You'll need a JDK version 1.8, and also to have the parent pom installed with the correct version:
```
git clone https://github.com/trustedanalytics/trustedanalytics-cloud-parent.git
cd trustedanalytics-cloud-parent
git checkout edf7681e
mvn clean install
```
You'll also need to ensure that your model is named such that it is a valid
Java class name. For example, use underscores (_) rather than hyphens (-).

## Building scoring engine
To build `h2o-scoring-engine-base`, run the following command from the project root directory: 
```
mvn clean package
```
After building `h2o-scoring-engine-base` go to *tools* directory and run a `h2o-scoring-engine-builder.sh` passing 3 parameters: H2O server URL, H2O model name and a path to previously built h2o-scoring-engine-base JAR. 
```
 ./h2o-scoring-engine-builder.sh https://<h2o server> <model_name> ../target/h2o-scoring-engine-base.jar
```
Optionally, you can also pass output file name and credentials for H2O server.
```
./h2o-scoring-engine-builder.sh  -o <output jar name> -u <username> -p https://<h2o server> <model name> ../target/h2o-scoring-engine-base.jar
```
Builder script downloads your model and a required `h2o-genmodel.jar` library form H2O server and builds a scoring engine based on the model.
Application JAR, `h2o-scoring-engine-<model name>.jar`, is placed in working directory.

To run it locally run:
```
java -jar h2o-scoring-engine-<model name>.jar
```

To specify a particular port, rather than the default "8080," append to the above command `--server.port <port>`.

## Using scoring engine
Scoring engine *score* method is accessible through REST API:

**URL**: `http://<application host>/score`

**Headers**: `Content-type: application/json`

**HTTP Method**: `POST`

**Request body**: sequence of numbers of a size required by the model

Usage example: 
```
curl -i -X POST -H "Content-type: application/json" -d '[1.2, 1.3, 1.4, 5.3]' http://<application host>/score
```
