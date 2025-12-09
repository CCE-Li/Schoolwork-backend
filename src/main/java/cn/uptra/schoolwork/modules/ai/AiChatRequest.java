package cn.uptra.schoolwork.modules.ai;

import lombok.Data;

@Data
public class AiChatRequest {

    private String prompt;

    private String model;
}
