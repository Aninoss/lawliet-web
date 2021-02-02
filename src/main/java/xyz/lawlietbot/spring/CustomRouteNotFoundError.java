package xyz.lawlietbot.spring;

import xyz.lawlietbot.spring.frontend.views.PageNotFoundView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;

import javax.servlet.http.HttpServletResponse;

public class CustomRouteNotFoundError extends RouteNotFoundError {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        event.rerouteTo(PageNotFoundView.class);
        return HttpServletResponse.SC_ACCEPTED;
    }

}