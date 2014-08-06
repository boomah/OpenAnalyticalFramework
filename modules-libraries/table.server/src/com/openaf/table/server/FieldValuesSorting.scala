package com.openaf.table.server

import com.openaf.table.lib.api.SortOrder._

object FieldValuesSorting {
  def sort[V](data:Array[Int], ordering:Ordering[V], lookUp:Array[V], sortOrder:SortOrder) {
    sort(data, 0, data.length, ordering, lookUp, sortOrder)
  }

  private def sort[V](x:Array[Int], off:Int, len:Int, ordering:Ordering[V], lookUp:Array[V], sortOrder:SortOrder) {
    def compare(left:Int, right:Int) = {
      if (sortOrder == Ascending) {
        ordering.compare(lookUp(left), lookUp(right)) < 0
      } else {
        ordering.compare(lookUp(right), lookUp(left)) < 0
      }
    }
    def swap(a:Int, b:Int) {
      val t = x(a)
      x(a) = x(b)
      x(b) = t
    }
    def vecswap(_a:Int, _b:Int, n:Int) {
      var a = _a
      var b = _b
      var i = 0
      while (i < n) {
        swap(a, b)
        i += 1
        a += 1
        b += 1
      }
    }
    def med3(a:Int, b:Int, c:Int) = {
      if (compare(x(a),x(b))) {
        if (compare(x(b),x(c))) b else if (compare(x(a),x(c))) c else a
      } else {
        if (compare(x(c),x(b))) b else if (compare(x(c),x(a))) c else a
      }
    }
    def sort2(off:Int, len:Int) {
      // Insertion sort on smallest arrays
      if (len < 7) {
        var i = off
        while (i < len + off) {
          var j = i
          while (j>off && compare(x(j), x(j-1))) {
            swap(j, j-1)
            j -= 1
          }
          i += 1
        }
      } else {
        // Choose a partition element, v
        var m = off + (len >> 1)        // Small arrays, middle element
        if (len > 7) {
          var l = off
          var n = off + len - 1
          if (len > 40) {        // Big arrays, pseudomedian of 9
          val s = len / 8
            l = med3(l, l+s, l+2*s)
            m = med3(m-s, m, m+s)
            n = med3(n-2*s, n-s, n)
          }
          m = med3(l, m, n) // Mid-size, med of 3
        }
        val v = x(m)

        // Establish Invariant: v* (<v)* (>v)* v*
        var a = off
        var b = a
        var c = off + len - 1
        var d = c
        var done = false
        while (!done) {
          while (b <= c && compare(x(b),v)) {
            if (x(b) == v) {
              swap(a, b)
              a += 1
            }
            b += 1
          }
          while (c >= b && compare(v,x(c))) {
            if (x(c) == v) {
              swap(c, d)
              d -= 1
            }
            c -= 1
          }
          if (b > c) {
            done = true
          } else {
            swap(b, c)
            c -= 1
            b += 1
          }
        }

        // Swap partition elements back to middle
        val n = off + len
        var s = math.min(a-off, b-a)
        vecswap(off, b-s, s)
        s = math.min(d-c, n-d-1)
        vecswap(b,   n-s, s)

        // Recursively sort non-partition-elements
        s = b - a
        if (s > 1)
          sort2(off, s)
        s = d - c
        if (s > 1)
          sort2(n-s, s)
      }
    }
    sort2(off, len)
  }
}
