jfmi1.0 - A Java Wrapper for the Functional Mock-up Interface

A Functional Mock-up Unit (FMU) is a file that contains functionality
that may be invoked either via co-simulation or model exchange.
Typically, a FMU file has a .fmu extension. A FMU file is a zip file
that contains a file named modelDescription.xml and one or more
platform-dependent shared libraries.

JFMI is a BSD-licensed interface between Java
and FMI.

See org/ptolemy/fmi/driver/FMUCoSimulation.java and
org/ptolemy/fmi/driver/FMUModelExchange.java
for simple examples that read in .fmu files and invoke methods in
the the platform-dependent shared libraries.

Sample .fmu files may be found in fmu/cs and fmu/me.
for the following architectures: darwin64, linux32, linux64, win32 and win64.
These files were generated using a port of the FMUSDK which may be found at
http://github.com/cxbrooks/fmusdk.

FMI documentation may be found at http://www.modelisar.com/fmi.html

Build instructions:
To build, download and install Apache ant from https://ant.apache.org/
cd to org/ptolemy/fmi and run ant: 
  cd org/ptolemy/fmi
  ant

To run an example under Mac OS or Linux, try:
  cd org/ptolemy/fmi
  java -classpath ../../../lib/jna.jar:../.. \
     org.ptolemy.fmi.driver.FMUCoSimulation \
     fmu/cs/bouncingBall.fmu 1.0 0.1 true c results.csv 
Then look at the contents of the results.csv file

Sponsors:
JFMI was developed under a Department of Energy Contract
for the Building Controls Virtual Test Bed (BCVTB) see http://simulationresearch.lbl.gov/bcvtb

Additional support was provided by Ptolemy II, which is supported 
by the following organizations:
The Center for Hybrid and Embedded Software Systems (CHESS)
at UC Berkeley, which receives support from the
National Science Foundation (NSF awards #0720882 (CSR-EHS: PRET),
#1035672 (CPS: PTIDES), and #0931843 (ActionWebs)),
the U. S. Army Research Laboratory (ARL #W911NF-11-2-0038),
the Air Force Research Lab (AFRL), the Multiscale Systems Center (MuSyC),
one of six research centers funded under the Focus Center Research Program,
a Semiconductor Research Corporation program, and the following companies:
Bosch, National Instruments, Thales, and Toyota.

Authors: 
David Broman (UC Berkeley)
Christopher Brooks (UC Berkeley)
Edward A. Lee (UC Berkeley)
Thierry Stephane Nouidui (Lawrence Berkeley National Laboratory)
Michael Wetter (Lawrence Berkeley National Laboratory)

Support: 
This package is delivered without support.  However, the BCVTB mailing list
at https://groups.google.com/group/bcvtb maybe be used for questions.
