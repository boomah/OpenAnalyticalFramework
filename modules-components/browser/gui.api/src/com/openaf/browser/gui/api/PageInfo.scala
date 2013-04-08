package com.openaf.browser.gui.api

import ref.SoftReference
import com.openaf.pagemanager.api.Page

case class PageInfo(page:Page, softPageResponse:SoftReference[PageResponse])