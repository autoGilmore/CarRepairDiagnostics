package com.ubiquisoft.evaluation;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.CarTest;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

public class CarDiagnosticEngineTest {

	private CarDiagnosticEngine diagnostic = new CarDiagnosticEngine();

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errorContent = new ByteArrayOutputStream();

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errorContent));
	}

	@Test
	public void outByteArrayOutputStreamTest() {
		System.out.print("hello");
		assertTrue(outContent.toString().equals("hello"));
	}

	@Test
	public void test_executeDiagnostics() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// Test
		diagnostic.executeDiagnostics(car);

		// Verify
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("passed."));
	}

	@Test
	public void test_executeDiagnostics_null() {
		// Set up
		Car car = null;

		// Test: null
		try {
			diagnostic.executeDiagnostics(car);
		} catch (IllegalArgumentException e) {
			// Verify
			assertTrue(e.getMessage(), e.getMessage().contains("Car must not be null"));
		}
	}

	@Test
	public void test_executeDiagnostics_failEarly() {
		// Set up
		Car car = CarTest.getCompleteTestCar();
		// missing year
		car.setYear("");
		// bad tire
		int tireIndex = car.getParts().size() - 1;
		car.getParts().get(tireIndex).setCondition(ConditionType.DAMAGED);

		// Test
		diagnostic.executeDiagnostics(car);

		// Verify : first validation error present
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("Year"));

		// Verify : second validation error NOT present
		assertFalse("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: " + PartType.TIRE));
	}

	@Test
	public void test_validateDataFields() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// Test: missing year
		car.setYear("");
		diagnostic.validateDataFields(car);

		// Verify
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("Year"));

		// Test: missing Make
		car.setYear("1986");
		car.setMake("");
		diagnostic.validateDataFields(car);

		// Verify
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("Make"));

		// Test: missing Make
		car.setMake("Honda");
		car.setModel("");
		diagnostic.validateDataFields(car);

		// Verify
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("Model"));

		// Test: Multiple missing
		car.setMake("");
		car.setModel("");
		diagnostic.validateDataFields(car);

		// Verify
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("Make"));
		assertTrue("was: " + outContent.toString(), outContent.toString().contains("Model"));
	}

	@Test
	public void test_validateMissingParts_valid() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// Test
		boolean isValid = diagnostic.validateMissingParts(car);

		// Verify
		assertTrue("was: " + outContent.toString(), outContent.toString().isEmpty());
		assertTrue(isValid);
	}

	@Test
	public void test_validateMissingParts_removeATire() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// remove a tire
		int tireIndex = car.getParts().size() - 1;
		car.getParts().remove(tireIndex);

		// Test
		boolean isValid = diagnostic.validateMissingParts(car);

		// Verify
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: " + PartType.TIRE));
		assertFalse(isValid);
	}

	@Test
	public void test_validateMissingParts_removeAllPart() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// remove all parts
		car.setParts(new ArrayList<Part>());

		// Test
		boolean isValid = diagnostic.validateMissingParts(car);

		// Verify
		assertFalse(isValid);
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: ENGINE - Count: 1"));
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: OIL_FILTER - Count: 1"));
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: ELECTRICAL - Count: 1"));
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: FUEL_FILTER - Count: 1"));
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Missing Part(s) Detected: TIRE - Count: 4"));
	}

	@Test
	public void test_validateWorkingParts_goodParts() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// Test
		boolean isValid = diagnostic.validateWorkingParts(car);

		// Verify
		assertTrue(isValid);
		assertTrue("was: " + outContent.toString(), outContent.toString().isEmpty());

		// Test: NEW, GOOD, or WORN
		int tireIndex = car.getParts().size() - 1;
		car.getParts().get(tireIndex).setCondition(ConditionType.NEW);
		assertTrue(diagnostic.validateWorkingParts(car));
		car.getParts().get(tireIndex).setCondition(ConditionType.GOOD);
		assertTrue(diagnostic.validateWorkingParts(car));
		car.getParts().get(tireIndex).setCondition(ConditionType.WORN);
		assertTrue(diagnostic.validateWorkingParts(car));
	}

	@Test
	public void test_validateWorkingParts_badParts() {
		// Set up
		Car car = CarTest.getCompleteTestCar();

		// add a bad tire
		int tireIndex = car.getParts().size() - 1;
		car.getParts().get(tireIndex).setCondition(ConditionType.DAMAGED);

		// damage the engine
		car.getParts().get(car.getParts().indexOf(PartType.ENGINE) + 1)
				.setCondition(ConditionType.SIEZED);

		// Test
		boolean isValid = diagnostic.validateWorkingParts(car);

		// Verify
		assertFalse(isValid);
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains("Damaged Part Detected:"));
		// tire
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains(ConditionType.DAMAGED.toString()));
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains(PartType.TIRE.toString()));
		// engine
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains(PartType.ENGINE.toString()));
		assertTrue("was: " + outContent.toString(),
				outContent.toString().contains(ConditionType.SIEZED.toString()));

		// Test: other bad FLAT, NO_POWER, SIEZED, CLOGGED;
		car.getParts().get(car.getParts().indexOf(PartType.ENGINE) + 1)
				.setCondition(ConditionType.NEW);
		car.getParts().get(tireIndex).setCondition(ConditionType.FLAT);
		assertFalse(diagnostic.validateWorkingParts(car));
		car.getParts().get(tireIndex).setCondition(ConditionType.NO_POWER);
		assertFalse(diagnostic.validateWorkingParts(car));
		car.getParts().get(tireIndex).setCondition(ConditionType.SIEZED);
		assertFalse(diagnostic.validateWorkingParts(car));
		car.getParts().get(tireIndex).setCondition(ConditionType.CLOGGED);
		assertFalse(diagnostic.validateWorkingParts(car));
	}

}
