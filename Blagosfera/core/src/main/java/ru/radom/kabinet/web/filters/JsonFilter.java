package ru.radom.kabinet.web.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.radom.kabinet.dto.ErrorResponseDto;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.thread.ThreadParameters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр со стандартной обработкой ошибок для json запросов
 * Created by vgusev on 24.09.2015.
 */
public class JsonFilter extends OncePerRequestFilter {



    private ObjectMapper objectMapper;

    public JsonFilter() {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            String message = ThreadParameters.getParameter(ExceptionUtils.EXCEPTION_MESSAGE);
            if (message == null) {
                message = e.getCause() != null && e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getMessage();
            }
            ErrorResponseDto responseDto = new ErrorResponseDto(message);
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        }
    }
}
