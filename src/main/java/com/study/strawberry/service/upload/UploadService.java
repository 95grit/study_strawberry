package com.study.strawberry.service.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.study.strawberry.dao.upload.UploadMapper;
import com.study.strawberry.dto.upload.UploadDTO;

@Service
public class UploadService {
	
	@Autowired
    private SqlSession sqlSession;
	
	@Autowired
	ServletContext c;
	
	private String path;
	private String url = "/resources/upload/";

	public UploadDTO fileUpload(MultipartFile file) {
		UploadDTO result = fileCheck(new UploadDTO(file));
		
		if(!result.isError()) {
			result = saveFile(result, file);
			if(!result.isError())
				result = insertFile(result);
		}
		
		return result;
	}
	
	public UploadDTO fileCheck(UploadDTO dto) {
		this.path = c.getRealPath("resources\\upload") + "\\";
		// this.path = 프로젝트경로
		
		String [] checkTypes = {"image/jpeg", "image/png", "image/bmp"};
		Long checkSize = 419430400L; // 400MB
		
		for(String type : checkTypes) {
			if(dto.getOrignType().contentEquals(type)) {
				if(dto.getOrignSize() <= checkSize) {
					dto.setError(false);
					return dto;
				} else {
					dto.setError(true);
					dto.setMessage("400MB 이하의 파일만 업로드 가능합니다.");
					break;
				}
			}
			dto.setError(true);
			dto.setMessage("*.jpeg, *.pgn, *.bmp 파일만 업로드 가능합니다.");
		}
		
		return dto;
	}
	
	public UploadDTO saveFile(UploadDTO dto, MultipartFile file) {
		UploadMapper mapper = sqlSession.getMapper(UploadMapper.class);
		
		while(true) {
			dto.setSaveName(UUID.randomUUID().toString().substring(0,4) + "_" + dto.getOrignName());
			if(mapper.overlap(dto.getSaveName()) == 0)
				break;
		}
		
		File folder = new File(path);
		if(!folder.exists()) {
			try {
				System.out.println(folder.mkdir());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		dto.setSavePath(path);
		dto.setSaveURL(url + dto.getSaveName());
		
		try {
			byte[] data = file.getBytes();
			FileOutputStream fos = new FileOutputStream(path + dto.getSaveName());
			
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			dto.setError(true);
			dto.setMessage("이미지 파일 저장에 실패하였습니다.");
			e.printStackTrace();
		}
			
		return dto;
	}
	
	public void delFile(UploadDTO dto) {
		new File(path + dto.getSaveName()).delete();
	}
	
	public UploadDTO insertFile(UploadDTO dto) {
		UploadMapper mapper = sqlSession.getMapper(UploadMapper.class);
		
		if(mapper.insertInfo(dto) != 1) {
			dto.setError(true);
			dto.setMessage("데이터 서버에 이미지 정보를 저장하지 못하였습니다.");
			delFile(dto);
		}
		
		return dto;
	}
}
