package com.clarolab.helper;

import com.clarolab.dto.UserDTO;
import com.clarolab.model.User;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.UserServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestLoggedUser {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceDTO userServiceDTO;

    private User user;

    public User getUser() {
        if (user == null) {
            setUser();
        }
        return user;
    }

    public void setUser() {
        provider.setName("LoggedUser");
        provider.setUser(null);
        user = provider.getUser();
    }

    public User findUser() {
        return userService.find(getUser().getId());
    }

    public UserDTO findUserDTO() {
        return userServiceDTO.convertToDTO(userService.find(getUser().getId()));
    }

    public void setUser(User aUser) {
        user = aUser;
    }

    public UserDTO getUserDTO() {
        return userServiceDTO.convertToDTO(getUser());
    }
}
