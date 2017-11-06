package com.mbcu.hda.client

object Utils {

  def requiredProperty(prop: String): String = {
    System.getProperty(prop);
  }
}