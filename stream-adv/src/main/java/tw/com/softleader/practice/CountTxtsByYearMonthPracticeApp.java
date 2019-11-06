package tw.com.softleader.practice;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import tw.com.softleader.commons.function.Parses;

public class CountTxtsByYearMonthPracticeApp {
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyMMdd");

	/**
	 * 將resource/txts中所有的txt檔的所屬資料夾為期案件編號 案件編號以HW開頭者為線上交易進件，TP開頭則代表為台北區的書面掃描進件
	 *
	 * 案件編號格式說明: 線上交易件 HW01061129000001, 其中1061129為進件日期, 格式(民國年)YYYMMDD 台北區的書面件
	 * TP060821191749B0, 其中060821為進件日期, 只有民國100年以後的資料, 因此開頭須補1, 格式(民國年)YYYMMDD
	 *
	 * 請依照月份統計，蒐集每月案件數
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Stream();
	}

	public static void Stream() throws Exception {
		final URL txtsUrl = CountTxtsByYearMonthPracticeApp.class.getResource("/txts");
		final Path txtPath = Paths.get(txtsUrl.toURI());
		final File[] files = txtPath.toFile().listFiles();
		final Map<String, List<YearMonth>> result = Arrays.asList(files).stream()
				.filter(File::isDirectory)
				.map(x -> x.getName().contains("HW") ? x.getName().substring(3, 10) : 1 + x.getName().substring(2, 8))
				.map(x -> Parses.tryMinguoDate(x, FORMATTER)).map(YearMonth::from)
				.collect(Collectors.groupingBy(YearMonth::toString));
		result.forEach((k, v) -> System.out.println(k + "=" + v.size()));
//		 Map<YearMonth, Integer> result = new HashMap<>();
//				Stream<File> tp= Arrays.stream(files).filter(x->x.getName().startsWith("TP"));
//				Stream<File> hw= Arrays.stream(files).filter(x->x.getName().startsWith("HW"));
//				tp.filter(File::isDirectory)
//				.map(x->1+x.getName().substring(2, 8))
//				.map(x->Parses.tryMinguoDate(x, FORMATTER))
//				.filter(x->x!=null)
//				.map(x->YearMonth.from(x))
//				.forEach(x->{
//					if(!result.containsKey(x)) {
//						result.put(x, 1);
//					}else {
//						result.replace(x, result.get(x)+1);
//					}
//				});
//				hw.filter(File::isDirectory)
//				.map(x->x.getName().substring(3, 10))
//				.map(x->Parses.tryMinguoDate(x, FORMATTER))
//				.filter(x->x!=null)
//				.map(x->YearMonth.from(x))
//				.forEach(x->{
//					if(!result.containsKey(x)) {
//						result.put(x, 1);
//					}else {
//						result.replace(x, result.get(x)+1);
//					}
//				});
//		result.forEach((k, v) -> System.out.println(k + "=" + v));
	}

	/**
	 * 不使用Stream的範例
	 * 
	 * @throws Exception
	 */
	public static void nonStream() throws Exception {
		final URL txtsUrl = CountTxtsByYearMonthPracticeApp.class.getResource("/txts");
		final Path txtPath = Paths.get(txtsUrl.toURI());

		final Map<YearMonth, List<String>> result = new TreeMap<>();
		final File[] files = txtPath.toFile().listFiles();
		for (File file : files) {
			// txts下第一層一定是資料夾，不是資料夾的就略過
			if (file.isDirectory()) {
				// 資料夾的名稱即為案件編號
				final String caseNo = file.getName();
				final String dateStr;
				if (caseNo.startsWith("HW")) {
					dateStr = caseNo.substring(3, 10);
				} else if (caseNo.startsWith("TP")) {
					dateStr = "1" + caseNo.substring(2, 8);
				} else {
					// 非預期的開頭不處理
					continue;
				}

				// 處理民國年字串轉日期
				final MinguoDate date = Parses.tryMinguoDate(dateStr, FORMATTER);
				// 擷取年月資訊
				final YearMonth yearMonth = YearMonth.from(date);

				// 蒐集
				if (!result.containsKey(yearMonth)) {
					result.put(yearMonth, Lists.newArrayList());
				}
				result.get(yearMonth).add(caseNo);
			}
		}

		result.forEach((ym, caseNos) -> {
			System.out.println(ym + ": " + caseNos.size());
		});
	}

}
