package com.nextArt.community.provider;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nextArt.community.DTO.AccessTokenDTO;
import com.nextArt.community.DTO.GitHubUserDTO;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class GitHubProvider {
	//通过OKhttp发送请求到https://github.com/login/oauth/access_token，获取到一个token
	public String getAccessToken(AccessTokenDTO accessTokenDTO) {
		MediaType mediaType = MediaType.get("application/json; charset=utf-8");
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
	    Request request = new Request.Builder().url("https://github.com/login/oauth/access_token").post(body).addHeader("Accept", "application/json").build();
		try (Response response = client.newCall(request).execute()) {
			String str =  response.body().string();
			JSONObject  jsonObject = JSONObject.parseObject(str);
			String accessTokenStr = (String) jsonObject.get("access_token");
			return accessTokenStr;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//获取到token后，将token发送请求到https://api.github.com/user获取到用户信息
	public GitHubUserDTO getUserDTO(String accessToken) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
			      .url("https://api.github.com/user")
				  .header("Authorization","token "+accessToken)
			      .build();
		try {
			Response response = client.newCall(request).execute();
			String str = response.body().string();
			GitHubUserDTO gitHubUserDTO = JSON.parseObject(str,GitHubUserDTO.class);
			return gitHubUserDTO;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
