package orz.yanagin.stragengine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Data data = Datastore.get(request.getRequestURI());

		response.setContentType(data.getContentType());
		response.setContentLength(data.getData().length);

		if ("application/octet-stream".equals(data.getContentType().toLowerCase())) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + data.getName() + "\"");
		}

		response.getOutputStream().write(data.getData());
	}

}
