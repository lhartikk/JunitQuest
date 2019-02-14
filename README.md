# JunitQuest

Automatically generate (useless?) unit tests for Java.

Example input:
```java
public class MyService {

    public String getStatusString(int code) {
        if (code == 200) {
            return "OK";
        } else if (code == 404) {
            return "NOT FOUND";
        } else {
            return "UNKNOWN";
        }
    }
}
```

Output:
```java
 @Test
  public void getStatusStringTest0() throws Exception {    
    int var2 = 0;        
    org.hello.MyService var3 = new org.hello.MyService();
    var3.getStatusString(var2);
  }

  @Test
  public void getStatusStringTest1() throws Exception {    
    int var2 = 200;        
    org.hello.MyService var3 = new org.hello.MyService();
    var3.getStatusString(var2);
  }

  @Test
  public void getStatusStringTest2() throws Exception {    
    int var2 = 404;        
    org.hello.MyService var3 = new org.hello.MyService();
    var3.getStatusString(var2);
  }
```


## Motivation

I feel in software development it's easy to put too much emphasis on unit testing. Especially in Java, where measuring code coverage is super-easy, further giving motivation to write large sets of unit tests. JunitQuest tries to undermine/provoke discussion about unit tests by automatically creating tests that actually test nothing, but still quests through your codebase. Just like bad unit tests do.

## How to use
JunitQuest can be used from command line or as an IntelliJ plugin. Requires Java 8.


### Using the command line version
Download [junitquest-0.1.jar](http://lhartikk.github.io/junitquest/junitquest-0.1.jar "junitquest-0.1.jar")

To use the command line (.jar) version you need to know the absolute bytecode root folder location and the fully qualified name of the class.

Example command line usage (in Ubuntu), where tests are created for [Hours](https://github.com/JodaOrg/joda-time/blob/master/src/main/java/org/joda/time/Hours.java "Hours.java") class in the [joda-time](https://github.com/JodaOrg/joda-time "joda-time") project.

    > wget http://lhartikk.github.io/junitquest/junitquest-0.1.jar
    > git clone https://github.com/JodaOrg/joda-time.git 
    > (cd joda-time/ && mvn compile)
    > java -jar junitquest-0.1.jar org.joda.time.Hours $(readlink -f joda-time/target/classes/)

## Intellij Plugin
1. [Download](http://lhartikk.github.io/junitquest/JunitQuest-IntelliJ.zip) the plugin
2. File -> Settings -> Plugins -> Install plugin from disk..
3. On a Java class file: Right click -> Run JunitQuest

Sources [here](https://github.com/lhartikk/JunitQuest-IntelliJ)
## How it works

1. Instruments the bytecode
2. Uses (quite naive) [concolic execution](https://en.wikipedia.org/wiki/Concolic_testing "concolic exection") and tries to reach all the bytecode labels on each method.



