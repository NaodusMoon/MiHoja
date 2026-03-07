package com.miapp.MiHoja.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class AppErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam(name = "detalle", required = false, defaultValue = "false") boolean detalle,
                              Model model) {
        WebRequest webRequest = new ServletWebRequest(request);

        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE)
                .including(ErrorAttributeOptions.Include.BINDING_ERRORS);
        if (detalle) {
            options = options.including(ErrorAttributeOptions.Include.EXCEPTION)
                    .including(ErrorAttributeOptions.Include.STACK_TRACE);
        }

        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest, options);
        Throwable throwable = errorAttributes.getError(webRequest);
        int statusCode = resolveStatusCode(request);
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        String statusText = httpStatus != null ? httpStatus.getReasonPhrase() : "Error inesperado";

        response.setStatus(statusCode);

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("statusText", statusText);
        model.addAttribute("path", attributes.getOrDefault("path", request.getRequestURI()));
        model.addAttribute("timestamp", attributes.get("timestamp"));
        model.addAttribute("errorMessage", attributes.getOrDefault("message", "Ocurrio un error no controlado."));
        model.addAttribute("exceptionName", attributes.getOrDefault("exception", throwable != null ? throwable.getClass().getName() : null));
        model.addAttribute("stackTrace", attributes.get("trace"));
        model.addAttribute("showDetails", detalle);
        model.addAttribute("mensajeError", attributes.get("message"));

        return "error";
    }

    private int resolveStatusCode(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        try {
            return Integer.parseInt(statusCode.toString());
        } catch (NumberFormatException exception) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }
}
