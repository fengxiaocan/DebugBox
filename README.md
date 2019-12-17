# DebugBox
调试工具箱

一个调试工具箱,可以调试UI,抓包,拦截浏览器,查看沙盒文件等

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

gradle
maven
sbt
leiningen
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.fengxiaocan:DebugBox:0.0.1'
	}
