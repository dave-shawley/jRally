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
I get beaten into submission by the JVM.  One of the main purposes of this
project is to help me firm up *best practices* before I go and call myself
a Java programmer.  So you can expect a lot of directory reorganization and
complete changes in idioms used and what not.  I am going to try to capture
various approaches that I am taking in this file so that you at least have
some sort of a trail guide.

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
  Buildfile: /Users/daveshawley/src/jRally/build.xml

  Main targets:

   api-docs       ===> generate API documentation
   bootstrap      ===> update external dependencies, compile schemas, etc.
   clean          ===> removes intermediate generated files
   compile        ===> compile all classes
   compile-tests  ===> compile the test code
   dist-clean     ===> remove all non-repository files
   docs           ===> build all documentation
   eclipse        ===> generate an Eclipse project
   emma           ===> enable Emma by mixing this in
   jar            ===> build a JAR of all classes
   real-clean     ===> remove all generated files
   run            ===> run the command-line application
   schema-docs    ===> generate XML Schema documentation
   test           ===> run the test classes
   test-jar       ===> build the jar contain unit tests and test data
   update-ivy     ===> retrieve dependencies
   xml-sources    ===> generate JAXB sources from the schemas
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


Directory Layout
----------------

I use a directory structure that smears the style used by some Apache projects
and the style espoused by Sun (now Oracle) in their examples.  There doesn't
seem to be a real consensus in the community about project layouts.  Not to
mention that I seem to have chosen a handful of technologies that don't seem
to be used together very often.  So as any good programmer would do, I gone
off and created something of my own.  At least I am going to describe it here.

  +------------------------+-------------------------------------------------+
  | README.rst             | Used by github_ as the overview documentation   |
  |                        | for the project.  I convert this to html and    |
  |                        | store it in the *docs* directory so that        |
  |                        | people who are not familiar with reStructured   |
  |                        | Text can read it easily.                        |
  +------------------------+-------------------------------------------------+
  | build.xml              | Ant build script                                |
  +------------------------+-------------------------------------------------+
  | configs/               | Property files and other runtime configuration. |
  |                        | *\*.properties* files are copied into the       |
  |                        | appropriate place for the various deployment    |
  |                        | artifacts.                                      |
  +------------------------+-------------------------------------------------+
  | configs/main/          | Property files in this directory serve as the   |
  |                        | basis for all of the deployment artifacts.      |
  |                        | They are copied into root of the main and test  |
  |                        | JARs.  They are also copied into the            |
  |                        | *WEB-INF/classes* directory of the WAR file.    |
  +------------------------+-------------------------------------------------+
  | configs/test/          | Property files in this directory are copied     |
  |                        | into the root of the test JAR.  These files     |
  |                        | will overwrite the files from *configs/main*.   |
  +------------------------+-------------------------------------------------+
  | configs/web/           | Property files in this directory are copied     |
  |                        | into *WEB-INF/classes* of the WAR.  These files |
  |                        | will overwrite the files from *configs/main*.   |
  +------------------------+-------------------------------------------------+
  | docs/                  | Root of various documentation both generated    |
  |                        | and static.                                     |
  +------------------------+-------------------------------------------------+
  | docs/overview.html     | HTML file that is used as the overview file     |
  |                        | in the Javadoc when it is generated.            |
  +------------------------+-------------------------------------------------+
  | docs/README.html       | *README.rst* converted into HTML                |
  +------------------------+-------------------------------------------------+
  | ext-lib/               | External libraries imported by Ivy are stored   |
  |                        | here.  This directory is empty by default.      |
  +------------------------+-------------------------------------------------+
  | ivy.xml                | Ivy description of this projects dependencies   |
  +------------------------+-------------------------------------------------+
  | ivysettings.xml        | Ivy configuration of repositories that it can   |
  |                        | pull dependencies from.                         |
  +------------------------+-------------------------------------------------+
  | local-lib/             | Libraries used by the project at runtime that   |
  |                        | are not easy to find via Ivy.                   |
  +------------------------+-------------------------------------------------+
  | src/                   | Root of all source code. I put everything in    |
  |                        | subdirectories of this tree.                    |
  +------------------------+-------------------------------------------------+
  | src/main/              | Source code that goes into the main JAR file.   |
  |                        | This is where the meat of the application is.   |
  |                        | Other directories contain application hooks     |
  |                        | and other extensions of the base code.          |
  +------------------------+-------------------------------------------------+
  | src/main/schemas/      | XSD files that describe the Rally API that is   |
  |                        | used along with the intermediate XML            |
  |                        | representation.                                 |
  +------------------------+-------------------------------------------------+
  | src/main/xslt/         | XSL transforms that create the intermediate     |
  |                        | XML based on the Rally responses as well as     |
  |                        | generate XSL-FO output from the intermediate.   |
  +------------------------+-------------------------------------------------+
  | src/test/              | *JUnit tests.*  This subtree mimics the other   |
  |                        | trees so that test code resides in the same     |
  |                        | package as the code that it is testing.  This   |
  |                        | makes it possible to test non-public members    |
  |                        | by giving them package visibility.              |
  +------------------------+-------------------------------------------------+
  | src/test/test-data/    | XML files that are used as canned-responses     |
  |                        | for the JUnit tests.                            |
  +------------------------+-------------------------------------------------+
  | src/web/               | *Web Frontend.*  This subtree contains the      |
  |                        | JavaServer Faces and Servlet code that          |
  |                        | implements the web application.                 |
  +------------------------+-------------------------------------------------+
  | src/web/resources/     | Web pages, stylesheets, and other artifacts     |
  |                        | that are used for the web application.          |
  +------------------------+-------------------------------------------------+
  | src/web/WEB-INF/       | Deployment descriptors for the web application. |
  +------------------------+-------------------------------------------------+
  | tools/                 | Utilities used during the build process.        |
  |                        | Most of the tools are not used at runtime.      |
  |                        | One exception is the JAXB jars.  The XJC task   |
  |                        | is used to convert XSDs in the source tree      |
  |                        | into Java classes.  They are also required at   |
  |                        | runtime.                                        |
  +------------------------+-------------------------------------------------+

.. _Rally: http://www.rallydev.com/
.. _git: http://gitscm.org/
.. _github: http://github.com/dave-shawley/jRally
.. _Subversion: http://subversion.apache.org/
.. _Apache Ant: http://ant.apache.org/bindownload.cgi
.. _Apache Ivy: http://ant.apache.org/ivy/download.cgi
.. _Emma: http://emma.sourceforge.net/downloads.html
.. _Eclipse: http://eclipse.org/downloads/

