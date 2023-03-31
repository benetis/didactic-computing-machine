# Scala 3 amish

### Why
Almost all Scala projects depend on a number of libraries which provide quite unique functionality. Functionality like effect systems, streams and similar things are important part of any modern Scala project. It is essential to understand what each of these building blocks do.
Most of these libraries are documented well and have good amount of material describing design choices. However, in my opinion, the best way to understand author's design choice is to implement it yourself. This forces to understand tradeoffs made. Maybe its worth exploring further how library can choose different tradeoffs to accumulate mentioned functionality.

### Constrains
No dependencies in build.sbt, except for testing (munit). 
Note: I would like to focus on Scala 3 features first as I am new to Scala 3. Maybe later we can get rid of this dependency as well

### Goals

While the actual goal is to implement mentioned dependencies, this project needs real world project goals. These goals 
will dictate constraints and requirements for the libraries needed. I will not be sticking to one project and add them as we go. 

1. Memory flashcards CLI app
   - Functionality
     - Add flashcards
       - Question
       - Answer
     - Review flashcards
       - Select how hard the answer was:
         - (again), (hard), (ok), (easy)
         - Depending on the answer, app will adjust metadata on card when to be reminded again
       - Review all cards which are due 
   - Scope
     - Command line only. Control by capturing input in command line
     - CRUD to a local file, possibly SQLite
     - No decks for initial version
   
2. To be added later