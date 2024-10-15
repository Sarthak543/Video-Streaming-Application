package com.stream.app.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.stream.app.entities.Video;

public interface VideoService {

	Video save(Video video, MultipartFile file);

	Video get(String videoID);

	List<Video> getAll();

}
