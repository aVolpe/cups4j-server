package py.com.volpe.cups;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * 
 * @author Arturo Volpe
 *
 */
public class RestApplication extends Application {

	private Set<Object> singletons = new HashSet<>();

	public RestApplication() {
		singletons.add(PrinterService.class);
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
