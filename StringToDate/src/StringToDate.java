import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

public class StringToDate {

	static String debugMode=null;
	static List<String> formatStrings = Arrays.asList("yyyy-MM-dd", "yyyy-dd-MM", "dd-MM-yyyy", "MM-dd-yyyy",
			"EEE, MMM d, ''yy", "''yy, d.MMM", "yyyyMMdd", "ddMMyyyy", "yyMMdd", "dMMyy","yyMM", "MMdyy", "yyMMd");
	static List<String> formatStrings1 = Arrays.asList("yyMMdd", "ddMMyy");
	
	
	
	static Map<String, String> monthMap = Stream.of(new String[][] { { "jan", "01" }, { "feb", "02" }, { "dec", "12" },
			{ "January", "01" }, { "February", "02" }, { "March", "03" }, { "May", "05" }, { "June", "06" },
			{ "October", "10" }, { "Januar", "01" }, { "Februar", "02" }, { "März", "03" }, { "April", "04" },
			{ "Juni", "06" }, { "July", "07" }, { "August", "08" }, { "September", "09" }, { "Oktober", "10" },
			{ "November", "11" }, { "Dezember", "12" }, { "Jan", "01" }, { "Feb", "02" }, { "Mrz", "03" },
			{ "Jul", "07" }, { "Sep", "09" }, { "Nov", "11" }, { "Dez", "12" }, { "Mar", "03" }, { "Apr", "04" },
			{ "Oct", "10" }, { "Dec", "12" }, { "Febr", "02" }, { "Mär", "03" }, { "Aprl", "04" }, { "Mai", "05" },
			{ "Jun", "06" }, { "Jli", "07" }, { "Aug", "08" }, { "Sept", "09" }, { "Okt", "10" },{ "Juli", "07"}, { "Novem", "11" },
			{ "Dezem", "12"}, }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));

	public static void main(String[] args) {
		List<String[]> csvData;
		
		if(args.length==2) {
		debugMode=args[1];
		
		}
		 if (args[0] != null && !args[0].isEmpty()) {
		csvData = readFileData(args[0]);
		
		//csvData = readFileData("C:\\Users\\nayan\\Downloads\\Out-Search_dates_test_data.tsv");
		// data.stream().forEach(row -> System.out.println(row[0].toString()) );
		
		String[] header = csvData.get(0);
		List<String> arrayList = new ArrayList<String>(Arrays.asList(header));

		arrayList.add(1,"new-date-of-work");
		header = arrayList.toArray(header);
		List<String[]> finalData = stringToDate(csvData);
		List<String[]> newList = new ArrayList<String[]>();
		newList.add(header);
		newList.addAll(finalData);
		writeDataToFile(newList);
		
		  } else { System.out.println("can-not find test.csv file"); }
		 
	}

	private static void writeDataToFile(List<String[]> finalData) {


		try (ICSVWriter writer = new CSVWriterBuilder(
		          new FileWriter("output.tsv"))
		          .withSeparator('	')
		          .build()) {
		      writer.writeAll(finalData);
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static List<String[]> readFileData(String sourceFilePath) {
		List<String[]> data = new ArrayList<String[]>();

		try {
			File sourceFile = new File(sourceFilePath); // creates a new file instance

			data = Files.lines(sourceFile.toPath()).map(line -> line.split("\t")).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	private static List<String[]> stringToDate(List<String[]> data) {

		data.remove(0);
		List<String[]> parsedData = data.stream().map(StringToDate::parseDate).collect(Collectors.toList());
		return parsedData;
	}

	private static String[] parseDate(String[] row) {

		String[] rows = row[0].toString().split("\t");


		List<String> arrayList = new ArrayList<String>(Arrays.asList(row));

		if(debugMode!=null && debugMode.equalsIgnoreCase("debug")  ) {
		arrayList.add(1,tryParse(row[0].toString().split("\t")[0]).toString());
		}else {
			arrayList.add(1,tryParse(row[0].toString().split("\t")[0]).get(0).toString());
		}
		row = arrayList.toArray(row);

		return row;
	}


	static List<String> tryParse(String dateString) {
		Date d;
		Date now = new Date();
		List<String> finalDates = new ArrayList<String>();

		dateString = dateString.replaceAll("[$&+,:;=?@#|'<>.-]", "");
		dateString = dateString.replaceAll(" ", "").replaceAll("st", "")
				.replaceAll("nd","").replaceAll("rd", "").replaceAll("of", "").replaceAll("th", "");
		if (dateString.contains("C") && dateString.matches("\\d*[C]\\d*")) {
			dateString = dateString.replace("C", "");
		}

		String month = dateString.replaceAll("[0-9]", "");
		if (monthMap.containsKey(month)) {
			dateString = dateString.replace(month, monthMap.get(month));
			
		}
		if(!dateString.equalsIgnoreCase("nix")) {
			dateString = dateString.replaceAll("[a-zA-Z]", "");
		}
		if(dateString.length() == 4) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
				formatter.setLenient(false);
				d = formatter.parse(dateString);
				if (now.compareTo(d) > 0) {
					SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");

					finalDates.add(defaultFormat.format(d));
				}
			} catch (ParseException e) {

			}
		}else		if (dateString.length() == 6) {
			for (String formatString : formatStrings1) {

				try {
					SimpleDateFormat formatter = new SimpleDateFormat(formatString);
					formatter.setLenient(false);
					d = formatter.parse(dateString);
					if (now.compareTo(d) > 0) {
						SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");

						finalDates.add(defaultFormat.format(d));
					}
				} catch (ParseException e) {

				}
			}
		} else {
			for (String formatString : formatStrings) {

				try {
					SimpleDateFormat formatter = new SimpleDateFormat(formatString);
					formatter.setLenient(false);
					d = formatter.parse(dateString);
					SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
					finalDates.add(defaultFormat.format(d));
				} catch (ParseException e) {

				}
			}
		}
		if (finalDates.size() < 1)
			finalDates.add(dateString);

		return finalDates;

	}

}
