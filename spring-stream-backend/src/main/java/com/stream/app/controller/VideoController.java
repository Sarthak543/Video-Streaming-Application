package com.stream.app.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.AppConstants;
import com.stream.app.entities.Video;
import com.stream.app.payload.CustomMessage;
import com.stream.app.services.VideoService;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("http://localhost:5173/")
public class VideoController {

	@Autowired
	private VideoService service;
	@Value("${file.video.hsl}")
	private String HSL_DIR;
	@PostMapping
	public ResponseEntity<?> create(@RequestParam("file") MultipartFile file, @RequestParam("title") String title,
			@RequestParam("description") String description) {
		Video v = new Video();
		v.setTitle(title);
		v.setDescription(description);
		v.setVideoId(UUID.randomUUID().toString());
		Video savedVideo = service.save(v, file);
		if (savedVideo != null) {
			return ResponseEntity.status(HttpStatus.OK).body(savedVideo);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new CustomMessage("Video not uploaded", false));
		}
	}

	@GetMapping("/stream/{videoID}")
	public ResponseEntity<Resource> stream(@PathVariable String videoID) {
		Video video = service.get(videoID);
		String contentType = video.getContentType();
		String path = video.getFilePath();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		Resource resource = new FileSystemResource(path);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	}

	@GetMapping("/getAllVideos")
	public List<Video> getAll() {
		return service.getAll();
	}

	// stream video in chunks
	@GetMapping("/stream/range/{videoID}")
	public ResponseEntity<Resource> streamVideoRange(@PathVariable String videoID,
			@RequestHeader(value = "Range", required = false) String range) {
		Video video = service.get(videoID);
		Path path = Paths.get(video.getFilePath());
		Resource resource = new FileSystemResource(path);
		String contentType = video.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		long fileLength = path.toFile().length();
		if (range == null) {
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		}

		// calculating start and end range
		long rangeStart = 0, rangeEnd = 0;

		String[] ranges = range.replace("bytes=", "").split("-");
		rangeStart = Long.parseLong(ranges[0]);

		rangeEnd = rangeStart + AppConstants.CHUNK_SIZE - 1;
		if (rangeEnd >= fileLength) {
			rangeEnd = fileLength - 1;
		}

//		 calculating how much data we have to send
		System.out.println(rangeStart + "  " + rangeEnd);
		InputStream inputStream;
		try {
			inputStream = Files.newInputStream(path);
			inputStream.skip(rangeStart);
			long contentLength = rangeEnd - rangeStart + 1;
			
			
			byte[] data = new byte[(int)contentLength];
			inputStream.read(data,0,data.length);

			HttpHeaders header = new HttpHeaders();
			header.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
			header.setContentLength(contentLength);
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			header.add("Pragma", "np-cache");
			header.add("Expires", "0");
			header.add("X-Content-Type-Options", "nosniff");
			System.out.println("asdasd");
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(header)
					.contentType(MediaType.parseMediaType(contentType)).body(new ByteArrayResource(data));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}
}
