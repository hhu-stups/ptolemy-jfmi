/* Read a Functional Mock-up Unit .fmu file and invoke it as a co-simulation.

   Copyright (c) 2012 The Regents of the University of California.
   All rights reserved.
   Permission is hereby granted, without written agreement and without
   license or royalty fees, to use, copy, modify, and distribute this
   software and its documentation for any purpose, provided that the above
   copyright notice and the following two paragraphs appear in all copies
   of this software.

   IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
   FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
   ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
   THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
   SUCH DAMAGE.

   THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
   PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
   CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
   ENHANCEMENTS, OR MODIFICATIONS.

   PT_COPYRIGHT_VERSION_2
   COPYRIGHTENDKEY

 */
package org.ptolemy.fmi.driver;

import java.io.File;
import java.io.PrintStream;

import org.ptolemy.fmi.FMICallbackFunctions;
import org.ptolemy.fmi.FMILibrary;
import org.ptolemy.fmi.FMIModelDescription;
import org.ptolemy.fmi.FMUFile;
import org.ptolemy.fmi.FMULibrary;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;


///////////////////////////////////////////////////////////////////
//// FMUCoSimulation

/** Read a Functional Mock-up Unit .fmu file and invoke it as a co-simulation.
 *  
 * <p>This file is based on fmusdk/src/model_exchange/fmusim_me/main.c
 * by Jakob Mauss, which has the following license:</p>
 *
 * <p>FMU SDK license</p> 
 *
 * <p>Copyright (c) 2008-2011, QTronic GmbH. All rights reserved.
 * The FmuSdk is licensed by the copyright holder under the BSD License
 * (http://www.opensource.org/licenses/bsd-license.html):
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <br>- Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * <br>- Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.</p>
 *
 * <p>THIS SOFTWARE IS PROVIDED BY QTRONIC GMBH "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL QTRONIC GMBH BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</p>
 *
 * @author Christopher Brooks, based on fmusim_cs/main.c by Jakob Mauss
 * @version $Id: FMUCoSimulation.java 63506 2012-05-08 00:53:50Z cxh $
 * @Pt.ProposedRating Red (cxh)
 * @Pt.AcceptedRating Red (cxh)
 */
public class FMUCoSimulation extends FMUDriver {

    /** Perform co-simulation using the named Functional Mock-up Unit (FMU) file.
     *          
     *  <p>Usage:</p>
     *  <pre>
     *  java -classpath ../../../lib/jna.jar:../../.. org.ptolemy.fmi.driver.FMUCoSimulation \
     *  file.fmu [endTime] [stepTime] [loggingOn] [csvSeparator] [outputFile]
     *  </pre>
     *  <p>For example, under Mac OS X or Linux:
     *  <pre>
     *  java -classpath $PTII/lib/jna.jar:${PTII} org.ptolemy.fmi.driver.FMUCoSimulation \
     *  $PTII/org/ptolemy/fmi/fmu/cs/bouncingBall.fmu 1.0 0.1 true c foo.csv
     *  </pre>
     *
     *  <p>The command line arguments have the following meaning:</p>
     *  <dl>
     *  <dt>file.fmu</dt>
     *  <dd>The co-simulation Functional Mock-up Unit (FMU) file.  In FMI-1.0,
     *  co-simulation fmu files contain a modelDescription.xml file that 
     *  has an &lt;Implementation&gt; element.  Model exchange fmu files do not
     *  have this element.</dd>
     *  <dt>endTime</dt>
     *  <dd>The endTime in seconds, defaults to 1.0.</dd>
     *  <dt>stepTime</dt>
     *  <dd>The time between steps in seconds, defaults to 0.1.</dd>
     *  <dt>enableLogging</dt>
     *  <dd>If "true", then enable logging.  The default is false.</dd>
     *  <dt>separator</dt>
     *  <dd>The comma separated value separator, the default value is
     *  ',', If the separator is ',', columns are separated by ',' and
     *  '.' is used for floating-point numbers.  Otherwise, the given
     *  separator (e.g. ';' or '\t') is to separate columns, and ','
     *  is used as decimal dot in floating-point numbers. 
     *  <dt>outputFile</dt>
     *  <dd>The name of the output file.  The default is results.csv</dd>
     *  </dl>
     *
     *  <p>The format of the arguments is based on the fmusim command from the fmusdk
     *  by QTronic Gmbh.</p>
     *
     *  @param args The arguments: file.fmu [endTime] [stepTime]
     *  [loggingOn] [csvSeparator] [outputFile]
     *  @exception Exception If there is a problem parsing the .fmu file or invoking
     *  the methods in the shared library.
     */
    public static void main(String[] args) throws Exception {
        FMUDriver._processArgs(args);
        new FMUCoSimulation().simulate(_fmuFileName, _endTime, _stepSize,
                _enableLogging, _csvSeparator, _outputFileName);
    }

