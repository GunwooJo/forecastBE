package site.gunwoo.forecastBE.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.dto.UserDTO;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private ObjectMapper mapper = new ObjectMapper();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            log.debug("preflight은 통과시킴");

            return true;
        }
        UserDTO loggedInUser = (UserDTO) request.getSession().getAttribute("loggedInUser");
        if (loggedInUser == null) {

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            ResponseDTO responseDTO = new ResponseDTO("로그인이 필요합니다.", null);
            String jsonResponse = mapper.writeValueAsString(responseDTO);
            response.getWriter().write(jsonResponse);
            return false;
        }
        return true;
    }
}
