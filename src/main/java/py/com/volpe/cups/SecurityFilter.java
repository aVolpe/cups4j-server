package py.com.volpe.cups;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.FileUtils;

@Provider
@PreMatching
public class SecurityFilter implements ContainerRequestFilter {

	private static String credentials;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String header = requestContext.getHeaderString("Authorization");

		if (header == null || header.trim().isEmpty())
			throw Util.throwWrongUser();

		String[] parts = header.split(" ", 2);
		if (parts.length < 2)
			throw Util.throwWrongUser();

		String userPass = new String(Base64.getDecoder().decode(parts[1]));
		String[] userPassArray = userPass.split(":", 2);
		if (userPassArray.length < 2)
			throw Util.throwWrongUser();

		String user = userPassArray[0];
		String pass = userPassArray[1];
		checkLogin(user, pass);
	}

	protected void checkLogin(String user, String passwrod) {

		if (credentials == null)
			try {
				credentials = FileUtils.readFileToString(new File("/tomcat.credentials")).trim();
			} catch (IOException e) {
				throw Util.throwException("Can't check passwords");
			}

		String query = user + ":" + passwrod;

		if (!query.equals(credentials))
			throw Util.throwWrongUser();
	}

}
