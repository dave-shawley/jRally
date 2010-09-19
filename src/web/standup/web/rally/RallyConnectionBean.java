package standup.web.rally;

import java.io.Serializable;

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
		return "success";
	}

}
