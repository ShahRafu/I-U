cmake_minimum_required(VERSION 3.18.1)

project("pikachubridge")

add_library(
        pikachubridge
        SHARED
        PikachuBridgeNative.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        pikachubridge
        ${log-lib})
