# AWS Videoindex

> How to share videos of the kids so that the close relatives can browse them easily but they will not be available in the public internet, e.g. in Youtube?

A web application for storing and viewing videos in [AWS S3](https://aws.amazon.com/s3/). It uses [AWS Elastic Transcoder](https://aws.amazon.com/elastictranscoder/) to transcode videos in HTML5 compatible video formats: MP4 and Webm.

This project is based on the [AWS S3 Direct Upload Example](https://github.com/ykarikos/aws-s3-direct-upload-example).

## AWS Setup

### Access control

1. Create a user in [AWS IAM Management Console](https://console.aws.amazon.com/iam/home).
2. Create a group with two *Managed Policies*: *AmazonS3FullAccess* and *AmazonElasticTranscoderJobsSubmitter*
3. Assign the group to the user
4. Save the Access key ID and Secret Access Key. They will be configured in `AWS_ACCESS_KEY_ID`
and `AWS_SECRET_ACCESS_KEY`

### AWS S3

You need to set up two AWS S3 buckets: one for `AWS_S3_BUCKET_UPLOAD` and one for `AWS_S3_BUCKET_TARGET`. See [Heroku documentation](https://devcenter.heroku.com/articles/s3-upload-node#initial-setup) for the initial setup of the upload bucket and its permissions.

### AWS Elastic Transcoder

1. Setup a Pipeline in [AWS Elastic Transcoder Console](https://eu-west-1.console.aws.amazon.com/elastictranscoder/home).
 - Use the previously created `AWS_S3_BUCKET_UPLOAD` as the *Input Bucket*.
 - Use the previously created `AWS_S3_BUCKET_TARGET` for both *Transcoded Files and Playlists* and *Thumbnails*
 - Save the *Pipeline ID*. It will be configured in `AWS_TRANSCODER_PIPELINE`
2. Create two presets, one for MP4 and one for Webm format:
 - Copy *System preset: Web*, set *jpg* as the thumbnail format and select suitable output resolutions
 - Copy *System preset: Webm VP9 720p* and select suitable output resolutions.
 - Save the *Preset IDs*. They will be configured in `AWS_TRANSCODER_MP4` and `AWS_TRANSCODER_WEBM`.


## Environment

Set the following environment variables:

* `AWS_REGION`
* `AWS_ACCESS_KEY_ID`
* `AWS_SECRET_ACCESS_KEY`
* `AWS_S3_BUCKET_UPLOAD`
  AWS S3 Bucket for file uploads
* `AWS_S3_BUCKET_TARGET`
  AWS S3 Bucket for transcoded videos
* `AWS_TRANSCODER_PIPELINE`
  Pipeline ID in AWS Elastic Transcoder that reads from `AWS_S3_BUCKET_NAME_UPLOAD`
* `AWS_TRANSCODER_MP4`
  Job ID in AWS Elastic Transcoder for converting video to MP4 format and JPG thumbnails
* `AWS_TRANSCODER_WEBM`
  Job ID in AWS Elastic Transcoder for converting video to Webm format

## Running Locally for development

Make sure you have [Leiningen](https://leiningen.org/) installed.

```sh
$ git clone https://github.com/ykarikos/aws-s3-direct-upload-example.git
$ cd aws-s3-direct-upload-example
$ lein ring server-headless
```

Your app should now be running on [localhost:3000](http://localhost:3000/).

## Running on a server

```sh
$ lein ring uberjar
$ java -jar target/aws-videoindex-standalone.jar
```

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

See [Clojure on Heroku](https://devcenter.heroku.com/categories/clojure) for more information.

## Usage

- You can see all the videos listed per year in the root context.
- Use `/upload.html` to upload videos.

## License

Licensed with [Eclipse Public License v1.0](http://www.eclipse.org/legal/epl-v10.html).


## Thanks

This project is a grateful recipient of the [Futurice Open Source sponsorship program](http://futurice.com/blog/sponsoring-free-time-open-source-activities?utm_source=github&utm_medium=spice).
