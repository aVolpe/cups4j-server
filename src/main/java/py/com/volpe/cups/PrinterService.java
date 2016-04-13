package py.com.volpe.cups;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.cups4j.WhichJobsEnum;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Path("printer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrinterService {

	private CupsHelper helper = new CupsHelper();

	public static final String DEFAULT_USER = "cupsupser";

	@GET
	public List<Printer> getPrinters() {
		return helper.getPrinters().stream().map(Printer::new).collect(Collectors.toList());
	}

	@GET
	@Path("/{name}")
	public Printer getPrinter(@PathParam("name") String name) {
		return new Printer(helper.getCupsPrinter(name));
	}

	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String addPrinter(MultipartFormDataInput dataInput) {

		try (InputStream data = Util.getPartAsInputStream(dataInput, "data");
				InputStream driver = Util.getPartAsInputStream(dataInput, "file");) {

			Printer printer = null;
			printer = new ObjectMapper().readValue(data, Printer.class);

			return helper.addPrinter(printer, driver);

		} catch (IOException e1) {
			throw Util.throwException("Can't read the printer body ", e1.getMessage());
		}

	}

	@DELETE
	@Path("/{name}")
	public String removePrinter(@PathParam("name") String name) {

		helper.removePrinter(name);
		return "ok";

	}

	@POST
	@Path("/{name}/print")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public PrintResult printDocument(@PathParam("name") String name, MultipartFormDataInput image,
			@Context UriInfo info, @QueryParam("name") String jobName, @QueryParam("user") String user) {

		if (user == null)
			user = DEFAULT_USER;
		if (jobName == null)
			jobName = String.format("%s - %s", new Date().toString(), user);

		try (InputStream file = Util.getPartAsInputStream(image, "file")) {

			if (file == null)
				throw Util.throwException("File not Found");

			CupsPrinter printer = helper.getCupsPrinter(name);

			PrintJob toRet = new PrintJob.Builder(file).userName(user).jobName(name).build();
			return new PrintResult(printer.print(toRet));

		} catch (Exception io) {
			if (io instanceof WebApplicationException)
				throw (WebApplicationException) io;
			io.printStackTrace();
			throw Util.throwException("Can't print the document. ", io.getMessage());
		}

	}

	@GET
	@Path("/{name}/jobs")
	public List<Job> getJobs(@PathParam("name") String printerName, @QueryParam("user") String user) {
		if (user == null)
			user = DEFAULT_USER;
		try {
			return helper.client().getJobs(helper.getCupsPrinter(printerName), WhichJobsEnum.ALL, user, true).stream()
					.map(Job::new).collect(Collectors.toList());
		} catch (Exception e) {
			throw Util.throwException("Can't get jobs", e.getMessage());
		}
	}

	@Data
	public static class Job {

		String state;
		int size;
		int pagesPrinted;
		String user;
		String name;
		int id;
		String created;
		String finished;

		public Job(PrintJobAttributes at) {
			name = at.getJobName();
			id = at.getJobID();
			created = at.getJobCreateTime().toString();
			finished = at.getJobCompleteTime().toString();
			user = at.getUserName();
			pagesPrinted = at.getPagesPrinted();
			size = at.getSize();
			state = at.getJobState().toString();

		}
	}

	@Data
	@NoArgsConstructor
	public static class Printer {
		String name;
		String location;
		String description;
		String url;

		public Printer(CupsPrinter service) {

			this.name = service.getName();
			this.location = service.getLocation();
			this.description = service.getDescription();
			this.url = service.getPrinterURL().toString();
		}

	}

	@Data
	public static class PrintResult {

		int jobId;
		String message;

		public PrintResult(PrintRequestResult result) {
			this.jobId = result.getJobId();
			this.message = result.getResultDescription();
		}

	}

}
