package orz.yanagin.stragengine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RemoveAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Datastore.remove(request.getRequestURI());
	}

}
