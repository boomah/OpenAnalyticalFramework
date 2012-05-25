package com.openaf.browser

import com.google.common.cache.CacheBuilder


class PageBuilder {
  private val cb = CacheBuilder.newBuilder()

  def build(page:Page):PageData = {
    page.build
  }
}