package haedalSpring.haedal.service;

import haedalSpring.haedal.domain.User;
import haedalSpring.haedal.dto.LoginRequestDto;
import haedalSpring.haedal.dto.UserSimpleResponseDto;
import haedalSpring.haedal.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;



    @Autowired
    public AuthService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public UserSimpleResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        User user=userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() ->new IllegalAccessError("존재하지 않는 회원입니다."));
        if(!loginRequestDto.getPassword().equals(user.getPassword())){
            throw new IllegalArgumentException("잘못되 비밀번호입니다");
        }

        HttpSession session=request.getSession();
        session.setAttribute("user", user);

        return userService.convertUserToSimpleDto(user, user);

    }

    public void logout(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session!=null) {
            session.invalidate();
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null || session.getAttribute("user")==null) {
            throw new IllegalStateException("로그인되지 않았습니다.");
        }
        return (User) session.getAttribute("user");
    }
}
