Testing code changes
====================

Automated tests
---------------

The :source:`Bio-Formats testing framework <components/test-suite>` component
contains most of the infrastructure to run automated tests against the data
repository.

After checking out source code and building all the JAR files (see
:doc:`building-bioformats`), switch to the :file:`test-suite` component and run the tests using the :program:`ant` ``test-automated`` target::

  $ cd components/test-suite
  $ ant -Dtestng.directory=$DATA/metamorph test-automated

where ``$DATA`` is the path to the full data repository.


Multiple options can be passed to the :program:`ant` ``test-automated`` target 
by setting the ``testng.${option}`` option via the command line. Useful options
are described below.

testng.directory
  Mandatory option. Specifies the root of the data directory to be tested::

    $ ant -Dtestng.directory=$DATA/metamorph test-automated

  On Windows, the arguments to the test command must be quoted::

    > ant "-Dtestng.directory=$DATA\metamorph" test-automated

testng.configDirectory
  Specifies the root of the directory containing the configuration files. This
  directory must have the same hierarchy as the one specified by
  ``testng.directory`` and contain :file:`.bioformats` configuration
  files::

    $ ant -Dtestng.directory=/path/to/data -Dtestng.configDirectory=/path/to/config test-automated

  If no configuration directory is passed, the assumption is that it is the 
  same as the data directory.

testng.configSuffix
  Specifies an optional suffix for the configuration files::

    $ ant -Dtestng.directory=/path/to/data -Dtestng.configSuffix=win test-automated

testng.memory
  Specifies the amount of memory to be allocated to the |JVM|::

    $ ant -Dtestng.directory=$DATA -Dtestng.memory=4g test-automated

  Default: 512m.

testng.threadCount
  Specifies the number of threads to use for testing::

    $ ant -Dtestng.directory=$DATA -Dtestng.threadCount=4 test-automated

  Default: 1.

You should now see output similar to this::

    Buildfile: build.xml

    init-title:
         [echo] ----------=========== bio-formats-testing-framework ===========----------
    ...
    test-automated:
       [testng] 17:05:28,713 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [${logback.configurationFile}]
       [testng] 17:05:28,713 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
       [testng] 17:05:28,713 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
       [testng] 17:05:28,713 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback.xml] at [file:/opt/ome/bioformats/components/test-suite/logback.xml]
       [testng] 17:05:28,835 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
       [testng] 17:05:28,837 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [stdout]
       [testng] 17:05:28,876 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.classic.sift.SiftingAppender]
       [testng] 17:05:28,878 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [SIFT]
       [testng] 17:05:28,891 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [loci.tests.testng] to DEBUG
       [testng] 17:05:28,891 |-INFO in ch.qos.logback.classic.joran.action.RootLoggerAction - Setting level of ROOT logger to INFO
       [testng] 17:05:28,891 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [SIFT] to Logger[ROOT]
       [testng] 17:05:28,892 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [stdout] to Logger[loci.tests.testng]
       [testng] 17:05:28,892 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
       [testng] 17:05:28,894 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@706a04ae - Registering current configuration as safe fallback point
       [testng] [2015-08-18 17:05:28,904] [main] testng.directory = /ome/data_repo/test_per_commit/
       [testng] 17:05:28,908 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [loci.tests.testng.TimestampedLogFileAppender]
       [testng] 17:05:28,909 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [logfile-main]
       [testng] 17:05:28,955 |-INFO in loci.tests.testng.TimestampedLogFileAppender[logfile-main] - File property is set to [target/bio-formats-test-main-2015-08-18_17-05-28.log]
       [testng] [2015-08-18 17:05:28,963] [main] testng.multiplier = 1.0
       [testng] [2015-08-18 17:05:28,964] [main] testng.in-memory = false
       [testng] [2015-08-18 17:05:28,964] [main] user.language = en
       [testng] [2015-08-18 17:05:28,964] [main] user.country = US
       [testng] [2015-08-18 17:05:28,964] [main] Maximum heap size = 455 MB
       [testng] Scanning for files...
       [testng] [2015-08-18 17:05:32,258] [main] ----------------------------------------
       [testng] [2015-08-18 17:05:32,258] [main] Total files: 480
       [testng] [2015-08-18 17:05:32,258] [main] Scan time: 3.293 s (6 ms/file)
       [testng] [2015-08-18 17:05:32,258] [main] ----------------------------------------
       [testng] Building list of tests...

and then eventually::

       [testng] ===============================================
       [testng] Bio-Formats software test suite
       [testng] Total tests run: 19110, Failures: 0, Skips: 0
       [testng] ===============================================
       [testng]

    BUILD SUCCESSFUL
    Total time: 16 minutes 42 seconds

In most cases, test failures  should be logged in the main console output as::

       [testng] [2015-08-18 17:13:13,625] [pool-1-thread-1]     SizeZ: FAILED (Series 0 (expected 2, actual 1))

To identify the file, look for the initialization line preceding the test
failures under the same thread::

   [testng] [2015-08-18 17:13:12,376] [pool-1-thread-1] Initializing /ome/data_repo/test_per_commit/ome-tiff/img_bk_20110701.ome.tif: 

The console output is also recorded under :file:`components/test-suite/target` 
as :file:`bio-formats-software-test-main-$DATE.log` where "$DATE" is the date 
on which the tests started in "yyyy-MM-dd_hh-mm-ss" format. The detailed report
of each thread is recorded under
:file:`bio-formats-software-pool-$POOL-thread-$THREAD-main-$DATE.log`

Configuration files can be generated for files or directories using the 
:program:`ant` ``gen-config`` target. This generation target supports the same options as :program:`ant` ``test-automated``::

  $ ant -Dtestng.directory=/path/to/data -Dtestng.configDirectory=/path/to/config -Dtestng.memory=4g -Dtestng.threadCount=6 gen-config

MATLAB tests
------------
.. _matlab-xunit: https://github.com/psexton/matlab-xunit

Tests for the Bio-Formats MATLAB toolbox are written using the xunit framework
and are located under :sourcedir:`components/formats-gpl/test/matlab`.

To run these tests, you will need to download or clone `matlab-xunit`_, a
xUnit framework with JUnit-compatible XML output. Then add this package
together with the Bio-Formats MATLAB to your MATLAB path:

.. code-block:: matlab

  % Add the matlab-xunit toolbox to the MATLAB path
  addpath('/path/to/matlab-xunit');
  % Add the Bio-Formats MATLAB source to the MATLAB path
  % For developers working against the source code
  addpath('/path/to/bioformats/components/formats-gpl/matlab');
  addpath('/path/to/bioformats/artifacts');
  % For developers working against a built artifact, e.g. a release
  % addpath('/path/to/bfmatlab');

You can run all the MATLAB tests using :command:`runxunit`:

.. code-block:: matlab

  cd /path/to/bioformats/components/formats-gpl/test/matlab
  runxunit

Individual test classes can be run by passing the name of the class:

.. code-block:: matlab

  cd /path/to/bioformats/components/formats-gpl/test/matlab
  runxunit TestBfsave

Individual test methods can be run by passing the name of the class and the
name of the method:

.. code-block:: matlab

  cd /path/to/bioformats/components/formats-gpl/test/matlab
  runxunit TestBfsave:testLZW

Finally to output the test results under XML format, you can use the :option:`-xmlfile` option:

.. code-block:: matlab

  cd /path/to/bioformats/components/formats-gpl/test/matlab
  runxunit -xmlfile test-output.xml
