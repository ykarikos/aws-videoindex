
# AWS S3 Direct Upload Example

An example Clojure web app for uploading files directly to [AWS S3](https://aws.amazon.com/s3/). The app can easily be deployed to Heroku and also run locally. The app is based on Heroku's [clojure-getting-started](https://github.com/heroku/clojure-getting-started) app and [Direct to S3 File Uploads in Node.js](https://devcenter.heroku.com/articles/s3-upload-node) article.

## Environment

You need to set up an AWS S3 bucket. See [Heroku documentation](https://devcenter.heroku.com/articles/s3-upload-node#initial-setup) for the initial setup. Then set the following environment variables:

* `AWS_REGION`
* `AWS_ACCESS_KEY_ID`
* `AWS_SECRET_ACCESS_KEY`
* `AWS_S3_BUCKET_UPLOAD`
  AWS S3 Bucket for file uploads
* `AWS_TRANSCODER_PIPELINE`
  Pipeline ID in AWS Elastic Transcoder that reads from `AWS_S3_BUCKET_NAME_UPLOAD`
* `AWS_TRANSCODER_MP4`
  Job ID in AWS Elastic Transcoder for converting video to MP4 format and JPG thumbnails
* `AWS_TRANSCODER_WEBM`
  Job ID in AWS Elastic Transcoder for converting video to Webm format

## Running Locally

Make sure you have Clojure installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

```sh
$ git clone https://github.com/ykarikos/aws-s3-direct-upload-example.git
$ cd aws-s3-direct-upload-example
$ lein ring server-headless
```

Your app should now be running on [localhost:3000](http://localhost:3000/).

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

or

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Documentation

For more information about using Clojure on Heroku, see these Dev Center articles:

- [Clojure on Heroku](https://devcenter.heroku.com/categories/clojure)

## License

Licensed with [Eclipse Public License v1.0](http://www.eclipse.org/legal/epl-v10.html).
