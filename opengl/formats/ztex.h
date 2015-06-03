﻿/*
 * Copyright (c) 2015 Nikolay Zapolnov (zapolnov@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#pragma once
#include <cstdint>
#include <cstdio>

#pragma pack(push, 1)

enum : uint32_t
{
    ZTEX_MAGIC = 0x1A584554,

    // Flags
    ZTEXFLAG_CUBEMAP = 0x00000001,

    // Targets
    ZTEX_TEXTURE_2D = 0x0DE1,
    ZTEX_TEXTURE_3D = 0x806F,
    ZTEX_TEXTURE_2D_ARRAY = 0x8C1A,
    ZTEX_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515,
    ZTEX_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516,
    ZTEX_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517,
    ZTEX_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518,
    ZTEX_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519,
    ZTEX_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A,

    // Types
    ZTEX_BYTE = 0x1400,
    ZTEX_UNSIGNED_BYTE = 0x1401,
    ZTEX_SHORT = 0x1402,
    ZTEX_UNSIGNED_SHORT = 0x1403,
    ZTEX_INT = 0x1404,
    ZTEX_UNSIGNED_INT = 0x1405,
    ZTEX_FLOAT = 0x1406,
    ZTEX_UNSIGNED_SHORT_4_4_4_4 = 0x8033,
    ZTEX_UNSIGNED_SHORT_5_5_5_1 = 0x8034,
    ZTEX_UNSIGNED_SHORT_5_6_5 = 0x8363,

    // Formats
    ZTEX_ALPHA = 0x1906,
    ZTEX_RGB = 0x1907,
    ZTEX_RGBA = 0x1908,
    ZTEX_LUMINANCE = 0x1909,
    ZTEX_LUMINANCE_ALPHA = 0x190A,
    // ETC1
    ZTEX_ETC1_RGB8 = 0x8D64,
    // ETC2
    ZTEX_COMPRESSED_R11_EAC = 0x9270,
    ZTEX_COMPRESSED_SIGNED_R11_EAC = 0x9271,
    ZTEX_COMPRESSED_RG11_EAC = 0x9272,
    ZTEX_COMPRESSED_SIGNED_RG11_EAC = 0x9273,
    ZTEX_COMPRESSED_RGB8_ETC2 = 0x9274,
    ZTEX_COMPRESSED_SRGB8_ETC2 = 0x9275,
    ZTEX_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9276,
    ZTEX_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9277,
    ZTEX_COMPRESSED_RGBA8_ETC2_EAC = 0x9278,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 0x9279,
    // BPTC
    ZTEX_COMPRESSED_RGBA_BPTC_UNORM = 0x8E8C,
    ZTEX_COMPRESSED_SRGB_ALPHA_BPTC_UNORM = 0x8E8D,
    ZTEX_COMPRESSED_RGB_BPTC_SIGNED_FLOAT = 0x8E8E,
    ZTEX_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT = 0x8E8F,
    // FXT1
    ZTEX_COMPRESSED_RGB_FXT1 = 0x86B0,
    ZTEX_COMPRESSED_RGBA_FXT1 = 0x86B1,
    // RGTC
    ZTEX_COMPRESSED_RED_RGTC1 = 0x8DBB,
    ZTEX_COMPRESSED_SIGNED_RED_RGTC1 = 0x8DBC,
    ZTEX_COMPRESSED_RED_GREEN_RGTC2 = 0x8DBD,
    ZTEX_COMPRESSED_SIGNED_RED_GREEN_RGTC2 = 0x8DBE,
    // LATC
    ZTEX_COMPRESSED_LUMINANCE_LATC1 = 0x8C70,
    ZTEX_COMPRESSED_SIGNED_LUMINANCE_LATC1 = 0x8C71,
    ZTEX_COMPRESSED_LUMINANCE_ALPHA_LATC2 = 0x8C72,
    ZTEX_COMPRESSED_SIGNED_LUMINANCE_ALPHA_LATC2 = 0x8C73,
    // PVRTC
    ZTEX_COMPRESSED_RGB_PVRTC_4BPPV1 = 0x8C00,
    ZTEX_COMPRESSED_RGB_PVRTC_2BPPV1 = 0x8C01,
    ZTEX_COMPRESSED_RGBA_PVRTC_4BPPV1 = 0x8C02,
    ZTEX_COMPRESSED_RGBA_PVRTC_2BPPV1 = 0x8C03,
    // S3TC
    ZTEX_COMPRESSED_RGB_S3TC_DXT1 = 0x83F0,
    ZTEX_COMPRESSED_RGBA_S3TC_DXT1 = 0x83F1,
    ZTEX_COMPRESSED_RGBA_S3TC_DXT3 = 0x83F2,
    ZTEX_COMPRESSED_RGBA_S3TC_DXT5 = 0x83F3,
    // ASTC
    ZTEX_COMPRESSED_RGBA_ASTC_4x4 = 0x93B0,
    ZTEX_COMPRESSED_RGBA_ASTC_5x4 = 0x93B1,
    ZTEX_COMPRESSED_RGBA_ASTC_5x5 = 0x93B2,
    ZTEX_COMPRESSED_RGBA_ASTC_6x5 = 0x93B3,
    ZTEX_COMPRESSED_RGBA_ASTC_6x6 = 0x93B4,
    ZTEX_COMPRESSED_RGBA_ASTC_8x5 = 0x93B5,
    ZTEX_COMPRESSED_RGBA_ASTC_8x6 = 0x93B6,
    ZTEX_COMPRESSED_RGBA_ASTC_8x8 = 0x93B7,
    ZTEX_COMPRESSED_RGBA_ASTC_10x5 = 0x93B8,
    ZTEX_COMPRESSED_RGBA_ASTC_10x6 = 0x93B9,
    ZTEX_COMPRESSED_RGBA_ASTC_10x8 = 0x93BA,
    ZTEX_COMPRESSED_RGBA_ASTC_10x10 = 0x93BB,
    ZTEX_COMPRESSED_RGBA_ASTC_12x10 = 0x93BC,
    ZTEX_COMPRESSED_RGBA_ASTC_12x12 = 0x93BD,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4 = 0x93D0,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_5x4 = 0x93D1,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_5x5 = 0x93D2,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_6x5 = 0x93D3,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_6x6 = 0x93D4,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_8x5 = 0x93D5,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_8x6 = 0x93D6,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_8x8 = 0x93D7,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_10x5 = 0x93D8,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_10x6 = 0x93D9,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_10x8 = 0x93DA,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_10x10 = 0x93DB,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_12x10 = 0x93DC,
    ZTEX_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12 = 0x93DD,
    // Offline compression
    ZTEX_COMPRESSED_JPEG_RGB = 0xFFFF0001,
};

struct ZTexMipmapFormat
{
    uint32_t format;
    uint32_t internalFormat;
    uint32_t type;
    uint32_t dataSize;
    union {
        uint8_t* data;
        uint64_t dataOffset;
    };
};

struct ZTexMipmap
{
    uint32_t width;
    uint32_t height;
    uint32_t depth;
    uint32_t numFormats;
    union {
        ZTexMipmapFormat* formats;
        uint64_t formatsOffset;
    };
};

struct ZTexFace
{
    uint32_t numMipmaps;
    uint32_t target;
    union {
        ZTexMipmap* mipmaps;
        uint64_t mipmapsOffset;
    };
};

struct ZTexHeader
{
    uint32_t magic;
    uint32_t flags;
    union {
        ZTexFace* faces;
        uint64_t facesOffset;
    };
};

#pragma pack(pop)