package orz.yanagin.stragengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadAction implements Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Data data = null;
		try {
			data = getData(request);
		} catch (FileUploadException e) {
		}
		if (data == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Datastore.put(data);

		response.setStatus(HttpServletResponse.SC_CREATED);
	}

	Data getData(HttpServletRequest request) throws FileUploadException, IOException {
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter = upload.getItemIterator(request);
		while (iter.hasNext()) {
	        FileItemStream item = iter.next();
	        InputStream in = null;
	        try {
				in = item.openStream();
				if (!item.isFormField()) {
				    ByteArrayOutputStream out = null;
				    try {
				    	out = new ByteArrayOutputStream();
				    	int i = 0;
				        while ((i = in.read()) != -1) {
				            out.write(i);
				        }

				        return new Data(
				        		request.getRequestURI(),
				        		item.getName(),
				        		item.getContentType(),
				        		out.toByteArray());
				    } finally {
				        if (out != null) { out.close(); }
				    }
				}
			} catch (Exception e) {
				if (in != null) {
					in.close();
				}
			}
	    }

		return null;
	}

}
