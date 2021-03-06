
Bombyx3D Build System
=====================

<a href="https://travis-ci.org/bombyx3d/buildsystem" target="_blank">
 <img src="https://travis-ci.org/bombyx3d/buildsystem.svg?branch=master" alt="Build Status" />
</a>

Stable releases
---------------

Latest stable binary of the build system is available on the
[GitHub releases page](https://github.com/bombyx3d/buildsystem/releases).

Building from sources
---------------------

If you want to build the latest version from sources, you will need to download and install the following software:

  * [Java SE Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (version 8 or later)
  * [Apache ANT](http://ant.apache.org/)

Invoke `ant` in the root directory of the project. It will compile the sources and create file `bin/buildsystem.jar`.

It is also recommended to run tests after compilation. This can be achieved by invoking the `ant run-tests` command
in the root directory of the project.

Projects files for [IntelliJ IDEA](https://www.jetbrains.com/idea/) are also included.

License
-------

Copyright (c) 2015 Nikolay Zapolnov (zapolnov@gmail.com).

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
