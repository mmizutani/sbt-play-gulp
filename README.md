# SBT Play Gulp Plugin
> Gulp Asset Pipeline for Play Framework

**SBT Play Gulp Plugin** is an SBT plugin which allows you to use Gulp for static assets compilation in Play Framework projects.

## Features

This plugin allows you to:
- Automatically run various user-defined gulp tasks such as JavaScript obfuscation, CSS concatenation and CDNification on the `compile`, `run`, `stage`, `dist` and `clean` stages.
- Manually run the npm, bower and gulp commands inside the Play sbt console.

## For Whom and Why Gulp not Grunt

This plugin is assumed to be mainly for those who have been familiar with Gulp and would like to utilize Gulp instead of the official web-jar ecosystem for static asset compilation in Play Framework. Play Gulp Plugin is largely a modification of the [play-yeoman plugin](https://github.com/tuplejump/play-yeoman), which uses Grunt rather than Gulp. I created this custom plugin after having found that Gulp configuration is more streamlined and easier to use compared with Grunt.

## How to Use

1. Install npm, bower and gulp if you do not have them yet.

2. If you do not have any existing Play project, create a plain one like the play-scala template and specify in `<your-project-root>/project/build.properties` the sbt version `sbt.version=0.13.5`, which needs to be 0.13.5 or higher.

3. Add the play gulp plugin to the `<your-project-root>/project/plugins.sbt` file along with the play sbt plugin `addSbtPlugin("com.typesafe.play" % "sbt-plugin" % ${playVersion})` and let the project depend on the play gulp plugin:
  ```
  addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")

  addSbtPlugin("com.github.mmizutani" % "sbt-play-gulp" % "0.0.1")
  ```

4. Add the following routing settings in the `<your-project-root>/conf/routes` file:
  ```
  GET     /ui         com.github.mmizutani.playgulp.Gulp.index

  ->      /ui/        gulp.Routes
  ```

5. Create an `<your-project-root>/ui` folder in the project top directory and inside of it create `package.json`, `bower.json` and `gulpfile.js` files.

6. Edit `package.json`, `bower.json` and `gulpfile.js`, enter the play-sbt console, and install public libraries:
  ```bash
  $ sbt (or activator)
  [your-play-project] $ npm install
  [your-play-project] $ bower install
  ```

7. Create HTML/JavaScript/CSS files in the `<your-project-root>/` diretory.

8. Compile and run the Play project:
  ```bash
  $ sbt
  [your-play-project] $ compile
  Will run: [gulp, --gulpfile=gulpfile.js, --force] in /home/path/to/your/play/project/ui
  ...
  [your-play-project] $ run
  ```
  You will see the compiled app at http://localhost:9000/, which is redirected to http://localhost:9000/ui/ serving static web assets located in the ui/app directory in the dev run mode and in the ui/dist directory in the production start mode.


## How This Works
With this plugin, play-sbt build lifecycle triggers the corresponding gulp tasks:

| SBT Commands     |    | Gulp Tasks   |
| ---------------- | -- | ------------ |
| `sbt gulp <arg>` | => | `gulp <arg>` |
| `sbt run`        | => | `gulp watch` |
| `sbt compile`    | => | `gulp build` |
| `sbt stage`      | => | `gulp build` |
| `sbt dist`       | => | `gulp build` |
| `sbt clean`      | => | `gulp clean` |

So make sure that the gulpfile.js in the `ui` directory of your Play project implements the `watch`, `build` and `clean` tasks.

Built upon SBT's [auto plugin](http://www.scala-sbt.org/0.13/docs/Plugins.html) architecture, the Play Gulp plugin adds itself automatically to projects that have the sbt-play plugin enabled once you add it in `project/plugins.sbt`. It is not necessary to manually add `enablePlugins(PlayGulpPlugin)` to `build.sbt` of your Play project.

When compilation or testing takes place, the `PlayGulpPlugin` runs all required tasks on your Play projects and copies the processed files into the web assets jar (`target/scala-2.11/<play-project-name>_2.11-x.x.x-web-assets.jar/META-INF/webjars/x.x.x/***`).


## Demo
To see the plugin in action and how to configure the gulpfile.js, please see and run this Gulp-enabled [example Play application](https://github.com/mmizutani/play-gulp-demo).
