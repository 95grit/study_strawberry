package com.study.strawberry.dto.upload;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UploadDTO {
	// orign
	private String orignName;
	private String orignType;
	private long orignSize;
	// save
	private String saveName;
	private String savePath;
	private String saveURL;
	// error
	private boolean error;
	private HashMap<String, String> errorMessage = new HashMap<String, String>();
	
	public UploadDTO(MultipartFile file) {
		this.orignName = file.getOriginalFilename();
		this.orignType = file.getContentType();
		this.orignSize = file.getSize();
	}
	
	public void setMessage(String msg) {
		this.errorMessage.put("message", msg);
	}
	
	public String getMessage() {
		return this.errorMessage.get("message");
	}
}
