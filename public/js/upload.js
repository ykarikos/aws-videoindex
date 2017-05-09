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
  }, 10000);
}

function statusReady() {
  clearInterval(intervalId);
  document.getElementById('spinner').style = "display: none;";

  const uploadNew = document.getElementById('upload-new');
  uploadNew.style = "display: inline-block;";
  uploadNew.disabled = false;
}

function checkTranscodeStatus(id) {
  fetch(`/api/get-job-status?id=${id}`, { credentials: 'same-origin' })
  .then(response => response.json())
  .then(response => {
    addStatus('Transcode: ' + response.status);
    if (response.status === 'Complete' || response.status === 'Error') {
      statusReady();
    }
  }).catch(err => {
    console.error('Could not check transcode status: ', err);
  });
}

function startTranscode(filename) {
  addStatus('Transcode started...');

  fetch(`/api/create-job?file-name=${filename}`, { credentials: 'same-origin' })
  .then(response => response.json())
  .then(response => {
    addStatus('Transcode in progress');
    addStatusCheck(response.id);
  }).catch(err => {
    alert('Could not transcode video.');
    console.error(err);
  });
}

function uploadFile(file, signedRequest, filename) {
  document.getElementById('spinner').style = "display: inline;";
  addStatus('Upload started...');

  fetch(signedRequest, {
    method: 'PUT',
    body: file
  }).then(response => {
    if (response.ok) {
      addStatus('Upload done.');
      startTranscode(filename);
    } else {
      alert('Could not upload file.');
    }
  }).catch(err => {
    alert('Could not upload file.');
    console.error(err);
  });
}

function getSignedRequest(file, title, date) {
  const url = `/api/sign-s3?file-name=${file.name}&file-type=${file.type}&title=${title}&date=${date}`;

  fetch(url, { credentials: 'same-origin' })
  .then(response => response.json())
  .then(response => uploadFile(file, response.signedRequest, response.filename))
  .catch(err => {
    alert('Could not get signed URL.');
    console.error(err);
  });
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function dateValid(str) {
  return /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/.test(str);
}

function isVideo(file) {
  return /^video\//.test(file.type) || /\.mts$/i.test(file.name);
}

function disableFormElements() {
  for (let e of document.querySelectorAll('#upload-form input')) {
    e.disabled = true;
  }
}

function startUpload() {
  const title = document.getElementById('title').value;
  const date = document.getElementById('date').value;
  const files = document.getElementById('file-input').files;
  const file = files[0];

  if (file == null) {
    return alert('No file selected.');
  }
  if (!isVideo(file)) {
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

  disableFormElements();
  getSignedRequest(file, title, date);
}

function initialize() {
  document.getElementById("upload-button").onclick = (e) => {
    e.preventDefault();
    startUpload();
  };
}
