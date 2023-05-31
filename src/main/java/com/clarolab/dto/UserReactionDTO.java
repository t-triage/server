package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReactionDTO extends BaseDTO {
    private UserDTO user;
    private GuideDTO guide;

    private String answer;
    private int answerType;

}
