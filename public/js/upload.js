function addStatus(text) {
  const status = document.getElementById('status');
  const li = document.createElement('li');
  li.textContent = text;
  status.appendChild(li);
}

var intervalId;

function addStatusCheck(id) {
  intervalId = setInterval(function() {
    checkTranscodeStatus(id)
  }, 5000);
}

function checkTranscodeStatus(id) {
  const xhr = new XMLHttpRequest();
  xhr.open('GET', `/api/get-job-status?id=${id}`);
  xhr.onreadystatechange = () => {
    if (xhr.readyState === 4) {
      if (xhr.status === 200) {
        const response = JSON.parse(xhr.responseText);
        addStatus('Transcode: ' + response.status);
        if (response.status === 'Complete') {
          clearInterval(intervalId);
          document.getElementById('spinner').style = "display: none;";
        }
      } else {
        console.erro('Could not check transcode status');
      }
    }
  };
  xhr.send();
}

function startTranscode(filename) {
  addStatus('Transcode started...');

  const xhr = new XMLHttpRequest();
  xhr.open('GET', `/api/create-job?file-name=${filename}`);
  xhr.onreadystatechange = () => {
    if (xhr.readyState === 4) {
      if (xhr.status === 200) {
        const response = JSON.parse(xhr.responseText);
        addStatus('Transcode in progress');
        addStatusCheck(response.id);
      } else {
        alert('Could not transcode video.');
      }
    }
  };
  xhr.send();
}

function uploadFile(transcode, file, signedRequest, filename) {
  document.getElementById('spinner').style = "display: inline;";
  addStatus('Upload started...');

  const xhr = new XMLHttpRequest();
  xhr.open('PUT', signedRequest);
  xhr.onreadystatechange = () => {
    if (xhr.readyState === 4) {
      if (xhr.status === 200) {
        addStatus('Upload done.');
        transcode(filename);
      } else {
        alert('Could not upload file.');
      }
    }
  };
  xhr.send(file);
}

function getSignedRequest(transcode, file, title, date) {
  const xhr = new XMLHttpRequest();
  xhr.open('GET', `/api/sign-s3?file-name=${file.name}&file-type=${file.type}&title=${title}&date=${date}`);
  xhr.onreadystatechange = () => {
    if (xhr.readyState === 4) {
      if (xhr.status === 200) {
        const response = JSON.parse(xhr.responseText);
        uploadFile(transcode, file, response.signedRequest, response.filename);
      } else {
        alert('Could not get signed URL.');
      }
    }
  };
  xhr.send();
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function dateValid(str) {
  return /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/.test(str);
}

function isVideo(type) {
  return /^video\//.test(type);
}

function startUpload(transcode) {
  const title = document.getElementById('title').value;
  const date = document.getElementById('date').value;
  const files = document.getElementById('file-input').files;
  const file = files[0];

  if (file == null) {
    return alert('No file selected.');
  }
  if (!isVideo(file.type)) {
    return alert('Not a video file.');
  }
  if (isBlank(title)) {
    return alert('Title is missing.');
  }
  if (isBlank(date)) {
    return alert('Date is missing.');
  }
  if (!dateValid(date)) {
    return alert('Invalid date format. Expecting YYYY-MM-DD');
  }

  getSignedRequest(transcode, file, title, date);
}

function initialize() {
  document.getElementById("upload-button").onclick = (e) => {
    e.preventDefault();
    startUpload(startTranscode);
  };
}
