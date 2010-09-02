-------------------------------------
 jRally - Rally Integration for Java
-------------------------------------

This project is a simple Java interface to the Rally_ Agile Project
Management tool.  We have been using Rally for a little while at work
and I find it's user interface quite nice from a browser but difficult
to do anything useful with from a mobile device.  Since my carrier does
not support the iPhone, I am stuck with an Google Android derived phone
and there is not a decent Rally client for it.  To make a long story
relatively short, I decided to write one and this project is the work
in progress towards that goal.

I am relatively new to writing releasable Java programs beyond simple
utilities so expect to see some relatively non-standard coding styles as
I get beaten into submission by the JVM.

This is also my first play at using git_ and github_ for source control.
I am a long-time Subversion_ user so a distributed SCM is a bit of a
switch.

Build Instructions
------------------

This part of the project isn't quite as clean as I would like, but it will
have to do for now.  The process is currently driven from `build.xml`.
This is a free-standing Ant script.  You will need to download and install
a few things first:

1. `Apache Ant`_ 1.8.0 or newer
2. `Apache Ivy`_ 2.1 or newer
3. Emma_ 2.0 or newer
4. A fairly recent version of Eclipse_, I've been using Galileo (3.5.2)

Once you have these installed, you should be able to run::

    jRally$ ant -p
    Buildfile: C:\src\jRally\build.xml
    
    Main targets:
    
     api-docs     ===> generate API documentation
     bootstrap    ===> update external dependencies, compile schemas, etc.
     clean        ===> removes intermediate generated files
     compile      ===> compile all classes
     dist-clean   ===> remove all non-repository files
     eclipse      ===> generate an Eclipse project
     emma         ===> enable Emma by mixing this in
     jar          ===> build a JAR of all classes
     real-clean   ===> remove all generated files
     run          ===> run the command-line application
     test         ===> run the test classes
     update-ivy   ===> retrieve dependencies
     xml-sources  ===> generate JAXB sources from the schemas
    Default target: compile
    jRally$ 

If you get this far, then everything is probably set up correctly.  Execute
the `bootstrap` target.  This will fetch a bunch of libraries from various
Ivy and Maven repositories - expect this to take 5 or 6 minutes.  Then it
will generate some XML bindings.  Finally, it will generate an Eclipse
workspace for the whole mess.

Once the bootstrapping is complete, you can import the workspace into
Eclipse or continue to work from the ant script.  The `jar` target will
compile everything into `bin/jRally.jar`.  There is not currently a Main
Class set in the jar, so you will have to call the class name directly.
There are a few *main* classes embedded in the jar currently.

Running
-------

There are two main classes implemented.  Both are buried in the
`standup.application` package:

**RetrieveStoriesByID**
  Fetch stories and defects using their Rally IDs (e.g., US1234, DE42)

**RetrieveStoriesForIteration**
  Fetch all of the stories and defects associated with an iteration

Both classes implement a basic CLI using some goodies from Apache Commons
for command line parsing.  The core of the CLI is in the `RetrieveStories`
class (I know, my class naming skills are astounding).  It implements a
common set of command line options and some common processing as well.  The
following command line options are supported by both CLI applications:

--help                show this help summary
--password PASSWORD   use this password when connecting to Rally
--story-file FILE     use this name for the story cards PDF
--task-file FILE      use this name for the task cards PDF
--user USER           connect to Rally with the user name USER
--verbose             show debug diagnostics

If you specify a tasks file, then the application will fetch the stories
and then all of the tasks associated with them.  If you don't specify one,
then only the stories are fetched.

.. _Rally: http://www.rallydev.com/
.. _git: http://gitscm.org/
.. _github: http://github.com/dave-shawley/jRally
.. _Subversion: http://subversion.apache.org/
.. _Apache Ant: http://ant.apache.org/bindownload.cgi
.. _Apache Ivy: http://ant.apache.org/ivy/download.cgi
.. _Emma: http://emma.sourceforge.net/downloads.html
.. _Eclipse: http://eclipse.org/downloads/

