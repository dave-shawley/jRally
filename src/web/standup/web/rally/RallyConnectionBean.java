package standup.web.rally;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import standup.connector.rally.ServerConnection;


@ManagedBean
@SessionScoped
public class RallyConnectionBean
	extends ServerConnection
	implements Serializable
{
	private static final long serialVersionUID = 3954479674129797725L;
	private static final Logger logger = Logger.getLogger(RallyConnectionBean.class);

	@Override
	public String toString() {
		return String.format("%s<server:%s, user:%s, passwd:%s>",
				this.getClass().getName(), getServerName(),
				getUsername(), getPassword());
	}

	public void validateString(FacesContext fc, UIComponent ui, Object objVal) {
		String s = (String) objVal;
		if (s == null || s.trim().isEmpty()) {
			((UIInput)ui).setValid(false);
			fc.addMessage(ui.getClientId(fc),
					new FacesMessage("required to be a non-empty string"));
		}
	}

	public String loginAction() {
		Exception caughtExc = null;
		try {
			if (this.login()) {
				return "success";
			}
		} catch (Exception e) {
			logger.error("login attempt failed: "+e.getMessage(), e);
			caughtExc = e;
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		FacesMessage msg = new FacesMessage();
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		msg.setSummary("Login failed");
		if (caughtExc != null) {
			StringWriter sw = new StringWriter();
			caughtExc.printStackTrace(new PrintWriter(sw));
			msg.setDetail(sw.toString());
		}
		fc.addMessage(null, msg);

		return "failure";
	}

}
