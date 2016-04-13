package py.com.volpe.cups;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class Util {

	public static WebApplicationException throwException(String message) {
		return new WebApplicationException(
				Response.status(Status.BAD_REQUEST).entity(Collections.singletonMap("message", message)).build());
	}

	/**
	 * Return the input stream of the parth with the name specified.
	 * 
	 * @return the inputStream, or null if this parameters existsx, but has no
	 *         data.
	 * @throws IllegalArgumentException
	 *             if the form exists, but there is not a attribute with the
	 *             name 'name'
	 */
	public static InputStream getPart(MultipartFormDataInput dataInput, String name) {

		InputStream file = null;
		List<InputPart> parts = dataInput.getFormDataMap().get(name);
		if (parts == null)
			throw Util.throwException("File not found");

		for (InputPart inputPart : dataInput.getFormDataMap().get(name)) {
			try {
				file = inputPart.getBody(InputStream.class, null);
			} catch (IOException e) {
			}
			break;
		}
		if (file == null) {
			throw Util.throwException("File not found");
		}
		return file;
	}
}
