/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.quadtree;

import java.util.Iterator;
import wjd.teutoburg.collision.Collider;

/**
 *
 * @author wdyce
 * @since Dec 13, 2012
 */
public class QTContentsIterator implements Iterator<Collider>
{
  /* ATTRIBUTES */
  private final QTNode container;
  private int object_i = 0;

  /* METHODS */
  
  // constructors
  QTContentsIterator(QTNode parent_)
  {
    this.container = parent_;
  }
  
  /* IMPLEMENTS -- ITERATOR<PHYSICAL> */
  
  @Override
  public boolean hasNext()
  {
    return (object_i < container.getNObjects());
  }

  @Override
  public Collider next()
  {
    return container.getObject(object_i++);
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("Remove is not supported.");
  }
}
