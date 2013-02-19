package de.prob.jfmi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.ptolemy.fmi.driver.FMUCoSimulation;

public class WaterTankTest {

	@Test
	public void test() throws Exception {
		String fmuFileName = "waterTankEnv.fmu";
		URL resource = ClassLoader.getSystemResource(fmuFileName);
		FMU f = new FMU(resource.getFile());

		fail();
	}

}
