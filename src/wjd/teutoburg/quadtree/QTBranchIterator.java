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

/**
 * Spools through the 4 children of a Quad Tree node.
 *
 * @author wdyce
 * @since Dec 13, 2012
 */
public class QTBranchIterator implements Iterator<QTNode>
{
  /* ATTRIBUTES */
  private final QTNode parent;
  private int child_i = 0;

  /* METHODS */
  
  // constructors
  QTBranchIterator(QTNode parent_)
  {
    this.parent = parent_;
  }
  
  /* IMPLEMENTS -- ITERATOR<QUADNODE> */
  
  @Override
  public boolean hasNext()
  {
    return (child_i < 4);
  }

  @Override
  public QTNode next()
  {
    return parent.getChildTree(child_i++);
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("Remove is not supported.");
  }
}
