# Scala 3 DIY

### Why
Almost all Scala projects depend on a number of libraries which provide quite unique functionality. Functionality like effect systems, streams and similar things are important part of any modern Scala project. It is essential to understand what each of these building blocks do.
Most of these libraries are documented well and have good amount of material describing design choices. However, in my opinion, the best way to understand author's design choice is to implement it yourself. This forces to understand tradeoffs made. Maybe its worth exploring further how library can choose different tradeoffs to accumulate mentioned functionality.

### Constrains
No dependencies in build.sbt, except for testing (munit). 
Note: I would like to focus on Scala 3 features first as I am new to Scala 3. Maybe later we can get rid of this dependency as well

More information can be found in blog series about this project