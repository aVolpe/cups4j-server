package py.com.volpe.cups;

import java.util.List;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;

public class CupsHelper {

	private CupsClient client;

	public CupsClient client() {

		if (client == null) {
			try {
				client = new CupsClient();
			} catch (Exception e) {
				throw new RuntimeException("Error creating cups cliente", e);
			}
		}
		return client;
	}

	public List<CupsPrinter> getPrinters() {

		try {
			return client().getPrinters();
		} catch (Exception e) {
			throw new RuntimeException("Error getting printers", e);
		}
	}

	public CupsPrinter getCupsPrinter(String name) {
		if (name == null || name.isEmpty())
			throw Util.throwException("Printer not found");

		return getPrinters().stream().filter(e -> e.getName().equals(name)).findFirst()
				.orElseThrow(() -> Util.throwException("Printer not found"));
	}

}
