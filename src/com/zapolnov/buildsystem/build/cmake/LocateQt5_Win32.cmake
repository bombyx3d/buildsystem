#
# Copyright (c) 2015 Nikolay Zapolnov (zapolnov@gmail.com).
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

message(STATUS "Looking for Qt5")

set(found_versions)
foreach(root "C:/Qt" "C:/Qt5")

    file(GLOB files RELATIVE "${root}" "${root}/*")
    foreach(dir ${files})

        if(NOT IS_DIRECTORY "${root}/${dir}")
            continue()
        endif()

        if(NOT "${dir}" MATCHES "^5(\\.[0-9]+(\\.[0-9]+(\\.[0-9]+)?)?)?$")
            continue()
        endif()

        if(MINGW)
            if(CMAKE_SIZEOF_VOID_P EQUAL 8)
                set(pattern "^mingw([0-9]+)_64(_opengl)?$")
            else()
                set(pattern "^mingw([0-9]+)_32(_opengl)?$")
            endif()
        elseif(MSVC14 OR MSVC12 OR MSVC11)
            if(MSVC14)
                set(version "2015")
            elif(MSVC12)
                set(version "2013")
            elif(MSVC11)
                set(version "2012")
            endif()

            if(CMAKE_SIZEOF_VOID_P EQUAL 8)
                set(pattern "^msvc${version}([0-9]+)_64(_opengl)?$")
            else()
                set(pattern "^msvc${version}([0-9]+)(_opengl)?$")
            endif()
        else()
            continue()
        endif()

        file(GLOB subdirs RELATIVE "${root}/${dir}" "${root}/${dir}/*")
        foreach(subdir ${subdirs})

            if(NOT IS_DIRECTORY "${root}/${dir}/${subdir}")
                continue()
            endif()

            if(NOT subdir MATCHES "${pattern}")
                continue()
            endif()

            if(EXISTS "${root}/${dir}/${subdir}/lib/cmake/Qt5/Qt5Config.cmake")
                list(APPEND found_versions "${dir}")
                list(APPEND found_versions "${root}/${dir}/${subdir}")
            endif()

        endforeach()
    endforeach()
endforeach()

list(LENGTH found_versions count)
if(count GREATER 0)
    set(max_version "0.0")
    set(max_version_path)

    set(index 0)
    while(index LESS count)
        list(GET found_versions "${index}" version)
        math(EXPR index "${index} + 1")
        list(GET found_versions "${index}" path)
        math(EXPR index "${index} + 1")

        if("${version}" VERSION_GREATER "${max_version}")
            set(max_version "${version}")
            set(max_version_path "${path}")
        endif()
    endwhile()

    message(STATUS "Looking for Qt5 - found version ${max_version}")
    set(CMAKE_PREFIX_PATH "${max_version_path}" CACHE PATH "Path to Qt5" FORCE)
endif()
