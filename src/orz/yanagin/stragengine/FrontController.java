package orz.yanagin.stragengine;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.EntityNotFoundException;

public class FrontController implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		execute(
				HttpServletRequest.class.cast(request),
				HttpServletResponse.class.cast(response),
				chain);

	}

	void execute(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request.getRequestURI().startsWith("/_ah/admin")) {
			chain.doFilter(request, response);
			return;
		}

		String requestUri = request.getRequestURI();
		request.setAttribute("requestUri", requestUri);

		if ("form".equals(request.getQueryString())) {
			forwardUpload(request, response);
			return;
		}

		try {
			if ("remove".equals(request.getQueryString())) {
				new RemoveAction().execute(request, response);
				forwardUpload(request, response);
				return;
			}

			if ("post".equals(request.getMethod().toLowerCase())) {
				new UploadAction().execute(request, response);
				return;
			}

			try {
				new DownloadAction().execute(request, response);
				return;
			} catch (EntityNotFoundException e) {
				forwardUpload(request, response);
				return;
			}
		} catch (Exception e) {
			HttpServletResponse.class.cast(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	void forwardUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/upload.jsp").forward(request, response);
	}

	@Override
	public void destroy() {
	}

}
