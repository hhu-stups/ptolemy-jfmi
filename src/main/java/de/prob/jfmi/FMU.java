package de.prob.jfmi;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.ptolemy.fmi.FMICallbackFunctions;
import org.ptolemy.fmi.FMILibrary;
import org.ptolemy.fmi.FMIModelDescription;
import org.ptolemy.fmi.FMUFile;
import org.ptolemy.fmi.FMULibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class FMU {

	private static final Logger logger = LoggerFactory.getLogger(FMU.class);

	private final FMIModelDescription modelDescription;

	public FMU(String fmuFileName) throws IOException {
		modelDescription = FMUFile.parseFMUFile(fmuFileName);
		String sharedLibrary = FMUFile.fmuSharedLibrary(modelDescription);
		_nativeLibrary = NativeLibrary.getInstance(sharedLibrary);

		// The modelName may have spaces in it.
		_modelIdentifier = modelDescription.modelIdentifier;

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
		byte loggingOn = (byte) 1;

		Function instantiateSlave = getFunction("_fmiInstantiateSlave");
		Pointer fmiComponent = (Pointer) instantiateSlave.invoke(Pointer.class,
				new Object[] { _modelIdentifier, modelDescription.guid,
						fmuLocation, mimeType, timeout, visible, interactive,
						callbacks, loggingOn });
		if (fmiComponent.equals(Pointer.NULL)) {
			throw new RuntimeException("Could not instantiate model.");
		}

		double startTime = 0;
		double endTime = 5;

		invoke("_fmiInitializeSlave", new Object[] { fmiComponent, startTime,
				(byte) 1, endTime }, "Could not initialize slave: ");

	}

	public String getFmiVersion() {
		assert _nativeLibrary != null;
		Function function = getFunction("_fmiGetVersion");
		return (String) function.invoke(String.class, new Object[0]);
	}

	public FMIModelDescription getModelDescription() {
		return modelDescription;
	}

	public static void main(String[] args) throws IOException {
		String fmuFileName = "waterTankEnv.fmu";
		URL resource = ClassLoader.getSystemResource(fmuFileName);
		FMU f = new FMU(resource.getFile());
		System.out.println("FMI Version: " + f.getFmiVersion());
	}

	/**
	 * Return a function by name.
	 * 
	 * @param name
	 *            The name of the function. The value of the modelIdentifier is
	 *            prepended to the value of this parameter to yield the function
	 *            name.
	 * @return the function.
	 */
	public Function getFunction(String name) {
		// This is syntactic sugar.
		logger.debug("Getting the {} function.", name);
		return _nativeLibrary.getFunction(_modelIdentifier + name);
	}

	/**
	 * Invoke a function that returns an integer representing the FMIStatus
	 * return value.
	 * 
	 * @param name
	 *            The name of the function.
	 * @param arguments
	 *            The arguments to be passed to the function.
	 * @param message
	 *            The error message to be used if there is a problem. The
	 *            message should end with ": " because the return value of the
	 *            function will be printed after the error message.
	 */
	public void invoke(String name, Object[] arguments, String message) {
		Function function = getFunction(name);
		invoke(function, arguments, message);
	}

	/**
	 * Invoke a function that returns an integer representing the FMIStatus
	 * return value.
	 * 
	 * @param function
	 *            The function to be invoked.
	 * @param arguments
	 *            The arguments to be passed to the function.
	 * @param message
	 *            The error message to be used if there is a problem. The
	 *            message should end with ": " because the return value of the
	 *            function will be printed after the error message.
	 */
	public void invoke(Function function, Object[] arguments, String message) {
		logger.debug("Invoking  {}", function.getName());
		int fmiFlag = ((Integer) function.invoke(Integer.class, arguments))
				.intValue();
		if (fmiFlag > FMILibrary.FMIStatus.fmiWarning) {
			throw new RuntimeException(message + fmiFlag);
		}
	}

	// /////////////////////////////////////////////////////////////////
	// // protected fields ////

	/** The modelIdentifier from modelDescription.xml. */
	String _modelIdentifier;

	/** The NativeLibrary that contains the functions. */
	NativeLibrary _nativeLibrary;

}
