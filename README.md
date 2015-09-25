# h2o-scoring-engine-prototype
Application prototype for exposing H2O model POJO as a RESTful service.

## Runnig locally
To use h2o-scoring-engine-prototype you need to provide H2O model POJO class on a classpath of the project:

* Build h2o-scoring-engine-prototype using Maven: 
```
mvn clean pacakge
```
This will generate an artifact h2o-scoring-engine-prototype.jar in a *target* directory. 
* Download your generated H2O model POJO class code and *h2o-genmodel.jar* library.
* Compile a model class with a library: 
```
$ javac -cp h2o-genmodel.jar h2o_generated_model_class.java
```
* Attach compiled classes to prototype project:
```
jar -uv0f h2o-scoring-engine-prototype.jar *.class
```
* Run a scoring engine:
```
java -jar h2o-scoring-engine-prototype.jar
```

