package ru.radom.kabinet.web.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.radom.kabinet.utils.thread.ThreadParameters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TimeFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(TimeFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

		try {
			ThreadParameters.init();
			request.setAttribute("start", start);
			filterChain.doFilter(request, response);
		} finally {
            long end = System.currentTimeMillis();
            long duration = end - start;
            logger.info("request to " + request.getRequestURI() + " processed in " + duration + " milliseconds");

			ThreadParameters.clear();
		}
	}
}
