package tw.com.softleader.practice;

import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import tw.com.softleader.sample.data.TimeClockDatas;
import tw.com.softleader.sample.data.TimeClockEntity;

public class CollectorPracticeApp {

	public static void main(String[] args) {
		final List<TimeClockEntity> timeClocks = TimeClockDatas.get();

		groupingByName(timeClocks);
		groupingByNameWithSumSalary(timeClocks);
		toMapByName(timeClocks);
		toMapByNameWithSumSalary(timeClocks);
		groupingByNameAndWeekWithSumSalary(timeClocks);
	}

	/**
	 * 以人名分組
	 */
	public static void groupingByName(List<TimeClockEntity> timeClocks) {
		Map<String, List<TimeClockEntity>> map = new HashMap<>();
		for (TimeClockEntity t : timeClocks) {
			if (!map.containsKey(t.getEmployeeName())) {
				List<TimeClockEntity> list = new ArrayList<>();
				list.add(t);
				map.put(t.getEmployeeName(), list);
			} else {
				map.get(t.getEmployeeName()).add(t);
			}
		}

//				 Map<String, List<TimeClockEntity>> map = timeClocks.stream()
//				.collect(Collectors.groupingBy(TimeClockEntity::getEmployeeName));
		System.out.println(map);
	}

	/**
	 * 以人名分組 並算出每個人的合計薪資
	 */
	public static void groupingByNameWithSumSalary(List<TimeClockEntity> timeClocks) {
		Map<String, Double> map = new HashMap<>();
		for (TimeClockEntity t : timeClocks) {
			if (!map.containsKey(t.getEmployeeName())) {
				List<TimeClockEntity> list = new ArrayList<>();
				list.add(t);
				map.put(t.getEmployeeName(), t.calcSalary());
			} else {
				map.replace(t.getEmployeeName(), map.get(t.getEmployeeName()) + t.calcSalary());
			}
		}
//		Map<String, Double> map = timeClocks.stream().collect(Collectors.groupingBy(TimeClockEntity::getEmployeeName,
//				Collectors.summingDouble(TimeClockEntity::calcSalary)));
		System.out.println(map);
	}

	/**
	 * 以人名分組
	 */
	public static void toMapByName(List<TimeClockEntity> timeClocks) {
		Map<String, TimeClockEntity> map = timeClocks.stream()
				.collect(Collectors.toMap(TimeClockEntity::getEmployeeName, Function.identity(), (x, y) -> x));
		System.out.println(map);
	}

	/**
	 * 以人名分組 並算出每個人合計薪資 (因為沒有collect成list的動作，因此比上面快)
	 */
	public static void toMapByNameWithSumSalary(List<TimeClockEntity> timeClocks) {
		Map<String, Double> map = timeClocks.stream().collect(
				Collectors.toMap(TimeClockEntity::getEmployeeName, TimeClockEntity::calcSalary, (x, y) -> x + y));
		System.out.println(map);
	}

	/**
	 * 根據人名分組，再根據每周分組 並算出每人每周的總薪水
	 */
	public static void groupingByNameAndWeekWithSumSalary(List<TimeClockEntity> timeClocks) {
		Map<String, Map<Integer, Double>> map = timeClocks.stream()
				.collect(Collectors.groupingBy(TimeClockEntity::getEmployeeName,
						Collectors.groupingBy(x -> x.getTimeIn().get(WeekFields.ISO.weekOfYear()),
								Collectors.summingDouble(TimeClockEntity::calcSalary))));
		System.out.println(map);
	}

}
