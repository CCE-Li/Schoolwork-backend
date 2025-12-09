package cn.uptra.schoolwork.modules.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 简单流式聊天接口：
     * GET /api/ai/chat/stream?prompt=你好
     * 以 text/plain 分块的方式持续返回文本片段
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> streamChat(@RequestParam("prompt") String prompt,
                                   @RequestParam(value = "model", required = false) String model) {
        // String intro = "您好，我是线上图书商城的 AI 客服助手，可以为您解答图书选购、订单状态、物流进度和售后相关问题～\n";
        String intro = "";
        return Flux.just(intro).concatWith(aiService.streamChat(model, prompt));
    }

    /**
     * JSON 请求体形式的流式聊天接口：
     * POST /api/ai/chat/stream
     * {"prompt": "你好", "model": "doubao-seed-1-6-251015"}
     * 返回 text/plain 分块文本流
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> streamChatByPost(@RequestBody AiChatRequest request) {
        String prompt = request.getPrompt();
        String model = request.getModel();
        // String intro = "您好，我是线上图书商城的 AI 客服助手，可以为您解答图书选购、订单状态、物流进度和售后相关问题～\n";
        String intro = "";
        return Flux.just(intro).concatWith(aiService.streamChat(model, prompt));
    }
}