    /** Perform co-simulation using the named Functional Mock-up Unit (FMU) file.
     *  @param fmuFileName The pathname of the co-simulation .fmu file
     *  @param endTime The ending time in seconds.
     *  @param stepSize The step size in seconds.
     *  @param enableLogging True if logging is enabled.
     *  @param csvSeparator The character used for separating fields.
     *  Note that sometimes the decimal point in floats is converted to ','.
     *  @param outputFileName The output file.
     *  @exception Exception If there is a problem parsing the .fmu file or invoking
     *  the methods in the shared library.
     */
    public void simulate(String fmuFileName, double endTime, double stepSize,
            boolean enableLogging, char csvSeparator, String outputFileName)
            throws Exception {

        // Avoid a warning from FindBugs.
        FMUDriver._setEnableLogging(enableLogging);

        // Parse the .fmu file.
        FMIModelDescription fmiModelDescription = FMUFile
                .parseFMUFile(fmuFileName);

        // Load the shared library.
        String sharedLibrary = FMUFile.fmuSharedLibrary(fmiModelDescription);

        if (enableLogging) {
            System.out.println("FMUCoSimulation: about to load "
                    + sharedLibrary);
        }
        _nativeLibrary = NativeLibrary.getInstance(sharedLibrary);

        // The modelName may have spaces in it.
        _modelIdentifier = fmiModelDescription.modelIdentifier;

        // The URL of the fmu file.
        String fmuLocation = new File(fmuFileName).toURI().toURL().toString();
        // The tool to use if we have tool coupling.
        String mimeType = "application/x-fmu-sharedlibrary";
        // Timeout in ms., 0 means wait forever.
        double timeout = 1000;
        // There is no simulator UI.
        byte visible = 0;
        // Run the simulator without user interaction.
        byte interactive = 0;
        // Callbacks
        FMICallbackFunctions.ByValue callbacks = new FMICallbackFunctions.ByValue(
                new FMULibrary.FMULogger(), new FMULibrary.FMUAllocateMemory(),
                new FMULibrary.FMUFreeMemory(),
                new FMULibrary.FMUStepFinished());
        // Logging tends to cause segfaults because of vararg callbacks.
        byte loggingOn = enableLogging ? (byte) 1 : (byte) 0;
        loggingOn = (byte) 0;

        Function instantiateSlave = getFunction("_fmiInstantiateSlave");
        Pointer fmiComponent = (Pointer) instantiateSlave.invoke(Pointer.class,
                new Object[] { _modelIdentifier, fmiModelDescription.guid,
                        fmuLocation, mimeType, timeout, visible, interactive,
                        callbacks, loggingOn });
        if (fmiComponent.equals(Pointer.NULL)) {
            throw new RuntimeException("Could not instantiate model.");
        }

        double startTime = 0;

        invoke("_fmiInitializeSlave", 
                new Object[] { fmiComponent, startTime, (byte) 1, endTime },
                "Could not initialize slave: ");

        File outputFile = new File(outputFileName);
        PrintStream file = null;
        try {
            file = new PrintStream(outputFile);
            if (enableLogging) {
                System.out.println("FMUCoSimulation: about to write header");
            }
            // Generate header row
            OutputRow.outputRow(_nativeLibrary, fmiModelDescription,
                    fmiComponent, startTime, file, csvSeparator, Boolean.TRUE);
            // Output the initial values.
            OutputRow.outputRow(_nativeLibrary, fmiModelDescription,
                    fmiComponent, startTime, file, csvSeparator, Boolean.FALSE);
            // Loop until the time is greater than the end time.
            double time = startTime;

            Function doStep = getFunction("_fmiDoStep");
            while (time < endTime) {
                if (enableLogging) {
                    System.out.println("FMUCoSimulation: about to call "
                            + _modelIdentifier
                            + "_fmiDoStep(Component, /* time */ " + time
                            + ", /* stepSize */" + stepSize + ", 1)");
                }
                invoke(doStep,
                        new Object[] {fmiComponent, time, stepSize, (byte) 1 },
                        "Could not simulate, time was "
                        + time + ": ");
                time += stepSize;
                // Generate a line for this step
                OutputRow.outputRow(_nativeLibrary, fmiModelDescription,
                        fmiComponent, time, file, csvSeparator, Boolean.FALSE);
            }
        } finally {
            if (file != null) {
                file.close();
            }
        }

        invoke("_fmiTerminateSlave",
                new Object[] { fmiComponent },
                "Could not terminate slave: ");

        // Don't throw an exception while freeing a slave.  Some
        // fmiTerminateSlave calls free the slave for us.
        Function freeSlave = getFunction("_fmiFreeSlaveInstance");
        int fmiFlag = ((Integer) freeSlave.invoke(Integer.class,
                        new Object[] { fmiComponent })).intValue();
        if (fmiFlag >= FMILibrary.FMIStatus.fmiWarning) {
            new Exception("Warning: Could not free slave instance: " + fmiFlag).printStackTrace();
        }

        if (enableLogging) {
            System.out.println("Results are in "
                    + outputFile.getCanonicalPath());
        }
    }
}
