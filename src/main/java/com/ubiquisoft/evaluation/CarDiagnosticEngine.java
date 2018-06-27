package com.ubiquisoft.evaluation;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		/*
		 * Implement basic diagnostics and print results to console.
		 *
		 * The purpose of this method is to find any problems with a car's data
		 * or parts.
		 *
		 * Diagnostic Steps: First - Validate the 3 data fields are present, if
		 * one or more are then print the missing fields to the console in a
		 * similar manner to how the provided methods do.
		 *
		 * Second - Validate that no parts are missing using the
		 * 'getMissingPartsMap' method in the Car class, if one or more are then
		 * run each missing part and its count through the provided missing part
		 * method.
		 *
		 * Third - Validate that all parts are in working condition, if any are
		 * not then run each non-working part through the provided damaged part
		 * method.
		 *
		 * Fourth - If validation succeeds for the previous steps then print
		 * something to the console informing the user as such. A damaged part
		 * is one that has any condition other than NEW, GOOD, or WORN.
		 *
		 * Important: If any validation fails, complete whatever step you are
		 * actively one and end diagnostics early.
		 *
		 * Treat the console as information being read by a user of this
		 * application. Attempts should be made to ensure console output is as
		 * least as informative as the provided methods.
		 */
		if (car == null)
			throw new IllegalArgumentException("Car must not be null");
		
		boolean isCarValid = validateDataFields(car);
		
		if (isCarValid)
			validateMissingParts(car);
		
		if (isCarValid)
			validateWorkingParts(car);
		
		if (isCarValid) {
			System.out.println(String.format("The validation for car: " + car.getMake() + "/"
					+ car.getModel() + " passed."));
		} else {
			System.out.println(String.format("Validation errors occurred for car: " + car.getMake()
					+ "/" + car.getModel() + "."));
		}
	}

	public boolean validateDataFields(Car car) {
		if (car.getYear() == null)
			throw new IllegalArgumentException("Car year must not be null");
		if (car.getMake() == null)
			throw new IllegalArgumentException("Car make must not be null");
		if (car.getModel() == null)
			throw new IllegalArgumentException("Car make must not be null");

		if (car.getYear().isEmpty())
			printMissingField("Year");
		if (car.getMake().isEmpty())
			printMissingField("Make");
		if (car.getModel().isEmpty())
			printMissingField("Model");
		return !car.getYear().isEmpty() && !car.getMake().isEmpty() && !car.getModel().isEmpty();
	}

	public boolean validateMissingParts(Car car) {
		boolean isCarValid = true;
		Map<PartType, Integer> missingPartMap = car.getMissingPartsMap();
		if (missingPartMap.size() > 0) {
			Iterator<Entry<PartType, Integer>> it = missingPartMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<PartType, Integer> pair = (Map.Entry<PartType, Integer>) it.next();
				PartType partType = pair.getKey();
				Integer needed = pair.getValue();
				printMissingPart(partType, needed);
				isCarValid = false;
			}
		}
		return isCarValid;
	}

	public boolean validateWorkingParts(Car car) {
		List<Part> badPartsList = car.getParts().stream().filter(p -> !p.isInWorkingCondition())
				.collect(Collectors.toList());
		badPartsList.forEach(b -> printDamagedPart(b.getType(), b.getCondition()));
		return badPartsList.isEmpty();
	}

	private void printMissingField(String field) {
		if (field == null)
			throw new IllegalArgumentException("Field must not be null");
		System.out.println(String.format("Missing Field(s) Detected: %s ", field));
	}

	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");

		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {
		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		diagnosticEngine.executeDiagnostics(car);

	}

}
