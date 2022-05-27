package matt.kjlib.weak.bag

/*
 * Copyright (c) 1998 - 2005 Versant Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Versant Corporation - initial API and implementation
 */


import matt.klib.lang.void
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * This maintains a bag of weakly referenced objects. The clean method
 * must be called from time to time to get rid of the objects that the
 * garbage collector wants to nuke. This class is not synchronized.
 */
class WeakBag<T> {
  private val set: HashSet<WeakReference<T>> = HashSet<WeakReference<T>>()
  private val refQueue: ReferenceQueue<T> = ReferenceQueue<T>()


  operator fun plusAssign(o: T) = set.add(WeakReference(o, refQueue)).void
  operator fun minusAssign(o: Reference<*>?) = set.remove(o).void
  operator fun minusAssign(o: Any) = set.removeAll { it.get() == o }.void
  fun approxSize() = set.size
  fun values() = set.mapNotNull { it.get() }
  operator fun contains(v: Any) = v in values()

  /**
   * Get rid of objects in the bag that the garbage collector wants to
   * nuke. This does not block.
   */
  fun clean() {
	while (true) {
	  val r = refQueue.poll() ?: return
	  set.remove(r)
	}
  }


}