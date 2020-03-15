package com.nextArt.community.DTO;

import lombok.Data;

@Data
public class GitHubUserDTO {
	private String name;
	private long id;
	private String bio;
	private String avatarUrl;
}
