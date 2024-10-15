import React, { useEffect, useState } from "react";
import VideoUpload from "./components/VideoUpload";
import { Toaster } from "react-hot-toast";
import "../src/App.css";
import axios from "axios";
function App() {
  const [videoID, setvideoID] = useState(
    "3edaf929-0b82-47f8-be8f-e1b1d081ebcb"
  );
  const [video, setvideo] = useState(null);
  const [isVideoUploadToggle, setIsVideoUploadToggle] = useState(false);

  async function getvideo() {
    const response = await axios
      .get("http://localhost:8010/api/v1/videos/getAllVideos")
      .then((response) => {
        setvideo(response.data);
      });
  }
  useEffect(() => {
    getvideo();
  }, [isVideoUploadToggle]);

  return (
    <div>
      <Toaster position="top-center" reverseOrder={false} />
      <div className="container mt-3">
        <p className="text-center text-secondary fs-2 fw-bold">
          Video Streaming Application
        </p>
      </div>
      <div className="d-flex justify-content-around">
        <div>
          <h6 className="text-light text-center">Playing Video</h6>
          <video
            src={`http://localhost:8010/api/v1/videos/stream/range/${videoID}`}
            controls
            height={400}
            width={600}
          ></video>
          <div>
            <select
              className="form-select text-white border-0 my-2"
              name="video"
              id="videoPlay"
              style={{ backgroundColor: "rgba(255, 255, 255, 0.1)" }}
              onChange={(event) => {
                setvideoID(event.target.value);
              }}
            >
              <option value="#" className="text-dark">
                Select
              </option>
              {video != null
                ? video.map((v, i) => (
                    <option
                      className="text-dark"
                      key={v.videoId}
                      value={v.videoId}
                    >
                      {v.title}
                    </option>
                  ))
                : ""}
              ;
            </select>
          </div>
        </div>

        <VideoUpload
          isVideoUploadToggle={isVideoUploadToggle}
          setIsVideoUploadToggle={setIsVideoUploadToggle}
        />
      </div>
    </div>
  );
}

export default App;
