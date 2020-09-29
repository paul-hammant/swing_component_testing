# A demo of MVC in Swing with UI component testing

![image](https://user-images.githubusercontent.com/82182/56163536-8c022b00-5f9c-11e9-8c70-5a30f70c7aa3.png)

## Run the build like so:

```
mvn clean test
```

First time through Maven downloads a bunch and makes the final jar, but second and subsequent times through for 
me (`mvn test` at least), it is 22 seconds for all UI-clicking automated tests.

## Or skip the build and launch the app like so:

You'll do this to interact with it manually.

### Java 11 and above

```
java src/main/java/demo/Demo.java
```

### Java version 8 thru 10

```
cd src/main/java/
javac demo/Demo.java
java demo.Demo
cd -
```

## View the source of the Demo app

[src/main/java/demo/Demo.java](src/main/java/demo/Demo.java). It's a single source file.

That's as close as you can get to pseudo-declarative in plain Java. It'd be more declarative in 
JavaFX, TornadoFX (Kotlin) and Swiby (JRuby, discontinued).

As it is now, it is also pretty close to 1998's original Swing. Package names are different, and 
there's one or two helper methods that were not there back then, and also some Java 8 syntactic 
sugar that ends up making the same bytecode classes  

## Architectural criticisms

### Declarative tricks

In order to to have a nested feel inner classes are used as a syntactic 
trick:

```
add(new JPanel() {{
  // components and more containers.
}});

```

Because of a decision in the 90's to keep compatibility with Microsoft'sJava (that was 
frozen at JDK 1.0 ater a lawsuit), the bytecode design for inner classes in JDK 1.1 didn't 
change. Thus we see a lot of $1, $2 suffixed classes that are ugly to look at if you were 
looking at the generated class files, and also come with a file size impediment versus 
other choices Sun could have made for the same feature back then.

### Views are not overridable in this demo

Related to that declarative style above, we don't have overridable views. No subclasses 
for whatever reason - including testing purposes. Sure, we have overridable models 
(Mockito could mock Counter.Model), but not views as we have it here.  From a purist 
point of view, MVC suggests that Views could be further specialized, and we can do that 
through composition, but not inheritance here.

We also have view's as final fields that are accessible outside the class. Like 
`foo.view`. Java's conventions would be for a getter - `foo.getView()`but I skipped 
that here to save a few lines. If this were a long-lived mutli-developer solution, I 
would put them back in if. Or if they were truly needed for some OO reason.

# Tests and coverage reports

Individual test classes:

1. CounterComponentTests (6.7 seconds for 10 tests)
2. CounterModelUnitTests (0.5 second for 5 tests)
3. CoupledCounterPairComponentTests (3.7 seconds for 2 tests)
4. WholeAppIntegrationTests (6.7 seconds for 2 tests)

Times above are for each test class run on their own. That is in the Intellij IDE and the times 
would be longer for Maven on the command line as it is less intelligent re unnecessary steps. If 
all are run together in one execution, that is 19 tests in 6 seconds. Clearly there's an overhead 
for bringing up Marathon and for the 'single test' breakdown above

## CounterModel Unit Tests (on their own).

Coverage for Counter.Model class

![2019-04-15_1605](https://user-images.githubusercontent.com/82182/56143385-72e28580-5f6e-11e9-965f-2b9cc86cfba9.png)

The missed lines are two catch blocks that I can't simulate. Java's checked 
exceptions being the reason I can get the coverage to 100%.

These tests take 1ms each, but the first takes 950ms on my 2017 MacBookAir. There's 
no Marathon/WebDriver involved - no UI at all.  Counter's View and Controller logic 
is not tested.

## Counter Component Tests (on their own).

Coverage for Counter and inner classes:

![image](https://user-images.githubusercontent.com/82182/56144431-58111080-5f70-11e9-81a9-b476b06350ce.png) 

The coverage for the model is exactly the same as the Counter Model unit tests above. But 
this time is was achieved via Marathon/WebDriver. The initial test was 4.5 seconds, and each 
test thereafter was an average 330ms. Frames are opening and closing per test. Speed up 
opportunities are (as Web Selenium) not close and reopen the window between tests.  Mocking 
the model would most likely not provide a speedup.

## CoupledCounterPair Component Tests (on their own).

Coverage for CoupledCounterPair and inner classes:

![image](https://user-images.githubusercontent.com/82182/56144982-6dd30580-5f71-11e9-8a4a-3dbb962905b8.png)

100% of the View and Controller logic is covered. There's no model logic got 
this component as it uses the model from Counter (covered above). There's about 5s 
spend getting the first test complete, and 450ms the second (of two).

## Whole App Integration Tests (on their own)

Coverage for the whole "Demo" app, and all classes used:

![2019-04-15_1633](https://user-images.githubusercontent.com/82182/56145480-5ea08780-5f72-11e9-817a-bbe7d1a86dcf.png)

This is closer to a happy path test. Counter's coverage lowered as there was 
no edge-case checking.  Five seconds or the first test to complete, the 300ms 
for the next.

## All Tests together

![2019-04-15_1636](https://user-images.githubusercontent.com/82182/56145684-bccd6a80-5f72-11e9-96c7-6488a689509e.png)

Coverage accumulated for all unit, component and happy path (whole app) 
tests: 93%. 10.5 seconds for all tests.  Here's a YouTube video of that:

[![Test suite running](http://img.youtube.com/vi/kJIYdXIeZm8/0.jpg)](https://www.youtube.com/watch?v=kJIYdXIeZm8 "Tests suite running")

# Marathon

Marathon came out of a project that ThoughtWorks did for "Dixons" (a large electrical good retailer in the 
UK) in 2003/4. The same prooject provided some notes for Jez Humble and Dave Farley's "Continuous Delivery" book. 
There were a lot of developers involved and it was six months from first code to  
go-live. It was a Java solution (backend and frontend) for point-of-sale equipment. 
WinRunner was the state of the art in the industry for UI automation and we knew it 
[wasn't that usable in a XP/DevOps way of working](https://paulhammant.com/blog/000245.html).

[Charles Lowel](https://twitter.com/cowboyd) and 
[Jeremy Lightsmith](https://twitter.com/lightsmith) 
put "MarathonMan" together and released it as open source. ThoughtWorks didn't substantially 
fund it like they did for Selenium soon after. In the end, others took care of the project in 
opensource-land: Karra "KD" Dakshinamurthy and colleagues at Jalian Systems Pvt. Ltd (India). 
There's a full circle aspect now, because Jalian recently made Marathon Selenium2+ compatible.
See their portal [marathontesting.com/](https://marathontesting.com/) for more info.