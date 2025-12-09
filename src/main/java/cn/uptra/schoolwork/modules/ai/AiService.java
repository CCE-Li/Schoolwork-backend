package cn.uptra.schoolwork.modules.ai;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(@Value("${ark.api-key:}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    /**
     * 调用 Doubao responses 接口，只返回助手输出文本的流（过滤掉推理等其它事件）。
     */
    public Flux<String> streamChat(String model, String userContent) {
        if (model == null || model.isEmpty()) {
            model = "doubao-seed-1-6-251015";
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        // system 提示词：限定 AI 的服务范围
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        Map<String, Object> systemContent = new HashMap<>();
        systemContent.put("type", "input_text");
        systemContent.put("text", "你是一个线上图书商城的智能客服助手，主要负责解答与图书商品、图书推荐、下单流程、订单状态、支付、发货、物流、退换货及售后服务等相关的问题。对于与图书商城无关或不适宜的问题，要礼貌地说明自己无法提供该类服务，并引导用户回到与购书或订单相关的话题。");
        systemMessage.put("content", List.of(systemContent));

        // user 提示词：用户真实输入
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "input_text");
        textContent.put("text", userContent);
        userMessage.put("content", List.of(textContent));

        requestBody.put("input", List.of(systemMessage, userMessage));
        requestBody.put("stream", true);

        return webClient.post()
                .uri("/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofMinutes(5))
                // WebClient 读取到的是每一条 data 对应的一行 JSON，这里只抽取助手的输出 delta 文本
                .flatMap(line -> {
                    if (line == null || line.isEmpty()) {
                        return Flux.empty();
                    }
                    try {
                        JSONObject json = JSONUtil.parseObj(line);
                        String type = json.getStr("type", "");
                        if (!"response.output_text.delta".equals(type)) {
                            return Flux.empty();
                        }
                        String delta = json.getStr("delta");
                        if (delta == null || delta.isEmpty()) {
                            return Flux.empty();
                        }
                        return Flux.just(delta);
                    } catch (Exception e) {
                        // 如果不是标准 JSON（比如 [DONE]），直接忽略
                        return Flux.empty();
                    }
                });
    }
}
