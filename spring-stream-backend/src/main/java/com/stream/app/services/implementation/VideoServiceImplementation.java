package com.stream.app.services.implementation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.entities.Video;
import com.stream.app.repository.VideoRepository;
import com.stream.app.services.VideoService;


import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.util.StringUtils;

@Service
public class VideoServiceImplementation implements VideoService {

	@Autowired
	private VideoRepository repository;

	@Value("${files.video}")
	String directory;



	@Override
	public Video save(Video video, MultipartFile file) {

		try {
			String fileName = file.getOriginalFilename();
			InputStream inputStream = file.getInputStream();

			// folder path: create
			// StringUtils.cleanPath: provide us clean path and remove unnecessary slashes
			String cleanFileName = StringUtils.cleanPath(fileName);
			String cleanFolder = StringUtils.cleanPath(directory);

			Path path = Paths.get(cleanFolder, cleanFileName);
			System.out.println(path);

			// copy file to the folder
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

			// video meta data
			video.setContentType(file.getContentType());
			video.setFilePath(path.toString());

			
//			processVideo(video.getVideoId());
//			
//			// metadata save
//			return repository.save(video);
			
			// saving the video and then making the segments
			if(repository.save(video) != null) {
				return video;
			}else {
				Files.delete(path);
				return null;
			}
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Video get(String videoID) {
		Video video = repository.findById(videoID).orElseThrow(() -> new RuntimeException("Video Not Found"));
		return video;
	}

	@Override
	public List<Video> getAll() {
		return repository.findAll();
	}



}
