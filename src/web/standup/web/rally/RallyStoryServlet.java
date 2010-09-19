package standup.web.rally;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RallyStoryServlet extends HttpServlet {

	private static final long serialVersionUID = 4792194392589790182L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			pw.append("<html><head><title>Hello</title></head><body>")
			  .append("<table>")
			  .append("<tr><th>Domain</th><th>Path</th><th>Name</th><th>Value</th></tr>");
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie c: cookies) {
					pw.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
							c.getDomain(), c.getPath(), c.getName(), c.getValue());
				}
			}
			pw.append("</table></body></html>");
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
