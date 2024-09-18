package xyz.lawlietbot.spring;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;
import jakarta.servlet.http.HttpServletResponse;
import xyz.lawlietbot.spring.frontend.views.PageNotFoundView;

public class CustomRouteNotFoundError extends RouteNotFoundError {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        event.rerouteTo(PageNotFoundView.class);
        return HttpServletResponse.SC_ACCEPTED;
    }

}