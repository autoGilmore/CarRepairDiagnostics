package com.ubiquisoft.evaluation.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Car {

	private String year;
	private String make;
	private String model;

	private List<Part> parts;

	/*
	 * Return map of the part types missing.
	 *
	 * Each car requires one of each of the following types: ENGINE, ELECTRICAL,
	 * FUEL_FILTER, OIL_FILTER and four of the type: TIRE
	 *
	 * Example: a car only missing three of the four tires should return a map
	 * like this:
	 *
	 * { "TIRE": 3 }
	 */
	public Map<PartType, Integer> getMissingPartsMap() {

		List<Part> carPartsList = getParts();

		Map<PartType, Integer> missingPartMap = getMinimumRequiredPartCount(carPartsList);

		return missingPartMap;
	}

	private Map<PartType, Integer> getMinimumRequiredPartCount(List<Part> partList) {

		Map<PartType, Integer> missingPartMap = getRequiredPartTypesMap();

		for (Part part : partList)
			missingPartMap = foundNeededPartType(part.getType(), missingPartMap);

		return missingPartMap;
	}

	private Map<PartType, Integer> getRequiredPartTypesMap() {
		Map<PartType, Integer> missingPartMap = new HashMap<PartType, Integer>();
		missingPartMap.put(PartType.ENGINE, 1);
		missingPartMap.put(PartType.ELECTRICAL, 1);
		missingPartMap.put(PartType.FUEL_FILTER, 1);
		missingPartMap.put(PartType.OIL_FILTER, 1);
		missingPartMap.put(PartType.TIRE, 4);
		return missingPartMap;
	}

	private Map<PartType, Integer> foundNeededPartType(PartType foundPart,
			Map<PartType, Integer> missingPartMap) {
		// TODO: stream this
		Iterator<Entry<PartType, Integer>> it = missingPartMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<PartType, Integer> pair = (Map.Entry<PartType, Integer>) it.next();
			PartType partType = pair.getKey();
			if (partType == foundPart) {
				Integer needed = pair.getValue();
				if (needed > 1) {
					missingPartMap.put(partType, missingPartMap.get(partType) - 1);
				} else {
					it.remove();
				}
				break;
			}
		}
		return missingPartMap;
	}

	@Override
	public String toString() {
		return "Car{" +
				       "year='" + year + '\'' +
				       ", make='" + make + '\'' +
				       ", model='" + model + '\'' +
				       ", parts=" + parts +
				       '}';
	}

	/* --------------------------------------------------------------------------------------------------------------- */
	/*  Getters and Setters *///region
	/* --------------------------------------------------------------------------------------------------------------- */

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

	/* --------------------------------------------------------------------------------------------------------------- */
	/*  Getters and Setters End *///endregion
	/* --------------------------------------------------------------------------------------------------------------- */

}
