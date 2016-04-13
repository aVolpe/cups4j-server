package py.com.volpe.cups;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * 
 * @author Arturo Volpe
 *
 */
@ApplicationPath("rest")
public class RestApplication extends Application {

	private Set<Object> singletons = new HashSet<>();

	public RestApplication() {
		singletons.add(new PrinterService());
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
