import React, { useState } from "react";
import toast from "react-hot-toast";
import axios from "axios";
import ProgressBar from "@ramonak/react-progress-bar";

export default function VideoUpload({
  isVideoUploadToggle,
  setIsVideoUploadToggle,
}) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [progress, setProgress] = useState(0);
  const [uploading, setUploading] = useState(false);
  const [meta, setMeta] = useState({
    title: "",
    description: "",
  });

  function handleFileChange(event) {
    setSelectedFile(event.target.files[0]);
  }

  function handleForm(formEvent) {
    formEvent.preventDefault();
    if (!selectedFile) {
      alert("Select File");
    } else if (meta.title === "") {
      alert("Please provide title");
    } else if (meta.description === "") {
      alert("Please provide title");
    } else {
      saveVideoToServer();
    }
  }

  function formFieldChange(event) {
    setMeta({ ...meta, [event.target.name]: event.target.value });
  }

  async function saveVideoToServer() {
    setUploading(true);
    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("title", meta.title);
    formData.append("description", meta.description);
    try {
      const response = await axios.post(
        "http://localhost:8010/api/v1/videos",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            setProgress(percentCompleted);
            setIsVideoUploadToggle(isVideoUploadToggle == true ? false : true);
          },
        }
      );

      setUploading(false);
      toast.success("File Uploaded");

      ///////////////////////
    } catch (error) {
      console.error("Upload failed:", error);
      setUploading(false);
      toast.error("Error in uploading file");
      console.log(error);
    }
    resetForm();
  }

  function resetForm() {
    setMeta({
      title: "",
      description: "",
    });
    document.getElementById("fileUpload").value = "";
    setSelectedFile(null);
    setUploading(false);
    setProgress(0);
    console.clear();
    console.log(meta);
  }

  return (
    <>
      <form onSubmit={handleForm}>
        <div className="container card bg-dark pb-3 ">
          <div className="card-body">
            <h6 className="card-title text-center text-light fs-6 fw-bold ">
              Video Upload
            </h6>
            <div className="d-flex flex-column">
              <div className="mb-3">
                <label
                  htmlFor="videoTitle"
                  className="form-label text-light mt-3"
                >
                  Video Title
                </label>
                <input
                  type="text"
                  name="title"
                  value={meta.title}
                  className="form-control text-white border-0"
                  id="videoTitle"
                  onChange={formFieldChange}
                  style={{ backgroundColor: "rgba(255, 255, 255, 0.1)" }}
                />
              </div>
              <div className="mb-3">
                <label
                  htmlFor="videoDescription"
                  className="form-label text-light"
                >
                  Video Description
                </label>
                <textarea
                  type="text"
                  name="description"
                  value={meta.description}
                  className="form-control text-white border-0"
                  id="videoDescription"
                  onChange={formFieldChange}
                  style={{ backgroundColor: "rgba(255, 255, 255, 0.1)" }}
                  rows={5}
                />
              </div>
            </div>
            <div className="d-flex">
              <img src="../public/upload.png" alt="#" className="m-2" />
              <div className="input-group m-3">
                <input
                  type="file"
                  className="form-control border-0 text-light"
                  id="fileUpload"
                  name="video"
                  onChange={handleFileChange}
                  style={{ backgroundColor: "rgba(255, 255, 255, 0.1)" }}
                />
              </div>
            </div>
            <div className="mt-2 mb-2" hidden={!uploading}>
              <ProgressBar
                completed={progress}
                bgColor="#32de84"
                baseBgColor="grey"
                borderRadius="5px"
                maxCompleted={100}
                customLabelStyles={{
                  margin: "0px auto",
                }}
              />
            </div>
            <div className="w-25 container mt-3">
              <button
                className="input-group-text btn btn-primary cursor-pointer"
                htmlFor="fileUpload"
                disabled={uploading}
              >
                Upload
              </button>
            </div>
          </div>
        </div>
      </form>
    </>
  );
}
