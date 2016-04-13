package py.com.volpe.cups;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class Util {

	public static WebApplicationException throwException(String message) {
		return throwException(message, (String) null);
	}

	public static WebApplicationException throwException(String message, Throwable exp) {

		exp.printStackTrace();
		return throwException(message, exp.getMessage());
	}

	public static WebApplicationException throwException(String message, String cause) {

		Map<String, String> entity = new HashMap<>();
		entity.put("message", message);
		if (cause != null)
			entity.put("cause", cause);
		return new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(entity).build());
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
	public static InputStream getPartAsInputStream(MultipartFormDataInput dataInput, String name) {

		return getPart(dataInput, name, InputStream.class);
	}

	public static <T> T getPart(MultipartFormDataInput dataInput, String name, Class<T> classType) {
		T file = null;
		List<InputPart> parts = dataInput.getFormDataMap().get(name);
		if (parts == null)
			return null;

		for (InputPart inputPart : dataInput.getFormDataMap().get(name)) {
			try {
				file = inputPart.getBody(classType, null);
			} catch (IOException e) {
			}
			break;
		}
		return file;
	}
}
