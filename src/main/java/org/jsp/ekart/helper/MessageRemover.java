package org.jsp.ekart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class MessageRemover {
	public void remove() {
		ServletRequestAttributes servletAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpSession session = servletAttributes.getRequest().getSession();
		session.removeAttribute("success");
		session.removeAttribute("failure");
	}
}
