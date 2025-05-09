import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tribes {
	private static URL gathererURL(String creatureSubtypes, String colorIdentity) {
		StringBuilder url = new StringBuilder(
			"https://gatherer.wizards.com/Pages/Search/Default.aspx?action=advanced&type=+[%22Creature%22]");

		// add subtypes
		if (creatureSubtypes != null) {
			url.append("&subtype=");
			for (String subtype : creatureSubtypes.split("\\s+"))
				url.append("+[m/\\b").append(subtype).append("\\b/]");
		}

		// add color identity
		if (colorIdentity != null) {
			url.append("&color=");
			for (String color : new String[] {"W", "U", "B", "R", "G"}) {
				url.append('+');
				if (!colorIdentity.contains(color))
					url.append('!');
				url.append("[%22").append(color).append("%22]");
			}
		}

		try {
			return new URL(url.toString());
		} catch(MalformedURLException ex) {
			throw new Error(ex);
		}
	}

	private static URL multiclassGathererURL(String creatureSubtype) {
		StringBuilder url = new StringBuilder(
			"https://gatherer.wizards.com/Pages/Search/Default.aspx?action=advanced&type=+[%22Creature%22]");
		url.append("&subtype=+[m/\\b").append(creatureSubtype).append("\\b/]");
		url.append("+[m/^(?!").append(creatureSubtype).append("$).+/]");

		try {
			return new URL(url.toString());
		} catch(MalformedURLException ex) {
			throw new Error(ex);
		}
	}

	private static String readAll(InputStream input) throws IOException {
		StringBuilder str = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		int c;
		while ((c = in.read()) >= 0)
			str.append((char) c);
		return str.toString();
	}

	private static List<String> readAllLines(InputStream input) throws IOException {
		List<String> lines = new ArrayList<>();
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		String line;
		while ((line = in.readLine()) != null)
			lines.add(line);
		return lines;
	}

	private static String html(URL gathererURL) throws IOException {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) gathererURL.openConnection();
			connection.setRequestMethod("GET");
		} catch(ClassCastException | ProtocolException ex) {
			throw new Error(ex);
		}

		try (InputStream in = connection.getInputStream()) {
			String html = readAll(in);
			connection.disconnect();
			return html;
		}
	}

	private static String tryHtml(URL gathererUrl) {
		try {
			return html(gathererUrl);
		} catch(IOException ex) {
			return null;
		}
	}

	private static int scrapeCount(String gathererHtml) {
		final String target = "id=\"ctl00_ctl00_ctl00_MainContent_SubContent_SubContentHeader_searchTermDisplay\"";
		int i = gathererHtml.indexOf(target) + target.length();
		i = gathererHtml.indexOf("</span>", i) - 1;
		i = gathererHtml.lastIndexOf("(", i) + 1;
		int j = gathererHtml.indexOf(")", i);
		return Integer.parseInt(gathererHtml.substring(i, j));
	}

	private static int safeScrapeCount(String gathererHtml) {
		if (gathererHtml != null)
			try {
				return scrapeCount(gathererHtml);
			} catch(NumberFormatException ex) {}
		return -1;
	}

	public static void printColorTable(List<String> types) throws IOException {
		System.out.println("--- TYPE COLOR TABLE: ---");

		final String[] cIds = {
			"",  // colorless
			"W", "U", "B", "R", "G",  // monocolored
			"WU", "UB", "BR", "RG", "GW",  // allied colors
			"WB", "UR", "BG", "RW", "GU",  // enemy colors
			"WUB", "UBR", "BRG", "RGW", "GWU",  // shards
			"WBG", "URW", "BGU", "RWB", "GUR",  // clans
			"WUBR", "UBRG", "BRGW", "RGWU", "GWUB",
			"WUBRG"  // rainbow
		};
		String[][] table = new String[types.size()][2 + 32];

		// table headers
		System.out.print("Name");
		for (String cId : cIds)
			System.out.print(",\t" + cId);
		System.out.println(",\tTotal");
		
		// fill table
		for (int i = 0; i < types.size(); i++) {
			String type = types.get(i);
			int total = 0;
			System.out.print(table[i][0] = type);
			for (int j = 0; j < cIds.length; j++) {
				String cId = cIds[j];
				int count = safeScrapeCount(tryHtml(gathererURL(type, cId)));
				System.out.print(",\t" + (table[i][2 + j] = "" + count));
				total += count;
			}
			System.out.println(",\t" + (table[i][1] = "" + total));
		}

		// sort table. output again.
		System.out.println("\nSorted:");
		Arrays.sort(table, (rowA, rowB) -> {
			return Integer.parseInt(rowB[1]) - Integer.parseInt(rowA[1]);
		});

		// headers:
		System.out.print("Type,\tTotal");
		for(String cId : cIds)
			System.out.print(",\t" + cId);
		System.out.println();
		// data
		for (String[] row : table) {
			System.out.print(row[0] + ",\t" + row[1]);
			for (int j = 0; j < cIds.length; j++)
				System.out.print(",\t" + row[2 + j]);
			System.out.println();
		}
	}

	public static void printMatrix(List<String> types) throws IOException {
		System.out.println("--- TYPE OVERLAP MATRIX: ---");

		int[][] table = new int[types.size()][types.size()];

		// headers
		System.out.print("--");
		for (String type : types)
			System.out.print(",\t" + type);
		System.out.println();

		// rows
		for(int i = 0; i < types.size(); i++) {
			System.out.print(types.get(i));
			for (int j = 0; j < types.size(); j++) {
				System.out.print(",\t");
				if (i <= j) {
					// look up
					String type = types.get(i) + " " + types.get(j);
					System.out.print(table[i][j] = safeScrapeCount(tryHtml(gathererURL(type, null))));
				} else {
					// transpose
					System.out.print(table[i][j] = table[j][i]);
				}
			}
			System.out.println();
		}

		// sort table. output again.
		System.out.println("\nSorted:");
		// TODO: sort based on [i][j] elements, an swap types in types list when we swap rows

		// TODO: reprint
	}

	public static void printMulticlass(List<String> types) throws IOException {
		String[][] table = new String[types.size()][2];
		for (int i = 0; i < types.size(); i++) {
			String type = types.get(i);
			System.out.print((table[i][0] = type) + ",\t");
			System.out.println(table[i][1] = "" + safeScrapeCount(tryHtml(multiclassGathererURL(type))));
		}
	}

    public static void main(String[] args) throws IOException {
		// load types from file
        List<String> types;
		try (InputStream in = new FileInputStream("creature-types-filtered-even-more.txt")) {
			types = readAllLines(in);
		}

		// run report(s)
		printMulticlass(types);
    }
}