package com.study.strawberry.dao.upload;

import com.study.strawberry.dto.upload.UploadDTO;

public interface UploadMapper {
	public int overlap(String saveName);
	public int insertInfo(UploadDTO dto);
}
