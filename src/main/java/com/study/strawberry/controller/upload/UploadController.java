package com.study.strawberry.controller.upload;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.study.strawberry.dto.upload.UploadDTO;
import com.study.strawberry.service.upload.UploadService;

@Controller
public class UploadController {
	
	@Autowired
	UploadService uploadService;

	@PostMapping(value = "util/upload")
	public ModelAndView fileUpload(HttpServletRequest request, HttpServletResponse response, 
			MultipartHttpServletRequest reqFile) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, MultipartFile> fileMap =  reqFile.getFileMap();
	
		if(fileMap.containsKey("upload")) {
			MultipartFile file = fileMap.get("upload");
			UploadDTO dto = uploadService.fileUpload(file);
			
			if(!dto.isError()) {
				mav.addObject("uploaded","1");
				mav.addObject("fileName", dto.getOrignName());
				mav.addObject("url", dto.getSaveURL());
			} else {
				mav.addObject("uploaded","0");
				mav.addObject("error", dto.getErrorMessage());
			}
		} else {
			HashMap<String, String> errorMessage = new HashMap<String, String>();
			errorMessage.put("message", "이미지가 없습니다.");
			
			mav.addObject("uploaded", 0);
			mav.addObject("error", errorMessage);
		}

		return mav;
	}
}
