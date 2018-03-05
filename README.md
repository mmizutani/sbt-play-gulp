# SBT Play Gulp Plugin
> Gulp Asset Pipeline for Play Framework

[![Build Status](https://travis-ci.org/mmizutani/sbt-play-gulp.svg?branch=master)](https://travis-ci.org/mmizutani/sbt-play-gulp)

**sbt-play-gulp** is an SBT plugin to use Gulp for static assets compilation in Play Framework projects.

If you do not like your Play app to depend on any sbt plugin, [play-gulp-standalone](https://github.com/mmizutani/play-gulp-standalone) might be a better fit for you.


## Change logs

[Bintray Releases](https://bintray.com/mmizutani/sbt-plugins/sbt-play-gulp/#release)
* v0.2.0 Upgraded for Play 2.6, Scala 2.12 and sbt 1.x., moved from Sonatype to Bintray
* v0.1.5 Fixed gulp.js resolution error on Windows.
* v0.1.3 Reworked the devAssetHandler so that it no longer returns a Content-Disposition header. Kudos to @jeantil for the pull request [#22](/sbt-play-gulp/pull/22), [#23](/sbt-play-gulp/pull/23).
         Added the [`yarn`](https://github.com/yarnpkg/yarn) command. (You can run the yarn package manager in the `/ui` directory from inside the sbt console.)
* v0.1.2 Added better gulp detection mechanism and a mitigating lock on hooks. Thanks @mriehl for the pull requests #17 and #18.
* v0.1.1 Fixed various errors.
* v0.1.0 Added support for Play 2.5 and dropped support for Scala 2.10.
* v0.0.7 Added jspm command - You can now execute jspm in the sbt console.
* v0.0.6 Bumped up the Play sbt plugin version from 2.4.2 to 2.4.3.
* v0.0.5 Fixed a bug concerning the path to the compiled static assets.


## Version compatibility

| Plugin version | Play version | Scala version | sbt version |
|----------------|--------------|---------------|-------------|
| 0.2.x          | 2.6.6-       | 2.12.x        | 1.x         |
|                | 2.6.0-5      | 2.12.x        | 0.13.15-    |
| 0.1.x          | 2.5.x        | 2.11.x        | 0.13.0-14   |
| - 0.0.7        | 2.4.x        | 2.10.x/2.11.x | 0.13.0-14   |


## Features

This SBT plugin allows you to:
- Automatically run various user-defined gulp tasks such as JavaScript obfuscation, CSS concatenation and CDNification on the `compile`, `run`, `stage`, `dist` and `clean` stages.
- Manually run the npm, bower and gulp commands on the Play sbt console.


## Demos

To see the plugin in action and how to configure the gulpfile.js, please see and run the sample Play projects
* [play-gulp-react](https://github.com/mmizutani/sbt-play-gulp/tree/master/samples/play-gulp-react)

in the samples directory of this repository.


## For whom and why Gulp not Grunt

This plugin is assumed to be mainly for those who have been familiar with Gulp and would like to utilize Gulp instead of the official web-jar ecosystem for static asset compilation in Play Framework. Play Gulp Plugin is largely a modification of the [play-yeoman plugin](https://github.com/tuplejump/play-yeoman), which uses Grunt rather than Gulp. I created this custom plugin after having found that Gulp configuration is more streamlined and easier to use compared with Grunt.


## How to use this sbt plugin

1. Install npm and other prerequisites:

    ```bash
    $ npm install -g yo gulp-cli bower
    ```

2. If you do not have any existing Play project, create a plain one like the play-scala template and specify in `<your-project-root>/project/build.properties` the sbt version as `sbt.version=1.1.1`.

3. Add the play gulp plugin to the `<your-project-root>/project/plugins.sbt` file along with the play sbt plugin `addSbtPlugin("com.typesafe.play" % "sbt-plugin" % ${playVersion})` and also import the sbt-play-gulp plugin of this repository:

    ```
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.10")
    addSbtPlugin("com.github.mmizutani" % "sbt-play-gulp" % "0.2.0")
    ```

4. Add settings specific to the sbt-play-gulp plugin in build.sbt:

    ```bash
    import com.github.mmizutani.sbt.gulp.PlayGulpPlugin
    ```

5. Create an `<your-project-root>/ui` folder and populate the Yeoman frontend template of your choice in the ui directory:

    ```bash
    $ mkdir ui
    $ cd $_
    $ npm install -g generator-fountain-webapp
    $ yo fountain-webapp
    ```

6. Move bower_components directory, if present, from ui to ui/src and adjust the paths in two config files accordingly:

    ```bash
    $ cd ui
    $ mv bower_components src
    $ sed -e 's/"bower_components"/"src\/bower_components"/' .bowerrc
    $ sed -e 's/\x27bower_components\x27/\x27src\/bower_components\x27/' gulp/conf.js
    ```

    This adjustment is necessary since the root directory of static assets is ui/src in the development and test modes of a Play application, which does not allow us to serve files in directories higher than the ui/src (e.g., ui/bower_components). This step is not necessary if you generate the frontend project in ui using Yeoman Fountain generators.

7. Make sure that this plugin recognizes the correct source and output directory paths of your Gulp frontend project.

    By default, the sbt-play-gulp plugin assumes that frontend static asset files reside in `ui/.tmp.serve`, `ui/src` or `ui` directories, a behavior specifically tailored for the Yeoman Gulp-Angular template project. In development and test modes the playgulp.GulpAssets handler looks for frontend static files in ui/.tmp/serve directory first. If those files were not found there, the asset handler next tries ui/src directory and then the ui directory. If your frontend project under the ui directory is based on a different Yeoman template and thus serves files from a diffent folder (e.g., `ui/app`), you can customize this behavior by overriding the default values of the devDirs array in `conf/application.conf` of your Play project:

    ```
    #gulp.devDirs=["ui/.tmp/serve", "ui/src", "ui"]
    gulp.devDirs=["ui/app"]
    ```

    For production build, this plugin assumes by default that the frontend files are compiled into the `ui/dist` directory and that the contents of the `ui/dist` are packaged into the application classpath. If Gulp for your frontend project uses a different folder (e.g., `ui/build`) for output, you can override this setting as well by adding the following lines to `build.sbt` or `project/Build.scala`:

    ```scala
    import PlayGulpPlugin._
    import PlayGulpKeys._
    ...
    unmanagedResourceDirectories in Assets <+= (gulpDirectory in Compile)(base => base / "build")
    ```

    This adds `ui/build` to the list of directories from which SBT packs non-Java/non-Scala static files into application jars.

    Likewise, if the source (pre-build) files of your frontend project are located in a directory other than `ui/src` (e.g., `ui/app`) and you want Play to compile twirl scala templates in the directory, you should add the following to `build.sbt` or `project/Build.scala`:

    ```scala
    import PlayGulpPlugin._
    import PlayGulpKeys._
    ...
    sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(gulpDirectory.value / "app")
    ```

8. Add the following routing settings in the `<your-project-root>/conf/routes` file:

    ```
    ->      /        gulp.Routes
    ```

9. Edit `package.json`, `bower.json` and `gulpfile.js`, enter the play-sbt console, and install public libraries:

    ```bash
    $ sbt
    [your-play-project] $ npm install
    [your-play-project] $ bower install
    ```

10. Tweak the frontend html/javascript/css project template in the `<your-project-root>/ui/src` diretory as you like.

11. Now you can compile and run your Play project with the frontend managed by Gulp:

    ```bash
    $ sbt
    [your-play-project] $ update
    [your-play-project] $ compile
    Will run: [gulp, --gulpfile=gulpfile.js, --force] in path/to/your/play/project/ui
    [your-play-project] $ run
    ```

    In the background, the Gulp taskrunner builds and packages your frontend part all the way through this Play app workflow.

    You will see the compiled frontend app at http://localhost:9000/, which is redirected to http://localhost:9000/ui/ serving static web assets located in the `ui/src directory` in the development mode and in the `ui/dist` directory in the production mode (the paths might differ if you customizes them in 7. above).


12. You can also test and package the Play app along with the compiled frontend assets:

    ```bash
    [your-play-project] > test
    [your-play-project] > testProd
    [your-play-project] > ;clean;stage;dist
    ```

## How this works

With this plugin, play-sbt build lifecycle triggers the corresponding gulp tasks:

SBT Commands       | Gulp Tasks
------------------ | ------------
`sbt "gulp <arg>"` | `gulp <arg>`
`sbt run`          | `gulp watch`
`sbt compile`      | `gulp build`
`sbt stage`        | `gulp build`
`sbt dist`         | `gulp build`
`sbt clean`        | `gulp clean`

So make sure that the gulpfile.js in the `ui` directory of your Play project implements the `watch`, `build` and `clean` tasks.

Built upon SBT's [auto plugin](https://www.scala-sbt.org/1.x/docs/Plugins.html#Using+an+auto+plugin) architecture, the Play Gulp plugin adds itself automatically to projects that have the sbt-play plugin enabled once you add it in `project/plugins.sbt`. It is not necessary to manually add `enablePlugins(PlayGulpPlugin)` to `build.sbt` of your Play project.

When compilation or testing takes place, the `PlayGulpPlugin` runs all the required tasks on your Play projects and copies the processed files into the web assets jar (`target/scala-2.12/<play-project-name>_2.12-x.x.x-web-assets.jar/META-INF/webjars/x.x.x/***`).


## For developers

```bash
$ echo 'version in ThisBuild := "x.x.x"' > ./version.sbt
$ vi src/main/scala/com/github/mmizutani/sbt/gulp/PlayGulpPlugin.scala
libraryDependencies += "com.github.mmizutani" %% "play-gulp" % "x.x.x" exclude("com.typesafe.play", "play"),
$ sbt ";project play-gulp;scripted;publish"
$ sbt ";project sbt-play-gulp;scripted;publish"
```
