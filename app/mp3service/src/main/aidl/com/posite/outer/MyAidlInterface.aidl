// MyAidlInterface.aidl
package com.posite.outer;

// build -> make module com.xxx 필수!!

interface MyAidlInterface {
   int getMaxDuration();
   void start();
   void pause();
}