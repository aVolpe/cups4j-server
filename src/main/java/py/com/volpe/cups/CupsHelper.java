package py.com.volpe.cups;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;

import py.com.volpe.cups.PrinterService.Printer;

public class CupsHelper {

	private String DRIVER_PATH = "/newppds/";
	private CupsClient client;

	/**
	 * Return the default {@link CupsClient}
	 * 
	 * @return {@link CupsClient}, never <code>null</code>
	 */
	public CupsClient client() {

		if (client == null) {
			try {
				client = new CupsClient();
			} catch (Exception e) {
				throw Util.throwException("Error creating cups client", e);
			}
		}
		return client;
	}

	/**
	 * Return the list of printers, never <code>null</code>.
	 * 
	 * @return the list of printres
	 */
	public List<CupsPrinter> getPrinters() {

		try {
			return client().getPrinters();
		} catch (Exception e) {
			throw Util.throwException("Can't get printers", e);
		}
	}

	/**
	 * Return a {@link CupsPrinter} by a name, never return <code>null</code>.
	 * 
	 * @param name
	 *            the name of the printer, not allows null or empty strings
	 * @return the printer, if is not founds, throws a exception
	 */
	public CupsPrinter getCupsPrinter(String name) {
		if (name == null || name.isEmpty())
			throw Util.throwException("Printer not found");

		return getPrinters().stream().filter(e -> e.getName().equals(name)).findFirst()
				.orElseThrow(() -> Util.throwException("Printer not found"));
	}

	public String writeDriver(Printer printer, InputStream driver) {

		String name = printer.getName().replaceAll("\\s", "_") + "_driver.ppd";

		File f = new File(DRIVER_PATH + name);
		if (f.exists())
			throw Util.throwException("Driver already found", "The file exists");

		try {
			FileUtils.copyInputStreamToFile(driver, f);
		} catch (IOException e) {
			throw Util.throwException("Can't write the file ", e.getMessage());
		}

		return DRIVER_PATH + name;
	}

	/**
	 * Add a printer, using the command line, this don't work on windows
	 * 
	 * @param printer
	 *            data of the printer
	 * @param driver
	 *            data of the driver
	 * @return the name of the new printer
	 */
	public String addPrinter(Printer printer, InputStream driver) {

		String driverPath = driver == null ? null : writeDriver(printer, driver);

		execute(printer.getName(), printer.getUrl(), driverPath, printer.getLocation(), printer.getDescription());

		return printer.getName();
	}

	public void removePrinter(String name) {

		try {
			doIt(new String[] { "lpadmin", "-x", name });
		} catch (IOException | InterruptedException e) {
			throw Util.throwException("Can't remove the printer", e);

		}

	}

	private void execute(String name, String url, String ppd, String location, String description) {

		try {
			doIt(new String[] { "lpadmin", "-E", "-p", name, "-v", url, "-P", ppd, "-L", location, "-D", description });
			doIt(new String[] { "cupsaccept", name });
			doIt(new String[] { "cupsenable", name });

		} catch (IOException | InterruptedException e) {
			throw Util.throwException("Can't add the printer", e);
		}

	}

	private void doIt(String command[]) throws IOException, InterruptedException {

		Process p = Runtime.getRuntime().exec(command);
		int i = p.waitFor();
		if (i != 0) {
			StringBuilder fullCommand = new StringBuilder();
			for (String s : command)
				fullCommand.append(s);
			System.out.println(fullCommand.toString());
			throw Util.throwException("Error in the execution of the " + fullCommand.toString() + " commmand",
					"The proccess returns " + i);
		}
	}

}
