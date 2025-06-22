package com.example.backendproject.stompwebsocket.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class GPTService {
    //json 문자열 <-> 자바객체, json 객체
    private final ObjectMapper mapper = new ObjectMapper();

    //@Value("${openai.api-key}")
    private String openaiApiKey;

    public String getMessage(String message) throws Exception {
        try {
            /*API 호출을 위한 본문 작성*/
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o"); //사용할 GPT 모델
            requestBody.put("input", message);  //사용자가 입력한 질문

            //http 요청 작성
            HttpRequest request = HttpRequest.newBuilder() 
                    .uri(URI.create("https://api.openai.com/v1/responses")) //OpenAI API
                    //API 키를 Authorization 헤더에 담아 전달
                    .header("A", "")
                    //Content-Type 을 json으로 지정
                    .header("Content-Type", "application/json")
                    //JSON으로 직렬화된 requestBody를 POST로 전송
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))  //본문 삽입
                    .build();

            /*요청 전송 및 응답*/
            //HttpClient : 요청 실행
            HttpClient client = HttpClient.newHttpClient();
            //HttpResponse<String> : 응답을 문자열(JSON 형식)로 받음
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //문자열 JSON을 JsonNode로 파싱 : JSON구조에 직접 접근해서 원하는 값을 추출하기 위해
            JsonNode jsonNode = mapper.readTree(response.body());
            System.out.println("get 응답 : " + jsonNode);

            //메시지 부분만 추출하여 반환(응답형태가 Json인데 json문자열 복잡함 -> 거기서 지피티의 대답만 추출)
            String gptMessageResponse = jsonNode.get("output").get(0).get("content").get(0).get("text").asText();
            return gptMessageResponse;

            //응답이 오지않으면 예외처리
        } catch (Exception e) {
            return "❗문제가 발생했습니다. 다시 시도해주세요.";
        }
    }
}
