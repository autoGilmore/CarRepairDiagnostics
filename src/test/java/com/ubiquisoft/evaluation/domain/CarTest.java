package com.ubiquisoft.evaluation.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

public class CarTest {

	@Test
	public void test_getMissingPartsMap() {
		// Set up
		Car car = getCompleteTestCar();

		// Test
		Map<PartType, Integer> partTypeMissingCountMap = car.getMissingPartsMap();

		// Verify
		assertNotNull(partTypeMissingCountMap);
		assertTrue("There should be no missing parts", partTypeMissingCountMap.isEmpty());
	}

	@Test
	public void test_getMissingPartsMap_missingOneTire() {
		// Set up
		Car car = getCompleteTestCar();

		// remove a tire
		int tireIndex = car.getParts().size() - 1;
		if (car.getParts().get(tireIndex).getType() == PartType.TIRE) {
			car.getParts().remove(tireIndex);
		} else {
			fail("Index for tire part was incorrect");
		}

		// Test
		Map<PartType, Integer> partTypeMissingCountMap = car.getMissingPartsMap();

		// Verify
		assertNotNull(partTypeMissingCountMap);
		assertTrue(partTypeMissingCountMap.toString().equals("{TIRE=1}"));
	}

	public static Car getCompleteTestCar() {
		Car car = new Car();
		car.setMake("Honda");
		car.setModel("CRV");
		List<Part> parts = getCompleteTestPartsList();
		car.setParts(parts);
		car.setYear("1997");
		return car;
	}

	private static List<Part> getCompleteTestPartsList() {
		List<Part> partList = new ArrayList<Part>();
		partList.add(getTestEngine());
		partList.add(getTestElectrical());
		partList.add(getTestFuel());
		partList.add(getTestOilFilter());
		partList.add(getTestTire());
		partList.add(getTestTire());
		partList.add(getTestTire());
		partList.add(getTestTire());
		return partList;
	}

	private static Part getTestEngine() {
		Part part = new Part();
		part.setCondition(ConditionType.GOOD);
		part.setInventoryId("1234");
		part.setType(PartType.ENGINE);
		return part;
	}

	private static Part getTestElectrical() {
		Part part = new Part();
		part.setCondition(ConditionType.GOOD);
		part.setInventoryId("1234");
		part.setType(PartType.ELECTRICAL);
		return part;
	}

	private static Part getTestFuel() {
		Part part = new Part();
		part.setCondition(ConditionType.GOOD);
		part.setInventoryId("1234");
		part.setType(PartType.FUEL_FILTER);
		return part;
	}

	private static Part getTestOilFilter() {
		Part part = new Part();
		part.setCondition(ConditionType.GOOD);
		part.setInventoryId("1234");
		part.setType(PartType.OIL_FILTER);
		return part;
	}

	private static Part getTestTire() {
		Part part = new Part();
		part.setCondition(ConditionType.GOOD);
		part.setInventoryId("1234");
		part.setType(PartType.TIRE);
		return part;
	}

}
