package orz.yanagin.stragengine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {

	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
